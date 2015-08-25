package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import org.apache.mina.core.buffer.IoBuffer;

public class ReadDbCommandThread extends Thread {

	public int waitTime = 10000;
	private List<DbCommand> commandList = new ArrayList<DbCommand>();
	private File file;
	private int buffLen = 1024 * 512;
	private byte buff[] = new byte[buffLen];
	private byte leftBuff[] = null;

	public ReadDbCommandThread(int interval) {
		waitTime = interval * 1000;
	}

	public void run() {
		File baseFile = FileUtil.getDbFile();
		while (true) {
			try {
				File files[] = FileUtil.getDelFileList(baseFile, FileUtil.FILENAME_SUFFIX_WAIT);
				if (files != null && files.length > 0) {
					Arrays.sort(files);
					for(File f : files) {
						file = f;
						readFile();
						dispathDbCommand();
						checkWriteStatus();
						resetFile();
					}
				}
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("文件读取线程文件读取失败：" + getName(), e);
			}
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("文件读取线程暂停失败：" + getName(), e);
			}
		}
	}

	public void readFile() throws Exception {
		if (file != null) {
			long start = DateUtil.currentTimeMillis();
			FileInputStream input = new FileInputStream(file);
			int byteread = 0;
			while((byteread = input.read(buff)) != -1) {
				parseCommand(byteread);
			}
			input.close();
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName() + "读取文件成功：" + file.getName() + "读取文件用时" + (DateUtil.currentTimeMillis() - start));
		}
	}

	public void resetFile() {
		FileUtil.changeFileSuffix(file, FileUtil.FILENAME_SUFFIX_DEL);
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(file.getName() + "文件指令执行完毕：");
	}

	public void parseCommand(int read) throws Exception {
		if (read > 1) {
			int size = read;
			byte data[];
			if (leftBuff != null && leftBuff.length > 0) {
				size = read + leftBuff.length;
				data = new byte[size];
				System.arraycopy(leftBuff, 0, data, 0, leftBuff.length);
				System.arraycopy(buff, 0, data, leftBuff.length, read);
				leftBuff = null;
			} else {
				data = buff;
			}
			int length;
			for (int i = 0; i < size; i += length + 2) {
				if (i + 1 >= size) {
					leftBuff = new byte[size - i];
					System.arraycopy(data, i, leftBuff, 0, leftBuff.length);
					return;
				}
				length = FileUtil.toInt(data[i], data[i + 1]);
				if (i + 2 + length > size) {
					leftBuff = new byte[size - i];
					System.arraycopy(data, i, leftBuff, 0, leftBuff.length);
					return;
				}
				byte src[] = new byte[length];
				System.arraycopy(data, i + 2, src, 0, length);
				IoBuffer buf = IoBuffer.wrap(src);
				DbCommand cmd = new DbCommand();
				cmd.decode(buf);
				commandList.add(cmd);
				if (commandList.size() > 10000) {
					dispathDbCommand();
				}
			}
		}
	}

	private void dispathDbCommand() {
		if (commandList.size() > 0) {
			List<DbCommand> pathDbList = commandList;
			commandList = new ArrayList<DbCommand>();
			for(DbCommand cmd : pathDbList) {
				DbCommandCache.threads[Math.abs(cmd.getObjectType()) % DbCommandCache.threads.length].add(cmd);
			}
		}
	}

	private void checkWriteStatus() {
		boolean flag = true;
		do {
			flag = true;
			try {
				Thread.sleep(1000L);
			} catch (Exception e) {
				
			}
			for(WriteThread2 wt : DbCommandCache.threads) {
				if (wt.getWriteStatus() == WriteThread2.WRITE_STATUS_PROCESSING) {
					flag = false;
				}
			}
		} while (!flag);
	}

}
