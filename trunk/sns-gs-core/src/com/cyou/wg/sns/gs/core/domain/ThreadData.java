package com.cyou.wg.sns.gs.core.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommand;
import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommandList;
import com.cyou.wg.sns.gs.core.cache.CacheService;
import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;
import com.cyou.wg.sns.gs.core.dataSource.DataClientHash;
import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.net.DbSynClient;
import com.cyou.wg.sns.gs.core.protocol.ResponseProtocol;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;

public class ThreadData {
	/**
	 * 更新db的请求
	 */
	private Map<String,DbCommand> dbCommandMap = new LinkedHashMap<String, DbCommand>();
	/**
	 * 线程对象缓存，事务提交后更新进程缓存和远端缓存
	 */
	private Map<String, BaseInstanceObject> prepareUpdateCache = new HashMap<String, BaseInstanceObject>();
	/**
	 * 线程中保存展现对象的缓存
	 */
	private Map<String, BaseInstanceObject> viewCache = new HashMap<String, BaseInstanceObject>();
	/**
	 * 更新一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void update(int userId,String id,BaseInstanceObject obj) {
		onlyUpdateDb(userId, id, obj);
	    onlyUpdateCache(obj);
	}
	/**
	 * 只更新db
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void onlyUpdateDb(int userId,String id,BaseInstanceObject obj) {
		DbCommand command = createDbCommand(userId, id, obj);
	    DbCommand oldCom = dbCommandMap.get(obj.getBaseObjectKey());
	    
	    if(oldCom != null) {
	    	if(oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT) {
	    		oldCom.setNextCommand(command);
	    	}else if(oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_UPDATE) {
	    		dbCommandMap.put(obj.getBaseObjectKey(), command);
	    	}else if(oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_DEL) {
	    		if(oldCom.getNextCommand() != null && oldCom.getNextCommand().getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT) {//如果删除的子队列中存在插入命令，则直接替换原命令
	    			dbCommandMap.put(obj.getBaseObjectKey(), command);
				}
	    	}
	    }else {
	    	dbCommandMap.put(obj.getBaseObjectKey(), command);
	    }
	}
	/**
	 * 只更新缓存
	 * @param userId
	 * @param obj
	 */
	public  void onlyUpdateCache(BaseInstanceObject obj) {
		BaseObject oldObj = prepareUpdateCache.get(obj.getBaseObjectKey());
	    
	    if(oldObj != null) {
	    	if(oldObj.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT || oldObj.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_UPDATE) {
	    		prepareUpdateCache.put(obj.getBaseObjectKey(), obj);
	    	}else if(oldObj.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_DEL){
	    		return;//不做操作
	    	}
	    }else {
	    	prepareUpdateCache.put(obj.getBaseObjectKey(), obj);
	    }
	}
	
	/**
	 * 更新一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public static void updateImm(int userId,String id,BaseObject obj) {
		 DbCommand command = createDbCommand(userId, id, obj);
		 DbCommandList list = new DbCommandList();
		 list.getList().add(command);
		 try {
			DbSynClient.writeCommand(command.getDbIndex(), list);
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("更新远端db错误 :" + (command.getDbIndex() + 1), e);
		}
	}
	/**
	 * 插入一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void insert(int userId,String id,BaseInstanceObject obj) {
		onlyInsertDb(userId, id, obj);
		onlyInsertCache(obj);
	}
	/**
	 * 只插入db
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void onlyInsertDb(int userId,String id,BaseInstanceObject obj) {
		DbCommand oldCom = dbCommandMap.get(obj.getBaseObjectKey());
		if(oldCom != null) {
			if(oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_DEL) {
				DbCommand command = createDbCommand(userId, id, obj);
				oldCom.setNextCommand(command);
			}else {
				throw new CyouSysException("插入数据错误，有重复的数据：" + oldCom.getBaseObjectKey());
			}
		}else {
			DbCommand command = createDbCommand(userId, id, obj);
			dbCommandMap.put(obj.getBaseObjectKey(), command);
		}
	}
	/**
	 * 只向缓存中插入
	 * @param obj
	 */
	public void onlyInsertCache(BaseInstanceObject obj) {
		BaseObject oldObj = prepareUpdateCache.get(obj.getBaseObjectKey());
		if(oldObj != null) {
			if(oldObj.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_DEL) {
				prepareUpdateCache.put(obj.getBaseObjectKey(), obj);
			}else {
				throw new CyouSysException("插入数据错误，有重复的数据：" + oldObj.getBaseObjectKey());
			}
			
		}else {
			prepareUpdateCache.put(obj.getBaseObjectKey(), obj);
		}
	}
	/**
	 * 插入一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public static void insertImm(int userId,String id,BaseObject obj) {
		DbCommand command = createDbCommand(userId, id, obj);
		DbCommandList list = new DbCommandList();
		list.getList().add(command);
		try {
			DbSynClient.writeCommand(command.getDbIndex(), list);
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("更新远端db错误 :" + (command.getDbIndex() + 1), e);
		}
	}
	/**
	 * 删除一条数据
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void delete(int userId,String id,BaseInstanceObject obj) {
		onlyDeleteDb(userId, id, obj);
		onlyDeleteCache(obj);
	}
	/**
	 * 只删除db
	 * @param userId
	 * @param id
	 * @param obj
	 */
	public void onlyDeleteDb(int userId,String id,BaseInstanceObject obj) {
		DbCommand command = createDbCommand(userId, id, obj);
		DbCommand oldCom = dbCommandMap.get(obj.getBaseObjectKey());
		if(oldCom != null) {
			if(oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_INSERT) {
				dbCommandMap.remove(obj.getBaseObjectKey());
			}else if(oldCom.getBaseObjectOpt() == BaseObject.BASE_OBJ_OPT_UPDATE) {
				dbCommandMap.put(obj.getBaseObjectKey(), command);
			}
		}else {
			dbCommandMap.put(obj.getBaseObjectKey(), command);
		}
	}
	
