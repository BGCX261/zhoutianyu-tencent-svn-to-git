package com.cyou.wg.sns.gs.core.factory.sequence;

import java.io.File;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.cyou.wg.sns.gs.core.domain.Sequence;
import com.cyou.wg.sns.gs.core.domain.ThreadData;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;

public class SequenceFactoryWithMemcached{
	public static final String SEQ_USER_ID = "User";
	private static int lockOverTime = 60;//锁超时 秒
	private static int expTime = Integer.MAX_VALUE;
	public static int STEP_INC = 2000;//id的步长增加数量，db中只记录下一次的步长位置，memcached记录下一次可以使用的id
	private static MemcachedClient memcachedClient = null;
	
	
	
	
	public void init() throws Exception {
		File file = ClassPath.location.createRelative("client/memcached.xml").getFile();
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		List<Element> list = doc.getRootElement().elements();
		for(Element e : list) {
			if(e.getName().equals("data")) {
				List<Element> tList = e.elements();
				InetSocketAddress[] addrs = new InetSocketAddress[tList.size() - 1];
				for(Element ee : tList) {
					int index = Integer.valueOf(ee.attribute("id").getStringValue()) - 1;
					if(index > addrs.length) {
						memcachedClient = new MemcachedClient(new InetSocketAddress(ee.attribute("ip").getStringValue(), Integer.valueOf(ee.attribute("port").getStringValue())));
					}
				}
			}
		}
		
	}
	/**
	 * 得到某个序列器的值
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	protected int getSequence(String type){
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
	public  int getMemcachedSequenceValue(String key) {
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
	public  int getDbSequenceValue(String key) throws Exception {
		try {
			Object obj =  SqlMapClientMgr.getSqlMap(0).queryForObject("getSequenceValue", key);
			if(obj != null) {
				return (Integer)obj;
			}
			return -1;
		}catch (Exception e) {
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
		ThreadData.insertImm(0, "insertNewSequence", new Sequence(type, 1));
	}
	
	/**
	 * 更新数据库中的值
	 * @throws SQLException 
	 */
	private  void updateDbSequence(String type, int count) throws SQLException {
		ThreadData.updateImm(0, "updateSequence", new Sequence(type, count));
	}
	/**
	 * 更新缓存中的数据
	 * @param key
	 * @param count
	 */
	private  void updateMemcached(String key, int count) {
		memcachedClient.set(key, expTime, count);
	}
	/**
	 * 锁定对应的序列器
	 * @param key
	 */
	public  void lock(String key) {
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
	public  void releaseLock(String key) {
		for(int i = 0; i < 3; i++) {
			OperationFuture<Boolean> op = memcachedClient.delete(key);
			try {
				op.get();
				break;
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("解除序列器锁定错误", e);
			} 
		}
	}
	
}
