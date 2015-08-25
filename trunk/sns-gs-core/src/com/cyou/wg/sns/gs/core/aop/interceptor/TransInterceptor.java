package com.cyou.wg.sns.gs.core.aop.interceptor;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;
import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.event.EventListener;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.factory.log.LogicalLogFactory;
import com.cyou.wg.sns.gs.core.factory.threadData.ThreadDataFactory;
import com.cyou.wg.sns.gs.core.util.LockUtil;

/**
 * 事务拦截器(此事务为逻辑端事务)
 * 在里面实现
 * 开启事务
 * 提交事务
 * 事务回滚
 * @author Administrator
 *
 */
public class TransInterceptor implements MethodInterceptor{
	
	private static ThreadLocal<TransStatus> status = new ThreadLocal<TransStatus>();
	
	protected ThreadDataFactory threadDataFactory = new ThreadDataFactory();
	
	@Override
	public Object invoke(MethodInvocation arg0) throws Throwable {
		startTrans();
		try {
			return arg0.proceed();
		}catch (Exception e) {
			if(status.get().isSucc()) {
				status.get().fail();
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("TransInterceptor", e);
			}
			throw e;
		}finally {
			if(status.get().decCount() == 0) {
				if(status.get().isSucc()) {
					commit();
				}else {
					rollback();
				}
				status.remove();
				SqlMapClientMgr.closeCurr();
			}
		}
	}
	/**
	 * 是否已经开启事务
	 * @return
	 */
	public static boolean isStartTrans() {
		return status.get() != null;
	}
	/**
	 * 得到开启事务的纳秒数，如果没有开起事务返回-1
	 * @return
	 */
	public static long getTransStartNano() {
		return status.get() != null ? status.get().getStartTransTimeNano() : -1;
	}
	
	/**
	 * 开启事务
	 */
	protected void startTrans() {
		if(status.get() == null) {//如果没有开启事务，则开启事务
			status.set(new TransStatus());
			ThreadLocalCache.data.set(threadDataFactory.createNew());
			LogicalLogFactory.start();
			return;
		}
		status.get().incCount();//如果开启事务，则给事务层数增加1
	}
	/**
	 * 提交事务
	 */
	protected void commit() {
		if(ThreadLocalCache.data.get() != null) {
			if(LockUtil.isHasLock()) {
				ThreadLocalCache.data.get().commit();//更新缓存，更新db命令发送到远端
			}
			ThreadLocalCache.data.remove();
		}
		LockUtil.releaseLock();//释放锁
		LogicalLogFactory.commit();
		EventListener.commit();
	}
	/**
	 * 回滚
	 */
	protected void rollback() {
		ThreadLocalCache.data.remove();//TODO 清除线程缓存
		LockUtil.releaseLock();//释放锁
		LogicalLogFactory.rollBack();
	}
	
	private class TransStatus {
		/**
		 * 当前事务状态
		 */
		public static final int TRANS_STATUS_SUCC = 1;		//事务开启时默认为成功
		public static final int TRANS_STATUS_FAIL = 2;		//事务中间出现过失败，回滚
		private int status;
		private int transCount;//事务递进层数，第一次为1,每进入一层增加1，每推出一层减少1
		private long startTransTimeNano;//开启事务的纳秒数
		public long getStartTransTimeNano() {
			return startTransTimeNano;
		}
		public TransStatus() {
			this.status = TRANS_STATUS_SUCC;
			transCount = 1;
			startTransTimeNano = System.nanoTime();
		}
		/**
		 * 执行块是否成功
		 * @return
		 */
		public boolean isSucc() {
			return this.status != TRANS_STATUS_FAIL;
		}
		/**
		 * 事务执行失败
		 */
		public void fail() {
			this.status = TRANS_STATUS_FAIL;
		}
		/**
		 * 增加事务进入层数
		 */
		public void incCount() {
			this.transCount++;
		}
		/**
		 * 减少事务进入层数
		 * @return
		 */
		public int decCount() {
			return --this.transCount;
		}
		
	}

	

}
