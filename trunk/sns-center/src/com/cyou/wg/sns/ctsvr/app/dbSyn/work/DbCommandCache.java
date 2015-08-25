// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DbCommandCache.java

package com.cyou.wg.sns.ctsvr.app.dbSyn.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommandList;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.ctsvr.core.util.ServerProperties;
import com.cyou.wg.sns.gs.core.dataSource.DataSourceInit;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.mina.core.buffer.IoBuffer;
import org.dom4j.DocumentException;
import org.slf4j.Logger;

// Referenced classes of package com.cyou.wg.sns.ctsvr.app.dbSyn.work:
//			WriteThread

public class DbCommandCache
{
	static class DisPatchDbCommand extends Thread
	{

		public void run()
		{
			List tList = null;
			do
				try
				{
					synchronized (DbCommandCache.commandListLock)
					{
						tList = DbCommandCache.commandList;
						DbCommandCache.commandList = new ArrayList();
					}
					for (int i = 0; i < tList.size(); i++)
					{
						List list = ((DbCommandList)tList.get(i)).getList();
						for (int j = 0; j < list.size(); j++)
							DbCommandCache.threads[Math.abs(((DbCommand)list.get(j)).getObjectType()) % DbCommandCache.threads.length].add((DbCommand)list.get(j));

					}

					tList = null;
					Thread.sleep(500L);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			while (true);
		}

		DisPatchDbCommand()
		{
		}
	}


	public static BasicDataSource dataSource;
	public static WriteThread threads[];
	public static Object commandListLock = new Object();
	public static List commandList = new ArrayList();

	public DbCommandCache()
	{
	}

	public static void init(int id)
		throws DocumentException
	{
		dataSource = DataSourceInit.init(new File("conf/DbSyn/dataSource.xml"), "master", id);
		threads = new WriteThread[CenterServerConfig.dbSynServer[id - 1].getWorkThreadNum()];
		for (int i = 0; i < threads.length; i++)
			initThread(i);

		DisPatchDbCommand dc = new DisPatchDbCommand();
		dc.setDaemon(true);
		dc.setName("DisPatchDbCommand");
		dc.start();
	}

	public static void initThread(int id)
	{
		WriteThread writeThread = new WriteThread(dataSource, id, CenterServerConfig.dbSynServer[0].getInterval());
		writeThread.setDaemon(true);
		writeThread.setName((new StringBuilder()).append("DbSynWriteThread-").append(id).toString());
		writeThread.start();
		threads[id] = writeThread;
	}

	public static void addCommand(byte src[])
	{
		DbCommandList dbCommandList = new DbCommandList();
		try
		{
			dbCommandList.decode(IoBuffer.wrap(src));
		}
		catch (Exception e)
		{
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error("数据库更新命令解码错误", e);
			return;
		}
		synchronized (commandListLock)
		{
			addCommand(dbCommandList);
		}
	}

	private static void addCommand(DbCommandList dbCommandList)
	{
		boolean isAdd = false;
		if (commandList.size() > 0)
		{
			if (dbCommandList.getSendTime() >= ((DbCommandList)commandList.get(commandList.size() - 1)).getSendTime())
			{
				commandList.add(dbCommandList);
				return;
			}
		} else
		{
			commandList.add(dbCommandList);
			return;
		}
		int i = 0;
		do
		{
			if (i >= commandList.size())
				break;
			if (((DbCommandList)commandList.get(i)).getSendTime() > dbCommandList.getSendTime())
			{
				commandList.add(i, dbCommandList);
				isAdd = true;
				break;
			}
			i++;
		} while (true);
		if (!isAdd)
			commandList.add(dbCommandList);
	}

	public static void main(String arg[])
		throws Exception
	{
		CenterServerConfig.initNetProperties(new File("conf/centerServer/centerServer.xml"), "dbSyn");
		dataSource = DataSourceInit.init(new File("conf/DbSyn/dataSource.xml"), "master", 1);
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(false);
		PreparedStatement ps = conn.prepareStatement("insert into sample_? (id,name) values (?,?)");
		for (int i = 0; i < 10; i++)
		{
			ps.setInt(1, 1);
			ps.setInt(2, i);
			ps.setString(3, (new StringBuilder()).append("aaaaaaaa").append(i).toString());
			ps.execute();
		}

		conn.commit();
	}

}
