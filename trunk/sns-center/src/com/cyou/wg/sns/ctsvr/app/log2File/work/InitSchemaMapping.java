package com.cyou.wg.sns.ctsvr.app.log2File.work;

import java.io.*;
import java.util.*;

public class InitSchemaMapping {

	private static Map<String, String[]> keyMap = new HashMap<String, String[]>();
	private static File confFile = null;
	public static String NULL = "NULL";
	private static String cfgSplit = ",";

	public static void initKeyMap() throws IOException {
		Properties p = new Properties();
		confFile = new File("conf/log2File/schema.cf");
		FileInputStream in = new FileInputStream(confFile);
		p.load(new InputStreamReader(in, "utf-8"));
		Iterator<?> it = p.keySet().iterator();
		Map<String, String[]> tmp = new HashMap<String, String[]>();
		while(it.hasNext()){
			String key = (String)it.next();
			tmp.put(key, p.getProperty(key).split(cfgSplit));
		}
		keyMap = tmp;
		in.close();
	}

	public static void startCheckThread() {
		CheckConfFileThread c = new CheckConfFileThread();
		c.setName("CheckConfFileThread");
		c.setDaemon(false);
		c.start();
	}

	public static String[] getKeys(String str) {
		return keyMap.get(str);
	}

	static class CheckConfFileThread extends Thread {

		public void run() {
			long modifyTime = InitSchemaMapping.confFile.lastModified();
			while (true) {
				long t = InitSchemaMapping.confFile.lastModified();
				if (t != modifyTime) {
					System.out.println("schame conf modify");
					modifyTime = t;
					try {
						InitSchemaMapping.initKeyMap();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