	/**
	 * 只删除缓存
	 * @param obj
	 */
	public void onlyDeleteCache(BaseInstanceObject obj) {
		prepareUpdateCache.put(obj.getBaseObjectKey(), obj);
	}
	
	/**
	 * 向线程缓存中添加数据
	 * @param key
	 * @param obj
	 */
	public void add2View(String key, BaseInstanceObject obj) {
		viewCache.put(key, obj);
	}
	/**
	 * 从线程缓存中得到数据
	 * @param key
	 * @return
	 */
	public BaseInstanceObject get(String key) {
		BaseInstanceObject obj = prepareUpdateCache.get(key);
		if(obj == null) {
			obj = viewCache.get(key);
		}
		return obj;
	}
	/**
	 * 事务提交，更新数据层
	 */
	public void commit() {
		updateCache();
		updateDb();
	}
	/**
	 * 更新进程和远程缓存
	 */
	@SuppressWarnings("unchecked")
	private void updateCache() {
		Iterator<BaseInstanceObject> it = prepareUpdateCache.values().iterator();
		while(it.hasNext()) {
			BaseInstanceObject obj = it.next();
			/**
			 * 在返回值中插入更新
			 */
			Object response = obj.createResponseProtocol();
			if(response instanceof ResponseProtocol) {
				ThreadLocalCache.responseList.get().add((ResponseProtocol)response);
			}else if(response instanceof List) {
				ThreadLocalCache.responseList.get().addAll((List<ResponseProtocol>)response);
			}else if(response != null) {
				throw new CyouSysException("数据层更新参数类型错误，无法完成更新：" + response.getClass().getName());
			}
			//更新缓存
			if(obj.getBaseObjectOpt() != BaseObject.BASE_OBJ_OPT_DEL) {
				obj.setBaseObjectOpt(BaseObject.BASE_OBJ_OPT_NO);
			}
			CacheService.update(obj);
		}
	}
	/**
	 * 更新db
	 */
	private void updateDb() {
		if(DbSynClient.getClientNum() <= 0) {
			return;
		}
		DbCommandList[] arr = new DbCommandList[DbSynClient.getClientNum()];
		Iterator<DbCommand> it = dbCommandMap.values().iterator();
		while(it.hasNext()) {
			DbCommand comm = it.next();
			DbCommandList list = arr[comm.getDbIndex()];
			if(list == null) {
				list = new DbCommandList();
				arr[comm.getDbIndex()] = list;
			}
			list.getList().add(comm);
			if(comm.getNextCommand() != null) {
				list.getList().add(comm.getNextCommand());
			}
		}
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null) {
				try {
					if(LogFactory.getLogger(LogFactory.SYS_INFO_LOG).isInfoEnabled()) {
						arr[i].print();
					}
					DbSynClient.writeCommand((byte)i, arr[i]);
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("更新远端db错误 :" + (i + 1), e);
				}
			}
		}
	}
	/**
	 * 产生一个新的请求
	 * @param userId
	 * @param id
	 * @param obj
	 * @return
	 */
	private static DbCommand createDbCommand(int userId,String id,BaseObject obj) {
		MappedStatement ms = SqlMapClientMgr.getDefaultSqlMapClientImpl().getMappedStatement(id);
	    Object[] parameters = ms.getParameterMap().getParameterObjectValues(null, obj);
	    DbCommand command = new DbCommand();
	    command.setBaseSql(id);
	    command.setKey(obj.getBaseObjectKey());
	    command.setBaseObjectOpt(obj.getBaseObjectOpt());
	    command.setObjectType(obj.getBaseObjectType());
	    command.setParams(parameters);
	    command.setDbIndex((byte) DataClientHash.dbHash4User(userId));
	    return command;
	}

}
