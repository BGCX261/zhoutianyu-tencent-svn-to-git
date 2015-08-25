package com.cyou.wg.sns.ctsvr.app.log2File.work;

import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.io.*;
import java.util.*;

public class LogUtil {

	public static String caluFileName(String recordDir, String fileName) {
		long curr = DateUtil.currentTimeMillis();
		Calendar calendar = DateUtil.getCalendar();
		calendar.setTimeInMillis(curr);
		StringBuilder sb = new StringBuilder();
		sb.append(recordDir);
		sb.append(fileName);
		sb.append(".");
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		sb.append(calendar.get(Calendar.MONTH) + 1);
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
		return num < 10 ? ( "0" + num ) : String.valueOf(num);
	}

	public static File getLogFile(String fileName, boolean createIfNotExist) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			List<File> tList = new ArrayList<File>();
			while(!file.exists()){
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

			file = (File)tList.get(0);
		}
		return file;
	}

	public static BufferedWriter getNewBufferedWriter(String fileName) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "utf-8"));
	}
}
