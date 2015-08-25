// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InitIdMapping.java

package com.cyou.wg.sns.ctsvr.app.dbSyn.work;

import java.io.*;
import java.util.*;

public class InitIdMapping
{
	static class CheckConfFileThread extends Thread
	{

		public void run()
		{
			long modifyTime = InitIdMapping.confFile.lastModified();
			do
			{
				long t = InitIdMapping.confFile.lastModified();
				if (t != modifyTime)
				{
					System.out.println("sql conf modify");
					modifyTime = t;
					try
					{
						InitIdMapping.initSqlMap();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				try
				{
					Thread.sleep(2000L);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			} while (true);
		}

		CheckConfFileThread()
		{
		}
	}


	private static Map idMap = new HashMap();
	private static File confFile = null;

	public InitIdMapping()
	{
	}

	public static void initSqlMap()
		throws IOException
	{
		Properties p = new Properties();
		confFile = new File("conf/DbSyn/sqlIdConf.cf");
		FileInputStream in = new FileInputStream(confFile);
		p.load(in);
		Iterator it = p.keySet().iterator();
		Map tmp = new HashMap();
		String key;
		for (; it.hasNext(); tmp.put(key, p.getProperty(key)))
			key = (String)it.next();

		idMap = tmp;
		in.close();
	}

	public static void startCheckThread()
	{
		CheckConfFileThread c = new CheckConfFileThread();
		c.setName("CheckConfFileThread");
		c.setDaemon(false);
		c.start();
	}

	public static void main(String arg[])
		throws IOException
	{
		initSqlMap();
		startCheckThread();
	}

	public static String getSql(String id)
	{
		return (String)idMap.get(id);
	}


}
