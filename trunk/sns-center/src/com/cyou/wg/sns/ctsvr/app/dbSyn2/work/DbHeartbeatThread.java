package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn2.db.DbConnect;
import com.cyou.wg.sns.ctsvr.app.dbSyn2.db.DbConnectFactory;
import com.cyou.wg.sns.ctsvr.core.log.LogFactory;

import java.sql.Statement;
import org.apache.commons.dbcp.BasicDataSource;

public class DbHeartbeatThread extends Thread
{

	public static boolean dbConnected = true;
	public static int MAX_FIAL = 5;
	private static int timeoutInterval = 2000;
	private DbConnect dbConn;
	private String testSql;

	public DbHeartbeatThread(BasicDataSource basicDataSource) {
		testSql = basicDataSource.getValidationQuery();
		dbConn = DbConnectFactory.getDbConnect(basicDataSource);
	}

	public boolean init() {
		try {
			dbConn.connect();
			dbConn.getConnect().setAutoCommit(false);
			return true;
		} catch(Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库建立连接失败："+getName(), e);
			return false;
		}
	}

	public void run() {
		while(!init()) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("初始化心跳线程JDBC连接失败，等待1秒重试："+getName());
			sleepTime(1000);
		}
		int succCount = 0;
		int failCount = 0;
		Statement statement = null;
		while(true) {
			try {
				if (statement == null) {
					statement = dbConn.getConnect().createStatement();
				} else if (statement.getConnection() == null || statement.getConnection().isClosed()) {
					init();
					statement = dbConn.getConnect().createStatement();
				}
				statement.execute(testSql);
				dbConn.getConnect().commit();
				if (!dbConnected && ++succCount >= 3) {
					success();
				}
				failCount = 0;
			} catch (Exception e1) {
				succCount = 0;
				if (++failCount > MAX_FIAL) {
					fail();
				}
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("DB心跳线程检查远程连接失败,连续次数" + failCount, e1);
			} finally {
				sleepTime(timeoutInterval);
			}
		}
	}

	private void success() {
		dbConnected = true;
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("数据库心跳线连接数据库成功，数据库写线程唤醒");
	}

	private void fail() {
		dbConnected = false;
		dbConn.close();
		while(!init()) {
			LogFactory.getLogger(LogFactory.SYS_WARN_LOG).warn(getName()+"连接的远程主机连接超时，等待3秒重新连接...");
			sleepTime(3000L);
		}
	}

	private void sleepTime(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (Exception e1) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库心跳线程暂停失败", e1);
		}
	}

}
