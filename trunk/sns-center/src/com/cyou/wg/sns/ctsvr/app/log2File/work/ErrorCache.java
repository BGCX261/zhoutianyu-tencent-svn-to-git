package com.cyou.wg.sns.ctsvr.app.log2File.work;

import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ErrorCache {

	private static List<String> cachedErrList = new ArrayList<String>();
	private static Object lock = new Object();
	private static BufferedWriter bfw = null;
	private static String currFileName = null;

	public static List<String> getCachedErrList() {
		return cachedErrList;
	}

	public static void setCachedErrList(List<String> cachedErrList) {
		synchronized (lock) {
			cachedErrList.addAll(cachedErrList);
		}
	}

	public static void init() throws IOException {
		String fileName = LogUtil.caluFileName(Log2FileConf.errRecordDir, Log2FileConf.errFileName);
		File file = LogUtil.getLogFile(fileName, false);
		while(file.exists()){
			fileName = fileName + ".bak";
			file = new File(fileName);
		}
		currFileName = fileName;
		bfw = LogUtil.getNewBufferedWriter(fileName);
	}

	private static BufferedWriter getBufferedWriter() throws IOException {
		String fileName = LogUtil.caluFileName(Log2FileConf.errRecordDir, Log2FileConf.errFileName);
		if (fileName.split("\\.")[1].equals(currFileName.split("\\.")[1])) {
			return bfw;
		}
		bfw.flush();
		bfw.close();
		currFileName = fileName;
		bfw = LogUtil.getNewBufferedWriter(currFileName);
		return bfw;
	}

	public static void startWriteThread() {
		WriteErrFileThread wft = new WriteErrFileThread();
		wft.setDaemon(true);
		wft.setName("WriteErrFileThread");
		wft.start();
	}

	static class WriteErrFileThread extends Thread {

		public void run() {
			while(true) {
				try {
					List<String> tList = null;
					if (cachedErrList.size() != 0) {
						synchronized (lock) {
							tList = cachedErrList;
							cachedErrList = new ArrayList<String>();
						}
						bfw = getBufferedWriter();
						for (int i = 0; i < tList.size(); i++) {
							bfw.write(tList.get(i));
							bfw.newLine();
						}
						bfw.flush();
						
					}
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("写错误日志文件失败", e);
				} finally {
					try {
						bfw.flush();
					} catch (Exception e) {
						LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("写错误日志文件失败", e);
					}
					try {
						Thread.sleep(Log2FileConf.recordInterval * 1000);
					} catch (InterruptedException e) {
						LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("WriteErrFileThread", e);
					}
				}
			}
			
		}
	}

}
