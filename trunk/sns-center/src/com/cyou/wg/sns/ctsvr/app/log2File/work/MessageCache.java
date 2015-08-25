package com.cyou.wg.sns.ctsvr.app.log2File.work;

import com.cyou.wg.sns.ctsvr.app.log2File.messageHandler.LogicalLogList;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.io.*;
import java.util.*;
import org.apache.mina.core.buffer.IoBuffer;

public class MessageCache {

	private static List<String> cachedList = new ArrayList<String>();
	private static Object lock = new Object();
	private static BufferedWriter bfw = null;
	private static String currFileName = null;

	public static void addMessage(byte message[]) throws Exception {
		LogicalLogList list = new LogicalLogList();
		list.decode(IoBuffer.wrap((byte[])message));
		List<String> logs = list.getLogs();
		List<String> err = list.getErrorList();
		synchronized (lock) {
			cachedList.addAll(logs);
			ErrorCache.setCachedErrList(err);
		}
	}

	public static void init() throws IOException {
		String fileName = caluFileName();
		File file = getLogFile(fileName, false);
		while(file.exists()) {
			fileName = fileName + ".bak";
			file = new File(fileName);
		}
		currFileName = fileName;
		bfw = getNewBufferedWriter(fileName);
	}

	public static void startWriteThread() {
		WriteFileThread wft = new WriteFileThread();
		wft.setDaemon(true);
		wft.setName("WriteFileThread");
		wft.start();
	}

	private static BufferedWriter getNewBufferedWriter(String fileName) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "utf-8"));
	}

	private static BufferedWriter getBufferedWriter() throws IOException {
		String fileName = caluFileName();
		if (fileName.split("\\.")[1].equals(currFileName.split("\\.")[1]))
		{
			return bfw;
		}
		bfw.flush();
		bfw.close();
		currFileName = fileName;
		bfw = getNewBufferedWriter(currFileName);
		return bfw;
	}

	public static File getLogFile(String fileName, boolean createIfNotExist) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			List<File> tList = new ArrayList<File>();
			while(!file.exists()) {
				tList.add(file);
				file = file.getParentFile();
			}
			for (int i = tList.size() - 1; i >= 0; i--) {
				if (i > 0) {
					tList.get(i).mkdir();
				}
				if (createIfNotExist && i == 0) {
					tList.get(i).createNewFile();
				}
			}
			file = tList.get(0);
		}
		return file;
	}

	public static String caluFileName()
	{
		long curr = DateUtil.currentTimeMillis();
		Calendar calendar = DateUtil.getCalendar();
		calendar.setTimeInMillis(curr);
		StringBuilder sb = new StringBuilder();
		sb.append(Log2FileConf.recordDir);
		sb.append(Log2FileConf.fileName);
		sb.append(".");
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		sb.append(fixNum(calendar.get(Calendar.MONTH) + 1));
		sb.append("-");
		sb.append(fixNum(calendar.get(Calendar.DAY_OF_MONTH)));
		sb.append("-");
		sb.append(fixNum(calendar.get(Calendar.HOUR_OF_DAY)));
		sb.append("-");
		int min = calendar.get(Calendar.MINUTE);
		min = (min / Log2FileConf.fileNameSubFfixInterval) * Log2FileConf.fileNameSubFfixInterval;
		sb.append(fixNum(min));
		return sb.toString();
	}

	private static String fixNum(int num) {
		return num < 10 ? ("0" + num) : String.valueOf(num);
	}

	static class WriteFileThread extends Thread {

		public void run() {
			while(true) {
				try {
					List<String> tList = null;
					synchronized (lock) 	{
						tList = cachedList;
						cachedList = new ArrayList<String>();
					}
					bfw = getBufferedWriter();
					for (int i = 0; i < tList.size(); i++) {
						bfw.write(tList.get(i));
						bfw.newLine();
					}
					bfw.flush();
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("写日志文件失败", e);
				} finally {
					try {
						bfw.flush();
					} catch (Exception e) {
						LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("写日志文件失败", e);
					}
					try {
						Thread.sleep(Log2FileConf.recordInterval * 1000);
					} catch (InterruptedException e) {
						LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("WriteFileThread", e);
					}
				}
			}
		}

	}

}
