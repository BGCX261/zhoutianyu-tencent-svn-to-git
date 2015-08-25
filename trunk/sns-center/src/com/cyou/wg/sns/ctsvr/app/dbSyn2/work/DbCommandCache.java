package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommandList;
import com.cyou.wg.sns.ctsvr.core.log.LogFactory;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.gs.core.dataSource.DataSourceInit;
import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.io.*;
import java.util.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.mina.core.buffer.IoBuffer;
import org.dom4j.DocumentException;

public class DbCommandCache {

	public static BasicDataSource dataSource;
	public static WriteThread2 threads[];
	public static Object commandListLock = new Object();
	public static List<DbCommandList> commandList = new ArrayList<DbCommandList>();
	public static int dbCommandNum = 0;
	public static final int MAX_DBCOMMAND_NUM_CACHE = 20000;

	public static void init(int id) throws DocumentException, IOException {
		dataSource = DataSourceInit.init(new File("conf/DbSyn/dataSource.xml"), "master", id);
		threads = new WriteThread2[CenterServerConfig.dbSynServer[id - 1].getWorkThreadNum()];
		for (int i = 0; i < threads.length; i++) {
			initThread(i);
		}
		initFileThread();
		initDbHeartbeatThread();
	}

	public static void initThread(int id) {
		WriteThread2 writeThread = new WriteThread2(dataSource, id, CenterServerConfig.dbSynServer[0].getInterval());
		writeThread.setDaemon(true);
		writeThread.setName("DbSynWriteThread-"+id);
		writeThread.start();
		threads[id] = writeThread;
	}

	public static void initFileThread() throws IOException {
		//设置生成文件目录
		FileUtil.DB_FILE_PATH = CenterServerConfig.dbSynServer[0].getTmpFielPath();
		//重命名留存的.waiting文件到.wait
		File[] fileList = new File(FileUtil.DB_FILE_PATH).listFiles();
		for(File file:fileList) {
			if(file.getName().endsWith(FileUtil.FILENAME_SUFFIX_WRITING)) {
				FileUtil.changeFileSuffix(file, FileUtil.FILENAME_SUFFIX_WAIT);
			}
		}
		
		WriteDbCommand2FileThread cw = new WriteDbCommand2FileThread(CenterServerConfig.dbSynServer[0].getCreateFileInterval());
		cw.setDaemon(true);
		cw.setName("DbCommandWrite2FileThread");
		cw.start();
		
		Runtime.getRuntime().addShutdownHook(new ShutDownThread(cw));
		
		ReadDbCommandThread rd = new ReadDbCommandThread(CenterServerConfig.dbSynServer[0].getReadFileInterval());
		rd.setDaemon(true);
		rd.setName("DbCommandReadThread");
		rd.start();
		
		FileDelThread fd = new FileDelThread(CenterServerConfig.dbSynServer[0].getDelFileInterval());
		fd.setDaemon(true);
		fd.setName("FileDelThread");
		fd.start();
	}

	public static void initDbHeartbeatThread() {
		DbHeartbeatThread dht = new DbHeartbeatThread(dataSource);
		dht.setDaemon(true);
		dht.setName("DbHeartbeatThread");
		dht.start();
	}

	public static void addCommand(byte src[]) {
		DbCommandList dbCommandList = new DbCommandList();
		try {
			dbCommandList.decode(IoBuffer.wrap(src));
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库更新命令解码错误", e);
			return;
		}
		synchronized (commandListLock) {
			commandList.add(dbCommandList);
		}
	}
	
	static class WriteDbCommand2FileThread extends Thread {

		public static int waitTime = 10000;
		private int createFileInterval = 10 * 1000;
		private File file;
		private FileOutputStream outStream;
		private long lasttime;

		private void initNewFile() throws IOException {
			lasttime = DateUtil.currentTimeMillis();
			file = FileUtil.createNewFile();
			outStream = new FileOutputStream(file);
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("开始新的文件写入："+file.getName());
		}

		public void resetFile() throws IOException {
			if (file != null) {
				synchronized (file) {
					outStream.close();
					FileUtil.changeFileSuffix(file, FileUtil.FILENAME_SUFFIX_WAIT);
					LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName()+"文件写入完毕："+file.getName());
				}
				file = null;
				outStream = null;
			}
		}

		public void run() {
			 while (true) {
				List<DbCommandList> tList = null;
				try {
					synchronized (commandListLock) {
						tList = commandList;
						commandList = new ArrayList<DbCommandList>();
						dbCommandNum = 0;
					}
					long start = DateUtil.currentTimeMillis();
					if (tList != null && tList.size() > 0) {
						sort(tList);
						if (file == null) {
							initNewFile();
						}
						synchronized (file) {
							for (int i = 0; i < tList.size(); i++) {
								List<DbCommand> list = ((DbCommandList) tList.get(i)).getList();
								for (int j = 0; j < list.size(); j++)
									FileUtil.writeDbCommand2File((DbCommand) list.get(j), outStream);
							}
							outStream.flush();
						}
						LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName()+"一次写入文件成功："+file.getName()+"本次写入用时"+(DateUtil.currentTimeMillis() - start));
						tList = null;
					}
					if ( (start - lasttime) > createFileInterval) {
						resetFile();
					}
					Thread.sleep(waitTime);
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("吧db更新命令写入文件异常",e);
				}
			}
		}

		public void sort(List<DbCommandList> list) {
			
			Collections.sort(list, new Comparator<DbCommandList>() {
				
				@Override
				public int compare(DbCommandList o1, DbCommandList o2) {
					return (int) (o1.getSendTime() - o2.getSendTime());
				}

			});
		}

		public WriteDbCommand2FileThread(int createFileInterval) {
			this.createFileInterval = createFileInterval * 1000;
		}
	}

}
