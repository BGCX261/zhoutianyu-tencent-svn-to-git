// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WriteThread.java

package com.cyou.wg.sns.ctsvr.app.dbSyn.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import java.sql.*;
import java.util.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;

// Referenced classes of package com.cyou.wg.sns.ctsvr.app.dbSyn.work:
//			DbCommandCache, InitIdMapping

public class WriteThread extends Thread
{

	private Connection conn;
	private BasicDataSource basicDataSource;
	private String sql;
	private int id;
	private int interval;
	private int batch;
	public Object lock;
	public Map receiveCache;
	public Map sqlPsMap;

	public WriteThread(BasicDataSource basicDataSource, int id, int interval)
	{
		this.interval = 60000;
		batch = 5000;
		lock = new Object();
		receiveCache = new HashMap();
		sqlPsMap = new HashMap();
		this.basicDataSource = basicDataSource;
		this.id = id;
		this.interval = interval * 1000;
	}

	public void init()
		throws SQLException
	{
		conn = basicDataSource.getConnection();
		conn.setAutoCommit(false);
		sql = basicDataSource.getValidationQuery();
	}

	public void run()
	{
		try
		{
			init();
		}
		catch (SQLException e)
		{
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("启动数据库同步线程失败，等待1秒重试：").append(getName()).toString(), e);
			try
			{
				Thread.sleep(1000L);
			}
			catch (InterruptedException e1)
			{
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("启动数据库同步线程失败，等待1秒重试：").append(getName()).toString(), e);
			}
			DbCommandCache.initThread(id);
			return;
		}
		do
		{
			sendMessage2Db();
			try
			{
				Thread.sleep(interval);
			}
			catch (InterruptedException e)
			{
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("数据库同步线程暂停失败：").append(getName()).toString(), e);
			}
		} while (true);
	}

	public void add(DbCommand command)
	{
		if (InitIdMapping.getSql(command.getBaseSql()) == null)
		{
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("不存在sql id：").append(command.getBaseSql()).toString());
			return;
		}
		command.setBaseSql(InitIdMapping.getSql(command.getBaseSql()));
		Object obj = lock;
		JVM INSTR monitorenter ;
		DbCommand oldCom = (DbCommand)receiveCache.get(command.getKey());
		if (oldCom == null)
			receiveCache.put(command.getKey(), command);
		else
		if (command.getBaseObjectOpt() == 3)
		{
			if (oldCom.getBaseObjectOpt() == 2)
				receiveCache.remove(command.getKey());
			else
				receiveCache.put(command.getKey(), command);
		} else
		if (command.getBaseObjectOpt() == 1)
		{
			if (oldCom.getBaseObjectOpt() == 2)
				oldCom.setNextCommand(command);
			else
			if (oldCom.getBaseObjectOpt() == 1)
				receiveCache.put(command.getKey(), command);
			else
			if (oldCom.getBaseObjectOpt() == 3 && oldCom.getNextCommand() != null && oldCom.getNextCommand().getBaseObjectOpt() == 2)
				receiveCache.put(command.getKey(), command);
		} else
		if (oldCom.getBaseObjectOpt() == 3)
			oldCom.setNextCommand(command);
		else
			return;
		break MISSING_BLOCK_LABEL_276;
		Exception exception;
		exception;
		throw exception;
	}

	public void sendMessage2Db()
	{
		Collection set;
		Map sendCache = null;
		synchronized (lock)
		{
			sendCache = receiveCache;
			receiveCache = new HashMap();
		}
		set = sendCache.values();
		if (!set.isEmpty())
			break MISSING_BLOCK_LABEL_98;
		try
		{
			conn.createStatement().execute(sql);
			conn.commit();
			return;
		}
		catch (SQLException e)
		{
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error("执行默认命令失败", e);
		}
		finally
		{
			return;
		}
		return;
		int count = 0;
		int all = 0;
		int fail = 0;
		Iterator i$ = set.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			DbCommand command = (DbCommand)i$.next();
			count++;
			all++;
			try
			{
				writeDb(command);
				if (command.getNextCommand() != null)
				{
					count++;
					writeDb(command.getNextCommand());
				}
			}
			catch (Exception e)
			{
				fail++;
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error((new StringBuilder()).append("预执行数据库同步命令失败：").append(command.toString()).toString(), e);
			}
			if (count >= batch)
			{
				try
				{
					conn.commit();
				}
				catch (SQLException e)
				{
					fail += count;
					LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error("提交数据库同步命令失败", e);
				}
				count = 0;
			}
		} while (true);
		if (count > 0)
		{
			try
			{
				conn.commit();
			}
			catch (SQLException e)
			{
				fail += count;
				LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.error").error("提交数据库同步命令失败", e);
			}
			count = 0;
		}
		LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.info").info((new StringBuilder()).append(getName()).append("提交数据库同步命令执行完毕，总共：").append(all).append("条,失败").append(fail).append("条。").toString());
		return;
	}

	public void writeDb(DbCommand command)
		throws SQLException
	{
		PreparedStatement ps = (PreparedStatement)sqlPsMap.get(command.getBaseSql());
		if (ps == null)
		{
			ps = conn.prepareStatement(command.getBaseSql());
			sqlPsMap.put(command.getBaseSql(), ps);
		} else
		if (ps.getConnection().isClosed())
		{
			conn.close();
			conn = null;
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.info").info((new StringBuilder()).append(getName()).append("数据库连接关闭，开始重新初始化。").toString());
			init();
			ps = conn.prepareStatement(command.getBaseSql());
			sqlPsMap.put(command.getBaseSql(), ps);
			LogFactory.getLogger("com.cyou.wg.sns.core.log.sys.info").info((new StringBuilder()).append(getName()).append("数据库连接关闭，初始化完毕。").toString());
		}
		for (int i = 0; i < command.getParams().length; i++)
		{
			Object o = command.getParams()[i];
			if (o == null)
			{
				ps.setNull(i + 1, 0);
				continue;
			}
			if (o instanceof Integer)
			{
				ps.setInt(i + 1, ((Integer)o).intValue());
				continue;
			}
			if (o instanceof String)
			{
				ps.setString(i + 1, (String)o);
				continue;
			}
			if (o instanceof Timestamp)
			{
				ps.setTimestamp(i + 1, (Timestamp)o);
				continue;
			}
			if (o instanceof byte[])
			{
				ps.setBytes(i + 1, (byte[])(byte[])o);
				continue;
			}
			if (o instanceof Short)
			{
				ps.setShort(i + 1, ((Short)o).shortValue());
				continue;
			}
			if (o instanceof Byte)
			{
				ps.setByte(i + 1, ((Byte)o).byteValue());
				continue;
			}
			if (o instanceof Long)
				ps.setLong(i + 1, ((Long)o).longValue());
			else
				throw new CyouSysException((new StringBuilder()).append(command.getBaseSql()).append("，不支持的数据类型 ：").append(o.getClass().getName()).toString());
		}

		ps.execute();
	}
}
