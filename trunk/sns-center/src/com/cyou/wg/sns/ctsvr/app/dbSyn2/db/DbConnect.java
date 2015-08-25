package com.cyou.wg.sns.ctsvr.app.dbSyn2.db;

import java.sql.*;

import com.cyou.wg.sns.ctsvr.core.log.LogFactory;

public class DbConnect {

	private Connection conn;
	private String driver;
	private String url;
	private String userName;
	private String pass;

	public DbConnect(String driver, String url, String userName, String pass) {
		this.driver = driver;
		this.url = url;
		this.userName = userName;
		this.pass = pass;
		init();
	}

	public void init() {
		try {
			Class.forName(driver);
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库JDBC连接Driver加载失败", e);
		}
	}

	public void connect() throws Exception {
		if (conn == null || conn.isClosed())
			conn = DriverManager.getConnection(url, userName, pass);
	}

	public Connection getConnect() {
		return conn;
	}

	public void close() {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("JDBC数据库连接关闭失败", e);
			}
	}

}
