package com.cyou.wg.sns.gs.core.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import com.cyou.wg.sns.gs.core.domain.BaseInstanceObject;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.domain.BaseObjectConfig;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.net.CachedClient;

/**
 * memcached缓存
 * @author Administrator
 *
 */
public class RemoteCache {
	
	private static BaseObjectConfig baseObjectConfig;
	
	
	
	public static void setBaseObjectConfig(BaseObjectConfig baseObjectConfig) {
		RemoteCache.baseObjectConfig = baseObjectConfig;
	}
	
	public  static BaseInstanceObject get(int cacheIndex, String objKey) {
		try {
			byte[] b = CachedClient.get(cacheIndex,objKey);
			if(b == null || b.length <= 0) {
				return null;
			}
			IoBuffer buff = IoBuffer.wrap(b);
			BaseInstanceObject obj = baseObjectConfig.getBaseObjectById(buff.getShort());
			obj.decode(buff);
			return obj;
		}catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("RemoteCache.decode", e);
			return null;
		}
		
	}
	/**
	 * 从一个远程缓存得到数据
	 * @param cacheIndex
	 * @param keys
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, BaseInstanceObject> get(int cacheIndex, String[] keys) {
		Map map = CachedClient.get(cacheIndex, keys);
		Map<String, BaseInstanceObject> result = new HashMap<String, BaseInstanceObject>();
		for(int i = 0; i < keys.length; i++) {
			Object obj = map.get(cacheIndex + "|" + keys[i]);
			if(obj != null) {
				IoBuffer buff = IoBuffer.wrap((byte[])obj);
				BaseInstanceObject b;
				try {
					b = baseObjectConfig.getBaseObjectById(buff.getShort());
					result.put(keys[i], (BaseInstanceObject) b.decode(buff));
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("RemoteCache.decode", e);
					obj = null;
				} 
			}
		}
		return result;
	}
	
	/**
	 * 从多个远程缓存得到数据
	 * @param keys 带缓存定位信息的key
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, BaseInstanceObject> get(String[] keys) {
		Map map = CachedClient.get(keys);
		Map<String, BaseInstanceObject> result = new HashMap<String, BaseInstanceObject>();
		for(int i = 0; i < keys.length; i++) {
			Object obj = map.get(keys[i]);
			if(obj != null) {
				IoBuffer buff = IoBuffer.wrap((byte[])obj);
				BaseInstanceObject b;
				try {
					b = baseObjectConfig.getBaseObjectById(buff.getShort());
					result.put(keys[i], (BaseInstanceObject) b.decode(buff));
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("RemoteCache.decode", e);
					obj = null;
				} 
			}
		}
		return result;
	}
	
	

	
	public static void update(int cacheIndex, BaseInstanceObject obj) {
		try {
			CachedClient.add(cacheIndex,obj);
		}catch (Exception e) {
			try {
				CachedClient.add(cacheIndex,obj);
			} catch (Exception e1) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("RemoteCache.update", e);
				throw new CyouSysException("RemoteCache", e);
			}
		}
	}
	
	


}
