package com.cyou.wg.sns.gs.core.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyou.wg.sns.gs.core.cache.CacheService;
import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;
import com.cyou.wg.sns.gs.core.dataSource.DataClientHash;
import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.domain.BaseInstanceObject;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.domain.ThreadData;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.net.CachedClient;

public class CoreDaoImplRemote implements CoreDao{
	/**
	 * 更新一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void update(int userId, String id, BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseInstanceObject.BASE_OBJ_OPT_UPDATE);
		ThreadLocalCache.data.get().update(userId, id, obj);
	}
	
	/**
	 * 只更新Db
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void onlyUpdateDb(int userId, String id, BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseObject.BASE_OBJ_OPT_UPDATE);
		ThreadLocalCache.data.get().onlyUpdateDb(userId, id, obj);
	}
	
	/**
	 * 只更新缓存
	 * @param userId
	 * @param obj
	 */
	public void onlyUpdateCache(BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseObject.BASE_OBJ_OPT_UPDATE);
		ThreadLocalCache.data.get().onlyUpdateCache(obj);
	}
	
	/**
	 * 插入一条新数据
	 * 主键在逻辑中由序列发生器生成，或者按照联合主键的方式计算生成
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void insert(int userId, String id, BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseInstanceObject.BASE_OBJ_OPT_INSERT);
		ThreadLocalCache.data.get().insert(userId, id, obj);
	}
	
	/**
	 * 只向Db中插入
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void onlyInsertDb(int userId, String id, BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseObject.BASE_OBJ_OPT_INSERT);
		ThreadLocalCache.data.get().onlyInsertDb(userId, id, obj);
	}
	
	/**
	 * 只向缓存中插入
	 * @param obj
	 */
	public void onlyInsertCache(BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseObject.BASE_OBJ_OPT_INSERT);
		ThreadLocalCache.data.get().onlyInsertCache(obj);
	}
	
	/**
	 * 删除一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void delete(int userId, String id, BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseInstanceObject.BASE_OBJ_OPT_DEL);
		ThreadLocalCache.data.get().delete(userId, id, obj);
	}
	
	/**
	 * 只删除Db
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void onlyDeleteDb(int userId, String id, BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseInstanceObject.BASE_OBJ_OPT_DEL);
		ThreadLocalCache.data.get().onlyDeleteDb(userId, id, obj);
	}
	
	/**
	 * 只删除缓存
	 * @param obj
	 */
	public void onlyDeleteCache(BaseInstanceObject obj) {
		obj.setBaseObjectOpt(BaseObject.BASE_OBJ_OPT_DEL);
		ThreadLocalCache.data.get().onlyDeleteCache(obj);
	}
	/**
	 * 从缓存得到数据,如果是已经删除的数据，缓存也将其保留，但是在具体逻辑中对缓存状态进行判断
	 * @param userId
	 * @param objType
	 * @param key
	 * @return
	 */
	public BaseInstanceObject getBaseObjectFromCache(int userId, int objType ,String key) {
		ThreadData td = ThreadLocalCache.data.get();
		if(td == null) {
			td = new ThreadData();
			ThreadLocalCache.data.set(td);
		}
		BaseInstanceObject obj = td.get(key);
		if(obj != null) {
			return obj;
		}
		obj = CacheService.get(userId,DataClientHash.cacheHash4User(userId),objType, key);
		if(obj != null) {
			td.add2View(key, obj);
		}
		//（从db得到数，此操作不在这里做，在coreDao的子类中根据具体逻辑实现，但是原则上需要根据数据的存储类型
		//放到不同 的缓存里，必须放到远程缓存和线程缓存中，进程内缓存根据数据的类型进行区分）
		return obj;
	}
	/**
	 * 针对同一类型的数据，进行批量查询
	 * @param userId
	 * @param objType
	 * @param keys 进行批量查询的key数组
	 * @return
	 */
	public Map<String, BaseObject> getBulkFromCache(int userId, int objType, String[] keys) {
		ThreadData td = ThreadLocalCache.data.get();
		if(td == null) {
			td = new ThreadData();
			ThreadLocalCache.data.set(td);
		}
		Map<String, BaseObject> result = new HashMap<String, BaseObject>();
		List<String> localNullKeys = null;
		BaseObject obj = null;
		for(int i = 0; i < keys.length; i++) {
			obj = td.get(keys[i]);
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
			Map<String, BaseInstanceObject> t = CacheService.getBulkObject(userId, DataClientHash.cacheHash4User(userId) ,objType, tk);
			result.putAll(t);
		}
		return result;
	}
	/**
	 * 从远端缓存得到多个值
	 * @param cacheIndex
	 * @param keys 不带缓存定位信息
	 * @return
	 */
	public Map<String, BaseInstanceObject> getBulkFromRemoteCache(int cacheIndex,  String[] keys) {
		return CacheService.getRemoteBulkObject(cacheIndex, keys);
	}
	/**
	 * 从远端缓存得到多个值
	 * @param keys 带缓存定位信息
	 * @return
	 */
	public Map<String, BaseInstanceObject> getBulkFromRemoteCache(String[] keys) {
		return CacheService.getRemoteBulkObject(keys);
	}
	
	/**
	 * 把数据添加到缓存
	 * @param userId
	 * @param objType
	 * @param obj
	 */
	public void add2Cache(int userId,BaseInstanceObject obj) {
		this.add2Cache(obj);
	}
	/**
	 * 把数据添加到缓存
	 * @param obj
	 */
	public void add2Cache(BaseInstanceObject obj) {
		ThreadData td = ThreadLocalCache.data.get();
		if(td == null) {
			td = new ThreadData();
			ThreadLocalCache.data.set(td);
		}
		td.add2View(obj.getBaseObjectKey(), obj);
		CacheService.update(obj);
	}
	
	/**
	 * 从db得到数据，列表数据
	 * @param userId
	 * @param id
	 * @param para
	 * @return
	 * @throws SQLException 
	 */
	public List queryForListFromDb(int userId,String id, Object paramObject) {
		try {
			List list = SqlMapClientMgr.getSqlMap(DataClientHash.dbHash4User(userId)).queryForList(id, paramObject);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CyouSysException("queryForListFromDb", e);
		}
	}
	/**
	 * 从db得到数据，单行数据
	 * @param userId
	 * @param id
	 * @param paramObject
	 * @return
	 */
	public Object queryForObjectFromDb(int userId, String id, Object paramObject) {
		try {
			Object obj = SqlMapClientMgr.getSqlMap(DataClientHash.dbHash4User(userId)).queryForObject(id, paramObject);
			return obj;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CyouSysException("queryForObjectFromDb", e);
		}
	}

}
