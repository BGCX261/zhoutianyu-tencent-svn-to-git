package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.mina.core.buffer.IoBuffer;

public class FileUtil {

	public static String DB_FILE_PATH = "/var/tmp";
	public static final String DB_FILE_PREFIX = "dbSynCommand.";
	public static final String FILENAME_SUFFIX_WRITING = ".writing";
	public static final String FILENAME_SUFFIX_WAIT = ".wait";
	public static final String FILENAME_SUFFIX_DEL = ".del";
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

	public static File getDbFile() {
		File file = new File(DB_FILE_PATH);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	public static File createNewFile() throws IOException {
		Calendar c = DateUtil.getCalendar();
		String fileName = buildFileName(DB_FILE_PREFIX, dateFormat(c.getTime()), FILENAME_SUFFIX_WRITING);
		File file = new File(DB_FILE_PATH + "/" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public static void changeFileSuffix(File file, String suffix) {
		String oldFileName = file.getName();
		String dateStr = oldFileName.substring(oldFileName.indexOf(".") + 1, oldFileName.lastIndexOf("."));
		String newFileName = buildFileName(DB_FILE_PREFIX, dateStr, suffix);
		File dest = new File(DB_FILE_PATH + "/" + newFileName);
		file.renameTo(dest);
	}

	public static String buildFileName(String prefix, String date, String suffix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append(date);
		sb.append(suffix);
		return sb.toString();
	}

	public static String dateFormat(Date date) {
		try {
			return df.format(date);
		} catch(Exception e) {
			return null;
		}
	}

	public static void writeDbCommand2File(DbCommand cmd, OutputStream out) throws Exception {
		if (cmd == null) {
			return;
		}
		byte src[] = cmd.encode();
		out.write((byte)(src.length >> 8));
		out.write((byte)src.length);
		out.write(src);
	}

	public static void writeDbCommand2File(DbCommand cmd, RandomAccessFile randf) throws Exception {
		if (cmd == null) {
			return;
		}
		byte src[] = cmd.encode();
		randf.write((byte)(src.length >> 8));
		randf.write((byte)src.length);
		randf.write(src);
	}

	public static DbCommand readDbCommandFromFile(int length, InputStream in) throws Exception {
		if (length < 1) {
			return null;
		}
		DbCommand cmd = new DbCommand();
		byte src[] = new byte[length];
		in.read(src);
		IoBuffer buff = IoBuffer.wrap(src);
		cmd = new DbCommand();
		cmd.decode(buff);
		return cmd;
	}

	public static int toInt(byte high, byte low)
	{
		return (high << 8) & 0x0000ff00 | low & 0x000000ff;
	}

	public static File[] getDelFileList(File file, final String suffix)
	{
		if (!file.isDirectory()) {
			return null;
		} else {
			
			return file.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(suffix);
				}

			});
		}
	}

}
