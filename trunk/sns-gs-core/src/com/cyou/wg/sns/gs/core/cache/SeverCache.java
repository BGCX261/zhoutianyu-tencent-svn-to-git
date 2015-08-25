package com.cyou.wg.sns.gs.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cyou.wg.sns.gs.core.domain.BaseInstanceObject;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.domain.UserHash;
/**
 * 进程缓存
 * 插入和更新都是直接向缓存中更新添加或更新数据
 * 当删除数据时，也将需要删除的缓存数据更新到缓存上，但是在逻辑端使用时判断如果
 * 数据的状态为delete,直接返回null
 * @author Administrator
 *
 */
public class SeverCache {
	
	private static SeverCache cache; 
	
	private Map<Integer, Map<String,Object>> localCacheMap = new ConcurrentHashMap<Integer, Map<String,Object>>();//玩家的缓存数据，此缓存只保存非交互数据
	
	private UserHash userHash;
	
	
	
	private SeverCache() {
		
	}
	
	public void setUserHash(UserHash userHash) {
		this.userHash = userHash;
	}

	
	public UserHash getUserHash() {
		return userHash;
	}

	public void init() {
		
		
	}
	
	public static SeverCache getInstance() {
		if(cache == null) {
			synchronized (SeverCache.class) {
				if(cache == null) {
					cache = new SeverCache();
				}
			}
		}
		return cache;
	}
	
	/**
	 * 得到玩家的缓存对象
	 * @param userId
	 * @param objKey
	 * @return
	 */
	public  BaseInstanceObject get(int userId, String objKey) {
		Map<String,Object> map = localCacheMap.get(userId);
		if(map != null) {
			BaseInstanceObject b = (BaseInstanceObject)map.get(objKey);
			if(b != null) {
				return b.clone();
			}
		}
		return null;
	}
	/**
	 * 更新内存中的玩家数据
	 * @param userId
	 * @param obj
	 */
	public void update(int userId, BaseInstanceObject obj) {
		if(!userHash.isHash(userId)) {
			return;
		}
		Map<String,Object> map = localCacheMap.get(userId);
		if(map == null) {
			map = new HashMap<String, Object>();
			localCacheMap.put(userId, map);
		}
		map.put(obj.getBaseObjectKey(), obj);
	}
	/**
	 * 移除玩家的数据缓存
	 * @param userId
	 */
	public void removeUserCache(int userId) {
		localCacheMap.remove(userId);
	}
	
	
	

}
