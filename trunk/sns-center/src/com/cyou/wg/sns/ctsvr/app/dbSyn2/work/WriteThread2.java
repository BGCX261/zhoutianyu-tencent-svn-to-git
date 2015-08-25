package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.ctsvr.app.dbSyn.work.InitIdMapping;
import com.cyou.wg.sns.ctsvr.app.dbSyn2.db.DbConnect;
import com.cyou.wg.sns.ctsvr.app.dbSyn2.db.DbConnectFactory;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import java.sql.*;
import java.util.*;
import org.apache.commons.dbcp.BasicDataSource;

public class WriteThread2 extends Thread {

	private DbConnect dbConn;
	private String sql;
	private int interval = 60 * 1000;
	private int batch = 5000;
	private byte writeStatus;
	public static final byte WRITE_STATUS_WAIT = 0;
	public static final byte WRITE_STATUS_PROCESSING = 1;
	public Object lock = new Object();
	public Map<String, DbCommand> receiveCache = new HashMap<String, DbCommand>();
	public Map<String, PreparedStatement> sqlPsMap = new HashMap<String, PreparedStatement>();

	public WriteThread2(BasicDataSource basicDataSource, int id, int interval) {
		this.interval = interval * 1000;
		sql = basicDataSource.getValidationQuery();
		dbConn = DbConnectFactory.getDbConnect(basicDataSource);
	}

	public byte getWriteStatus() {
		return writeStatus;
	}

	public boolean init() {
		try {
			dbConn.connect();
			dbConn.getConnect().setAutoCommit(false);
			return true;
		} catch(Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库建立连接失败：" + getName(), e);
			return false;
		}
	}

	public void run() {
		while (!init())  {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("初始化数据库同步线程JDBC连接失败，等待1秒重试：" + getName());
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库同步线程暂停失败：" + getName(), e);
			}
		}
		while (true) {
			sendMessage2Db();
			try {
				Thread.sleep(interval);
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库同步线程暂停失败：" + getName(), e);
			}
		}
	}

	private void checkHeart() {
		while (!DbHeartbeatThread.dbConnected) 
			try {
				LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("数据库心跳检测失败，数据库同步线程等待重试：" + getName());
				Thread.sleep(3000L);
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库同步线程暂停失败：" + getName(), e);
			}
	}

	public void add(DbCommand command)
	{
		if (InitIdMapping.getSql(command.getBaseSql()) == null) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("不存在sql id：" + command.getBaseSql());
			return;
		} else {
			command.setBaseSql(InitIdMapping.getSql(command.getBaseSql()));
		}
		synchronized (lock) {
			DbCommand oldCom = (DbCommand)receiveCache.get(command.getKey());
			if (oldCom == null) {
				receiveCache.put(command.getKey(), command);
			} else if (command.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_DEL) {
				if (oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT) {
					receiveCache.remove(command.getKey());
				} else {
					receiveCache.put(command.getKey(), command);
				}
			} else if (command.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_UPDATE) {
				if (oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT) {
					oldCom.setNextCommand(command);
				} else if (oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_UPDATE) {
					receiveCache.put(command.getKey(), command);
				} else if (oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_DEL) {// 如果原是删除
					if (oldCom.getNextCommand() != null && oldCom.getNextCommand().getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT) {// 如果删除的子队列中存在插入命令，则直接替换原命令
						receiveCache.put(command.getKey(), command);
					}
				}
			}
		}
		writeStatus = WRITE_STATUS_PROCESSING;

	}

	private void runTestSql() {
		try {
			PreparedStatement ps = (PreparedStatement)sqlPsMap.get(sql);
			if (ps == null) {
				ps = dbConn.getConnect().prepareStatement(sql);
				sqlPsMap.put(sql, ps);
			} else
			if (ps.getConnection() == null || ps.getConnection().isClosed()) {
				LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName() + "数据库连接关闭，开始重新初始化。");
				init();
				ps = dbConn.getConnect().prepareStatement(sql);
				sqlPsMap.put(sql, ps);
				LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName() + "数据库连接关闭，初始化完毕。");
			}
			ps.execute();
			dbConn.getConnect().commit();
			return;
		} catch (SQLException e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("执行默认命令失败", e);
		}
	}

	public void sendMessage2Db() {
		Map<String, DbCommand> sendCache = null;
		synchronized (lock) {
			sendCache = receiveCache;
			receiveCache = new HashMap<String, DbCommand>();
		}
		Collection<DbCommand> set = sendCache.values();
		if (set.isEmpty()) {
			writeStatus = WRITE_STATUS_WAIT;
			runTestSql();
			return;
		}
		int count = 0;
		int all = 0;
		int fail = 0;
		for(DbCommand command : set) {
			count++;
			all++;
			try {
				writeDb(command);
				if (command.getNextCommand() != null) {
					count++;
					writeDb(command.getNextCommand());
				}
			} catch (Exception e) {
				fail++;
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("预执行数据库同步命令失败：" + command.toString(), e);
			}
			if (count >= batch) {
				try {
					dbConn.getConnect().commit();
				} catch (SQLException e) {
					fail += count;
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("提交数据库同步命令失败", e);
				}
				count = 0;
			}
		}
			
		if (count > 0) {
			try {
				dbConn.getConnect().commit();
			} catch (SQLException e) {
				fail += count;
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("提交数据库同步命令失败", e);
			}
			count = 0;
		}
		writeStatus = WRITE_STATUS_WAIT;
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName() + "提交数据库同步命令执行完毕，总共：" + all + "条,失败" + fail + "条。");
	}

	public void writeDb(DbCommand command) throws SQLException {
		checkHeart();
		PreparedStatement ps = (PreparedStatement)sqlPsMap.get(command.getBaseSql());
		if (ps == null)
		{
			ps = dbConn.getConnect().prepareStatement(command.getBaseSql());
			sqlPsMap.put(command.getBaseSql(), ps);
		} else if (ps.getConnection() == null || ps.getConnection().isClosed()) {
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName() + "数据库连接关闭，开始重新初始化。");
			init();
			ps = dbConn.getConnect().prepareStatement(command.getBaseSql());
			sqlPsMap.put(command.getBaseSql(), ps);
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName() + "数据库连接关闭，初始化完毕。");
		}
		for (int i = 0; i < command.getParams().length; i++) {
			Object o = command.getParams()[i];
			if (o == null) {
				ps.setNull(i + 1, 0);
				continue;
			}
			if (o instanceof Integer) {
				ps.setInt(i + 1, ((Integer)o).intValue());
				continue;
			}
			if (o instanceof String) {
				ps.setString(i + 1, (String)o);
				continue;
			}
			if (o instanceof Timestamp)
			{
				ps.setTimestamp(i + 1, (Timestamp)o);
				continue;
			}
			if (o instanceof byte[]) {
				ps.setBytes(i + 1, (byte[])o);
				continue;
			}
			if (o instanceof Short) {
				ps.setShort(i + 1, ((Short)o).shortValue());
				continue;
			}
			if (o instanceof Byte) {
				ps.setByte(i + 1, ((Byte)o).byteValue());
				continue;
			}
			if (o instanceof Long)
				ps.setLong(i + 1, ((Long)o).longValue());
			else
				throw new CyouSysException(command.getBaseSql() + "，不支持的数据类型 ：" + o.getClass().getName());
		}

		ps.execute();
	}
}
