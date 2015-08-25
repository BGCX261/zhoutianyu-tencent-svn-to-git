package com.cyou.wg.sns.gs.core.factory.sequence;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.dom4j.DocumentException;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.domain.Sequence;
import com.cyou.wg.sns.gs.core.domain.ThreadData;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;

/**
 * 产生序列的工厂
 * 需要用到序列发生器的，目前只有玩家id。
 * 道具、精灵、建造玩家在得到或创建的时候有一定的顺序，可以计算出他们的序号
 * 道具：使用玩家id+背包序号的方式联合主键，所以不需要使用
 * 精灵：使用玩家id+精灵序号的方式联合主键
 * 建筑：使用玩家id+建造序号的方式
 * 
 * 此序列发生器访问单独的memcached缓冲区
 * @author Administrator
 *
 */
public class SequenceFactory {
	public static final String SEQ_USER_ID = "User";
	private static int lockOverTime = 60;//锁超时 秒
	private static int expTime = Integer.MAX_VALUE;
	public static int STEP_INC = 2000;//id的步长增加数量，db中只记录下一次的步长位置，memcached记录下一次可以使用的id
	private static MemcachedClient memcachedClient = null;
	/**
	 * 初始化memcachedClient
	 * @param addr
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public static void initMemcachedClient(InetSocketAddress addr) throws IOException, DocumentException {
		memcachedClient = new MemcachedClient(addr);
	}
	/**
	 * 得到某个序列器的值
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public static int getSequence(String type){
		try {
			lock(type + "_lock");
			int seq = getMemcachedSequenceValue(type);
			if(seq < 0) {//缓存中没有序列器
				seq = getDbSequenceValue(type);
				if(seq > 0) {//数据库中有序列器，为了不重复，把序列器加上步长
					seq = seq - seq % STEP_INC + STEP_INC + 1;
					updateDbSequence(type, seq);
				}else {//数据库中没有序列器
					insertSequence(type);
					seq = 1;
				}
				updateMemcached(type, seq);
			}else {
				seq++;
				updateMemcached(type, seq);
				if(seq % STEP_INC == 0) {
					updateDbSequence(type, seq + STEP_INC);
				}
			}
			return seq;
		}catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("操作序列器错误", e);
			throw new CyouSysException("操作序列器错误");
		}finally {
			releaseLock(type + "_lock");
		}
	}
	/**
	 * 得到缓存中的序列值
	 * @param key
	 * @return
	 */
	private static int getMemcachedSequenceValue(String key) {
		Object obj = memcachedClient.get(key);
		if(obj != null) {
			return (Integer)obj;
		}else {
			return -1;
		}
	}
	/**
	 * 从db中得到序列器的值
	 * db默认是第一组db
	 * @param key
	 * @return
	 * @throws SQLException 
	 */
	private static int getDbSequenceValue(String key) throws SQLException {
		try {
			Object obj =  SqlMapClientMgr.getSqlMap(0).queryForObject("getSequenceValue", key);
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
	private static void insertSequence(String type) throws SQLException {
		ThreadData.insertImm(0, "insertNewSequence", new Sequence(type, 1));
	}
	
	/**
	 * 更新数据库中的值
	 * @throws SQLException 
	 */
	private static void updateDbSequence(String type, int count) throws SQLException {
		ThreadData.updateImm(0, "updateSequence", new Sequence(type, count));
	}
	/**
	 * 更新缓存中的数据
	 * @param key
	 * @param count
	 */
	private static void updateMemcached(String key, int count) {
		memcachedClient.set(key, expTime, count);
	}
	/**
	 * 锁定对应的序列器
	 * @param key
	 */
	private static void lock(String key) {
		for(int i = 0; i < 3; i++) {
			OperationFuture<Boolean> op = memcachedClient.add(key, lockOverTime, 1);//锁超时60秒
			try {
				boolean a = op.get(100, TimeUnit.MILLISECONDS);
				if(a) {//此值是否已经锁定
					return;
				}else {//等待100毫秒
					Thread.sleep(100);
				}
			} catch (Exception e) {
				op.cancel(true);
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("读取序列器缓存错误", e);
			} 
		}
		throw new CyouSysException("锁定序列器超时key:" + key);
	}
	/**
	 * 释放锁
	 * @param key
	 */
	private static void releaseLock(String key) {
		for(int i = 0; i < 3; i++) {
			OperationFuture<Boolean> op = memcachedClient.delete(key);
			try {
				op.get();
				break;
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("解除序列器锁定错误", e);
			} 
		}
		try {
			SqlMapClientMgr.closeCurr();
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("关闭序列器数据库连接错误", e);
		}
	}

}
