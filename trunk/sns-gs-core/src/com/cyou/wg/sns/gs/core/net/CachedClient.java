package com.cyou.wg.sns.gs.core.net;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import com.cyou.wg.sns.gs.core.dataSource.DataClientHash;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.factory.memcached.ConnectFactory;
import com.cyou.wg.sns.gs.core.factory.sequence.SequenceFactory;
import com.cyou.wg.sns.gs.core.factory.sequence.SequenceFactoryWithMemcached;

/**
 * memcached客户端链接
 * @author Administrator
 *
 */
public class CachedClient {
	
	private static MemcachedClient dataClient;
	
	private static MemcachedClient lockClient;
	
	private static int expTime = Integer.MAX_VALUE;
	
	private static int lockOverTime = 60;//锁超时 秒
	
	public static int dataClientLength = 0;//数据缓存数量
	
	public static int lockClientLength = 0;//锁缓存数量
	
	public static void init() throws IOException, DocumentException {
		File file = ClassPath.location.createRelative("client/memcached.xml").getFile();
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		List<Element> list = doc.getRootElement().elements();
		for(Element e : list) {
			if(e.getName().equals("data")) {
				List<Element> tList = e.elements();
				InetSocketAddress[] addrs = new InetSocketAddress[tList.size()];
				for(Element ee : tList) {
					int index = Integer.valueOf(ee.attribute("id").getStringValue()) - 1;
					addrs[index] = new InetSocketAddress(ee.attribute("ip").getStringValue(), Integer.valueOf(ee.attribute("port").getStringValue()));
				}
				dataClient = new MemcachedClient(new ConnectFactory(), Arrays.asList(addrs));
				dataClient.addObserver(new CacheConnectionObserver());
				dataClientLength = addrs.length;
			}else if(e.getName().equals("lock")){
				List<Element> tList = e.elements();
				InetSocketAddress[] addrs = new InetSocketAddress[tList.size()];
				for(Element ee : tList) {
					addrs[Integer.valueOf(ee.attribute("id").getStringValue()) - 1] = new InetSocketAddress(ee.attribute("ip").getStringValue(), Integer.valueOf(ee.attribute("port").getStringValue()));
				}
				lockClient = new MemcachedClient(new ConnectFactory(), Arrays.asList(addrs));
				lockClient.addObserver(new CacheConnectionObserver());
				lockClientLength = addrs.length;
			}else if(e.getName().equals("sequence")){
				List<Element> tList = e.elements();
				SequenceFactory.initMemcachedClient(new InetSocketAddress(tList.get(0).attribute("ip").getStringValue(), Integer.valueOf(tList.get(0).attribute("port").getStringValue())));
			}else if(e.getName().equals("account")){
				
			}
		}
	}
	/**
	 * 重新初始化缓存连接
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public static void reInit() throws IOException, DocumentException {
		init();
	}
	/**
	 * 得到值
	 * @param cachedIndex 缓存的列表索引
	 * @param key
	 * @return
	 */
	public static byte[] get(int cachedIndex, String key) {
		if(key.indexOf("|") > 0) {
			throw new CyouSysException("memcached键值中不能包含|");
		}
		return (byte[])dataClient.get(cachedIndex + "|" + key);
	}
	
	/**
	 * 传入多个key，得到对应
	 * @param cachedIndex
	 * @param key
	 * @return
	 */
	public static Map<String,Object> get(int cacheIndex, String[] key) {
		String[] ss = new String[key.length];
		for(int i = 0; i < key.length; i++) {
			if(key[i].indexOf("|") > 0) {
				throw new CyouSysException("memcached键值中不能包含|");
			}
			ss[i] = cacheIndex + "|" + key[i];
		}
		return dataClient.getBulk(ss);
	}
	
	/**
	 * 传入多个key（包含缓存id信息），得到对应
	 * @param cachedIndex
	 * @param key
	 * @return
	 */
	public static Map<String,Object> get(String[] ss) {
		return dataClient.getBulk(ss);
	}
	
	
	
	public static void add(int cachedIndex, BaseObject obj) throws Exception {
		if(obj.getBaseObjectKey().indexOf("|") > 0) {
			throw new CyouSysException("memcached键值中不能包含|");
		}
		OperationFuture<Boolean> op = dataClient.set(cachedIndex + "|" + obj.getBaseObjectKey(), expTime, obj.encode());
		op.get();
	}
	
	private  void add(int cachedIndex,String key, byte[] obj) throws InterruptedException, ExecutionException {
		if(key.indexOf("|") > 0) {
			throw new CyouSysException("memcached键值中不能包含|");
		}
		OperationFuture<Boolean> op = dataClient.set(cachedIndex + "|" + key, expTime, obj);
		op.get();
	}
	
	private void delete(int cachedIndex, String key) throws InterruptedException, ExecutionException {
		if(key.indexOf("|") > 0) {
			throw new CyouSysException("memcached键值中不能包含|");
		}
		OperationFuture<Boolean> op = dataClient.delete(cachedIndex + "|" + key);
		op.get();
	}
	
	public static void lock(int type, int key) {
		for(int i = 0; i < 3; i++) {
			OperationFuture<Boolean> op = lockClient.add(lockKey(type, key), lockOverTime, 1);
			try {
				boolean a = op.get(100, TimeUnit.MILLISECONDS);
				if(a) {//此值是否已经锁定
					return;
				}else {//等待100毫秒
					Thread.sleep(100);
				}
			} catch (Exception e) {
				op.cancel(true);
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("读取缓存错误", e);
			} 
		}
		throw new CyouSysException("锁定超时");
	}
	
	public static void releaseLock(int type, int key) {
		for(int i = 0; i < 3; i++) {
			OperationFuture<Boolean> op = lockClient.delete(lockKey(type, key));
			try {
				op.get();
				break;
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("解除锁定错误", e);
			} 
		}
	}
	/**
	 * 生成锁的键值
	 * @param type
	 * @param key
	 * @return
	 */
	private static String lockKey(int type, int key) {
		String s = type + "_" + key;
		return DataClientHash.cacheHash4Lock(s.hashCode()) + "|" + s;
	}
	
	public static void main(String[] arg) {
		System.out.println(-3 % 2);
	}
	
	
}
