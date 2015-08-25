package com.cyou.wg.sns.gs.core.dao;

import java.sql.SQLException;
import java.util.List;

import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;
import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.domain.BaseInstanceObject;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;


public interface CoreDao {
	
	/**
	 * 更新一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void update(int userId, String id, BaseInstanceObject obj);
	/**
	 * 插入一条新数据
	 * 主键在逻辑中由序列发生器生成，或者按照联合主键的方式计算生成
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void insert(int userId, String id, BaseInstanceObject obj) ;
	/**
	 * 删除一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void delete(int userId, String id, BaseInstanceObject obj) ;
	/**
	 * 从缓冲得到数据
	 * @return
	 */
	public BaseInstanceObject getBaseObjectFromCache(int userId, int objType, String key);
	/**
	 * 从db得到数据，列表数据
	 * @param userId
	 * @param id
	 * @param para
	 * @return
	 * @throws SQLException 
	 */
	public List queryForListFromDb(int userId,String id, Object paramObject);
	/**
	 * 从db得到数据，单行数据
	 * @param userId
	 * @param id
	 * @param paramObject
	 * @return
	 */
	public Object queryForObjectFromDb(int userId, String id, Object paramObject);
	
	

}
