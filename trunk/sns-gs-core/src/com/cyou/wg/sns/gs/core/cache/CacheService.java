package com.cyou.wg.sns.gs.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyou.wg.sns.gs.core.domain.BaseInstanceObject;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
/**
 * 整体对外的缓存接口
 * @author Administrator
 *
 */
public class CacheService {
	/**
	 * 得到玩家缓存
	 * @param userId 玩家id
	 * @param cacheIndex 远端缓存id
	 * @param objType 存储类型
	 * @param objKey 键值
	 * @return
	 */
	public  static BaseInstanceObject get(int userId, int cacheIndex, int objType, String objKey){
		if(objType == BaseObject.STORAGE_TYPE_DOUBLE) {
			BaseInstanceObject obj = SeverCache.getInstance().get(userId, objKey);
			if(obj == null) {//如果服务器端缓存不存在，则从memcached中取值
				obj = RemoteCache.get(cacheIndex, objKey);
				if(obj != null) {
					SeverCache.getInstance().update(userId, obj);
				}
			}
			return obj;
		}else if(objType == BaseObject.STORAGE_TYPE_REMOTE) {
			return RemoteCache.get(cacheIndex, objKey);
		}else if(objType == BaseObject.STORAGE_TYPE_ONLY_LOCAL) {
			return SeverCache.getInstance().get(userId, objKey);
		}else {
			throw new CyouSysException("错误的对象存储类型：" + objType);
		}
	}
	
	
	/**
	 * 根据存储类型和key集合得到所有的key-value对应
	 * 如果key没有value则value为null
	 * @param userId
	 * @param objType
	 * @param objKey
	 * @return
	 */
	public static Map<String, BaseInstanceObject> getBulkObject(int userId, int cacheIndex ,int objType, String[] keys) {
		if(objType == BaseObject.STORAGE_TYPE_DOUBLE) {//如果是保存在本地内存的，首先在本地内存取值，之后剩下的再从远端缓存取值
			List<String> localNullKeys = null;
			BaseInstanceObject obj = null;
			Map<String, BaseInstanceObject> result = new HashMap<String, BaseInstanceObject>();
			for(int i = 0; i < keys.length; i++) {
				obj = SeverCache.getInstance().get(userId, keys[i]);
				if(obj == null) {
					if(localNullKeys == null) {
						localNullKeys = new ArrayList<String>();
					}
					localNullKeys.add(keys[i]);
				}else {
					result.put(keys[i], obj);
				}
			}
			if(localNullKeys != null && localNullKeys.size() > 0) {
				String[] tk = new String[localNullKeys.size()];
				for(int i = 0; i < localNullKeys.size(); i++) {
					tk[i] = localNullKeys.get(i);
				}
				Map<String, BaseInstanceObject> t = RemoteCache.get(cacheIndex, tk);
				result.putAll(t);
				for(Object b : t.values() ) {
					if(b != null) {
						SeverCache.getInstance().update(userId, (BaseInstanceObject) b);
					}
				}
			}
			return result;
		}else if(objType == BaseObject.STORAGE_TYPE_REMOTE) {//如果是只保存在远端缓存，则直接从远端缓存取值
			return RemoteCache.get(cacheIndex, keys);
		}else if(objType == BaseObject.STORAGE_TYPE_ONLY_LOCAL) {//只保存在本地
			BaseInstanceObject obj = null;
			Map<String, BaseInstanceObject> result = new HashMap<String, BaseInstanceObject>();
			for(int i = 0; i < keys.length; i++) {
				obj = SeverCache.getInstance().get(userId, keys[i]);
				result.put(keys[i], obj);
			}
			return result;
		}else {
			throw new CyouSysException("错误的对象存储类型：" + objType);
		}
	}
	/**
	 * 得到远端缓存
	 * @param cachedIndex 缓存定位
	 * @param keys 不带定位信息的键值列表
	 * @return
	 */
	public static Map<String, BaseInstanceObject> getRemoteBulkObject(int cacheIndex,String[] keys) {
		Map<String, BaseInstanceObject> t = RemoteCache.get(cacheIndex, keys);
		return t;
	}
	
	/**
	 * 得到远端缓存
	 * @param keys 带定位信息的键值列表
	 * @return
	 */
	public static Map<String, BaseInstanceObject> getRemoteBulkObject(String[] keys) {
		return RemoteCache.get(keys);
	}
	
	/**
	 * 更新缓存
	 * @param userId
	 * @param obj
	 */
	public static void update(BaseInstanceObject obj) {
		if(obj.getStorageType() == BaseObject.STORAGE_TYPE_DOUBLE) {
			SeverCache.getInstance().update(obj.getUserId(), obj);
			RemoteCache.update(obj.getCacheIndex(), obj);
		}else if(obj.getStorageType() == BaseObject.STORAGE_TYPE_REMOTE) {
			RemoteCache.update(obj.getCacheIndex(), obj);
		}else if(obj.getStorageType() == BaseObject.STORAGE_TYPE_ONLY_LOCAL) {
			SeverCache.getInstance().update(obj.getUserId(), obj);
		}
		else {
			throw new CyouSysException("错误的对象存储类型：" + obj.getBaseObjectType());
		}
	}

}
