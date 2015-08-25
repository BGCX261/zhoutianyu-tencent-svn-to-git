// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WorkThread.java

package com.cyou.wg.sns.ctsvr.app.logFile2Db.work;

import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.ctsvr.core.util.LogFile2DbProperties;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.threadPool.CoreThreadPool;
import java.io.*;
import java.sql.*;
import javax.sql.DataSource;
import org.slf4j.Logger;

public class WorkThread extends Thread
{
	class WriteDbThread
		implements Runnable
	{

		private File file;
		final WorkThread this$0;

		public void run()
		{
			Connection conn = null;
			try
			{
				conn = CenterServerConfig.logFile2DbServer[svrId - 1].getDataSource().getConnection();
			}
			catch (SQLException e1)
			{
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("得到数据库连接失败svrId：").append(svrId).toString(), e1);
			}
			try
			{
				writeFile2Db(conn, file);
				delete(file);
			}
			catch (Exception e)
			{
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("写数据库失败 原始文件：").append(file.getName()).toString(), e);
			}
		}

		private void writeFile2Db(Connection conn, File file)
			throws IOException, SQLException
		{
			InputStreamReader in = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader bfr = new BufferedReader(in);
			String br = bfr.readLine();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			int count = 0;
			int all = 0;
			for (; br != null; br = bfr.readLine())
			{
				try
				{
					writeOneSql(state, br);
				}
				catch (Exception e)
				{
					LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("写数据库失败 原始日志：").append(br).toString(), e);
				}
				count++;
				all++;
				if (count >= 10000)
				{
					conn.commit();
					count = 0;
				}
			}

			if (count > 0)
				conn.commit();
			bfr.close();
			conn.close();
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").info((new StringBuilder()).append("成功写入日志文件：").append(file.getName()).append("记录成功：").append(all).append("条日志").toString());
		}

		private void delete(File file)
		{
			file.delete();
		}

		public void writeOneSql(Statement statement, String br)
			throws SQLException
		{
			StringBuilder sb = new StringBuilder();
			String s[] = br.split(",");
			sb.append(sql);
			sb.append(s[4]);
			sb.append(" values ('");
			sb.append(s[0]);
			sb.append("','");
			sb.append(s[1]);
			sb.append("','");
			sb.append(s[2]);
			sb.append("','");
			sb.append(s[3]);
			sb.append("','");
			for (int i = 5; i < s.length; i++)
			{
				sb.append(s[i]);
				if (i < s.length - 1)
					sb.append("','");
			}

			sb.append("')");
			statement.executeUpdate(sb.toString());
		}

		public WriteDbThread(File file)
		{
			this$0 = WorkThread.this;
			super();
			this.file = null;
			this.file = file;
		}
	}


	private String sql;
	private int svrId;

	public WorkThread(int svrId)
	{
		sql = "insert into ";
		this.svrId = 0;
		this.svrId = svrId;
	}

	public void run()
	{
		Connection conn = null;
		try
		{
			conn = CenterServerConfig.logFile2DbServer[svrId - 1].getDataSource().getConnection();
		}
		catch (SQLException e1)
		{
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("得到数据库连接失败svrId：").append(svrId).toString(), e1);
		}
		do
		{
			File dir = new File(CenterServerConfig.logFile2DbServer[svrId - 1].getLogFileDir());
			File fileList[] = dir.listFiles();
			File arr$[] = fileList;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				File file = arr$[i$];
				try
				{
					WriteDbThread writeDbThread = new WriteDbThread(file);
					CoreThreadPool.addTast(writeDbThread);
				}
				catch (Exception e)
				{
					LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("写数据库失败 原始文件：").append(file.getName()).toString(), e);
				}
			}

			try
			{
				Thread.sleep(CenterServerConfig.logFile2DbServer[svrId - 1].getInterval() * 1000);
			}
			catch (InterruptedException e)
			{
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error("WorkThread", e);
			}
		} while (true);
	}


}
