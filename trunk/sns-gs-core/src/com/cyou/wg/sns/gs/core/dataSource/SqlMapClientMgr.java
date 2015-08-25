package com.cyou.wg.sns.gs.core.dataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dom4j.DocumentException;

import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.cyou.wg.sns.gs.core.exception.CyouAppException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.factory.spring.ServiceFactory;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;

public class SqlMapClientMgr {
	
	private static SqlMapClientImpl[] client;
	
	private static ThreadLocal<SqlMapSession[]> session = new ThreadLocal<SqlMapSession[]>();
	
	public static BaseDataSource[] dataS = null;//默认连接
	
	private static BaseDataSource[] dataSBack = null;//备份数据库
	
	public static BaseDataSource getDataSource(int dbIndex) {
		return dataS[dbIndex];
	}
	
	public static Connection getConnection(int dbIndex) {
		try {
			return getDataSource(dbIndex).getConnection();
		}catch (SQLException e) {
			try {
				return dataSBack[dbIndex].getConnection();
			} catch (SQLException e1) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("数据库连接错误", e1);
				throw new CyouAppException("数据库连接错误，备份数据库暂无法使用。");
			}
		}
	}
	/**
	 * 初始化数据库连接和sqlmap
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	public static void init() throws IOException, DocumentException {
		SqlMapClientMgr.initDataSource();
		SqlMapClientMgr.initSqlMap();
	}
	
	/**
	 * 初始化数据库连接
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	private static void initDataSource() throws IOException, DocumentException {
		dataS = DataSourceInit.init("slave");
		dataSBack = DataSourceInit.init("backup");
	}
	/**
	 * 重新初始化数据库连接
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public static void reInitDataSource() throws IOException, DocumentException {
		dataS = DataSourceInit.init("slave");
		dataSBack = DataSourceInit.init("backup");
	}
	
	/**
	 * 初始化sqlMap
	 * @throws IOException 
	 */
	private static void initSqlMap() throws IOException {
		client = new SqlMapClientImpl[dataS.length];
		File file = ClassPath.location.createRelative("sql/sqlmap-config.xml").getFile();
		for(int i = 0; i < dataS.length; i++) {
			client[i] = (SqlMapClientImpl) SqlMapClientBuilder.buildSqlMapClient(new FileInputStream(file));
		}
		
	}
	
	
	
	
	public static SqlMapClientImpl getDefaultSqlMapClientImpl() {
		return client[0];
	}
	
	public static SqlMapSession getSqlMap(int dbIndex) throws SQLException {
		SqlMapSession[] s = session.get();
		if(s == null) {
			s = new SqlMapSession[client.length];
			session.set(s);
		}
		if(s[dbIndex] == null) {
			s[dbIndex] = client[dbIndex].openSession(getConnection(dbIndex));
		}
		return s[dbIndex];
	}
	
	
	
	/**
	 * 关闭当前数据库回话,归还连接
	 * @throws Exception 
	 */
	public static void closeCurr() throws Exception {
		if(session.get() == null) {
			return;
		}
		SqlMapSession[] ds = session.get();
		SqlMapSession ss = null;
		for(int i = 0; i < ds.length; i++) {
			ss = ds[i];
			if(ss != null) {
				ss.getCurrentConnection().close();
				ss.close();
			}
		}
		session.remove();
	}
	/**
	 * 关闭所有数据库回话
	 */
	public static void closeAll() {
		for(BaseDataSource ds : dataS) {
			try {
				ds.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
