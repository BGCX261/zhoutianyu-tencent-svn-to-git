package com.cyou.wg.sns.gs.core.factory.log;

import java.util.ArrayList;
import java.util.List;

import com.cyou.wg.sns.ctsvr.app.log2File.messageHandler.LogicalBaseLog;
import com.cyou.wg.sns.ctsvr.app.log2File.messageHandler.LogicalLogList;
import com.cyou.wg.sns.gs.core.net.LogServerClient;

public class LogicalLogFactory {
	private static ThreadLocal<List<LogicalBaseLog>> transData = new ThreadLocal<List<LogicalBaseLog>>();
	/**
	 * 在事务中添加日志，最后事务提交时统一进行提交
	 */
	public static void addLogicalLogWithTrans(String accId, int userId, String logName, int reason, Object...objects) {
		LogicalBaseLog log = new LogicalBaseLog(accId,userId, logName, reason,objects);
		transData.get().add(log);
	}
	/**
	 * 事务提交时调用
	 */
	public static void commit() {
		if(transData.get() == null) {
			return;
		}
		List<LogicalBaseLog> srcList = transData.get();
		LogicalLogList[]  arr = new LogicalLogList[LogServerClient.getClientNum()];
		for(LogicalBaseLog log : srcList) {
			int index = LogServerClient.logicalSvrHash(log.getUserId());
			LogicalLogList tList = arr[index];
			if(tList == null) {
				tList = new LogicalLogList();
				arr[index] = tList;
			}
			tList.add(log);
		}
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null) {
				try {
					LogServerClient.sendLogical(i, arr[i]);
				} catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("添加逻辑日志出错 :" + (i + 1), e);
				}
			}
		}
		transData.remove();
	}
	/**
	 * 开启事务时调用
	 */
	public static void start() {
		transData.set(new ArrayList<LogicalBaseLog>());
	}
	/**
	 * 事务回滚时调用
	 */
	public static void rollBack() {
		transData.remove();
	}
	/**
	 * 不在事务中添加逻辑日志，慎用
	 * @param userId
	 * @param logNameId
	 * @param objects
	 */
	public static void addLogicalLogImm(String accId, int userId, String logName, int reason,Object...objects) {
		LogicalBaseLog log = new LogicalBaseLog(accId,userId, logName, reason ,-1, objects);
		LogicalLogList list = new LogicalLogList();
		list.add(log);
		try {
			LogServerClient.sendLogical(LogServerClient.logicalSvrHash(userId), list);
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("添加逻辑日志出错 :" + (LogServerClient.logicalSvrHash(userId) + 1), e);
		}
	}

}
