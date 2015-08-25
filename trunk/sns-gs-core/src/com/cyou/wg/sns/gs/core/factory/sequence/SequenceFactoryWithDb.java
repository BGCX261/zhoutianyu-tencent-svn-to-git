package com.cyou.wg.sns.gs.core.factory.sequence;

import java.sql.SQLException;

import javax.sql.DataSource;


import com.cyou.wg.sns.gs.core.dataSource.DataSourceInit;
import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.domain.Sequence;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.ibatis.sqlmap.client.SqlMapSession;

public class SequenceFactoryWithDb{
	
	public static final String SEQ_USER_ID = "User";
	
	private DataSource dataSource;
	
	private ThreadLocal<SqlMapSession> tConn = new ThreadLocal<SqlMapSession>();
	
	
	
	

	public void init() throws Exception {
		dataSource = DataSourceInit.init("sequence")[0];
	}
	
	
	
	/**
	 * 得到某个序列器的值
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public int getSequence(String type){
		try {
			lock(type);
			int seq = getDbSequenceValue(type);
			if(seq < 0) {//数据库中没有序列器
				insertSequence(type);
				seq = 1;
			}else {
				seq++;
				updateDbSequence(type, seq);
			}
			return seq;
		}catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("操作序列器db错误", e);
			throw new CyouSysException("操作序列器db错误");
		}finally {
			try {
				releaseLock();
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("释放序列器db锁错误", e);
			}
		}
	}
	/**
	 * 从db中得到序列器的值
	 * db默认是第一组db
	 * @param key
	 * @return
	 * @throws SQLException 
	 */
	public  int getDbSequenceValue(String key) throws SQLException {
		try {
			Object obj =  tConn.get().queryForObject("getSequenceValue", key);
			if(obj != null) {
				return (Integer)obj;
			}
			return -1;
		}catch (SQLException e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("操作序列器db错误", e);
			throw e;
		}
		
	}
	/**
	 * 插入一个新的序列器
	 * @param key
	 * @throws SQLException
	 */
	private  void insertSequence(String type) throws SQLException {
		SqlMapSession ss = tConn.get();
		ss.insert("updateSequence", new Sequence(type, 1));
	}
	
	/**
	 * 更新数据库中的值
	 * @throws SQLException 
	 */
	private  void updateDbSequence(String type, int count) throws SQLException {
		SqlMapSession ss = tConn.get();
		ss.update( "updateSequence", new Sequence(type, count));
	}
	/**
	 * 锁定对应的序列器
	 * @param key
	 * @throws SQLException 
	 */
	public  void lock(String key) throws SQLException {
		SqlMapSession ss = SqlMapClientMgr.getDefaultSqlMapClientImpl().openSession(dataSource.getConnection());
		tConn.set(ss);
		tConn.get().getCurrentConnection().setAutoCommit(false);
		tConn.get().getCurrentConnection().createStatement().execute("start Transaction;");
		ss.queryForObject("lockSequence", key);
	}
	/**
	 * 释放锁
	 * @param key
	 * @throws SQLException 
	 */
	public  void releaseLock() throws Exception {
		tConn.get().getCurrentConnection().commit();
		tConn.get().getCurrentConnection().close();
		tConn.get().close();
	}
	

}
