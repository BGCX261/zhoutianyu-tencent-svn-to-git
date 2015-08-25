package com.cyou.wg.sns.gs.core.dataSource;

import java.sql.Connection;

import com.ibatis.sqlmap.client.SqlMapSession;

public class DataConn {
	private Connection conn;
	private BaseDataSource dataSource;
	private SqlMapSession session;
	
	
	
	public SqlMapSession getSession() {
		return session;
	}
	public void setSession(SqlMapSession session) {
		this.session = session;
	}
	public DataConn(Connection conn, BaseDataSource dataSource) {
		this.conn = conn;
		this.dataSource = dataSource;
	}
	public Connection getConn() {
		return conn;
	}
	public BaseDataSource getDataSource() {
		return dataSource;
	}
	
	
}
