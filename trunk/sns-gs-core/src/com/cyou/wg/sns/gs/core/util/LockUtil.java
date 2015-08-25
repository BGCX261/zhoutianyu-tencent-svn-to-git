package com.cyou.wg.sns.gs.core.util;

import com.cyou.wg.sns.gs.core.aop.interceptor.TransInterceptor;
import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;
import com.cyou.wg.sns.gs.core.domain.ThreadData;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.threadData.ThreadDataFactory;
import com.cyou.wg.sns.gs.core.net.CachedClient;

/**
 * 只有处理玩家间交互的资源时才需要锁
 * @author Administrator
 *
 */
public class LockUtil {
	
	public static int LOCK_TYPE_USER = 0;//锁类型：玩家
	
//	public static int LOCK_TYPE_SYS = 1;//锁类型：系统
	
	private static int MAX_LOCK_TYPE = 1;//锁的类型数量
	
	private static int MAX_LOCK_NUM = 20;//每种资源最多锁定数量
	
	private static ThreadLocal<ThreadLock> l = new ThreadLocal<ThreadLock>();//本地线程锁记录
	
	public static ThreadDataFactory threadDataFactory = new ThreadDataFactory();
	
	public static void lockUser(int userId) {
		lock(LOCK_TYPE_USER, userId);
	}
	/**
	 * 
	 * @param userId1
	 * @param userId2
	 */
	public static void lockUser(int userId1, int userId2) {
		if(userId1 < userId2) {
			lockUser(userId1);
			lockUser(userId2);
		}else {
			lockUser(userId2);
			lockUser(userId1);
		}
	}
	
	/**
	 * 锁定资源
	 * 当需要更新玩家资源时需要锁定远程资源
	 * 使用专门的锁缓存
	 * 锁定多个时，先锁小的，后锁大的
	 * @param type 锁类型
	 * @param key 锁的资源名
	 */
	public static void lock(int type,int key) {
		if(TransInterceptor.isStartTrans()) {//只有开启事务，才进行锁操作
			ThreadLock lc = l.get();
			if(lc == null) {
				lc = new ThreadLock();
				l.set(lc);
				ThreadLocalCache.data.set(threadDataFactory.createNew());//清除锁定前的线程缓存
			}
			executeLock(type, key);
		}
	}
	
	public static boolean isHasLock() {
		return l.get() != null;
	}
	
	/**
	 * 执行锁定
	 * @param type
	 * @param key
	 */
	public static void executeLock(int type, int key) {
		if(!isLock(type, key)) {
			l.get().lock(type, key);//锁定本地，判断此资源是否被
			CachedClient.lock(type, key);//锁定远程资源
		}
	}
	/**
	 * 判断此资源是否被锁定，此方法只在事务提交时进行判断调用
	 * @param type
	 * @param key
	 * @return
	 */
	public static boolean isLock(int type, int key) {
		if(l.get() != null) {
			Integer[][] locks = l.get().locks;
			Integer[] si = locks[type];
			if(si != null) {
				for(int i = 0; i < si.length; i++) {
					if(si[i] != null && si[i] == key) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 释放锁定的资源，在事务提交最后执行
	 */
	public static void releaseLock() {
		if(l.get() != null) {
			Integer[][] locks = l.get().locks;
			l.remove();
			for(int i = 0; i < locks.length; i++) {
				Integer[] ids = locks[i];
				if(ids != null) {
					for(Integer id : ids) {
						if(id != null) {
							CachedClient.releaseLock(i, id);
						}
					}
				}
			}
		}
	}
	
	static class ThreadLock {
		Integer[][] locks = new Integer[MAX_LOCK_TYPE][MAX_LOCK_NUM];
		void lock(int type,int key) {
			Integer[] s = locks[type];
			if(s == null) {
				s = new Integer[MAX_LOCK_NUM];
				s[0] = key;
			}else {
				for(int i = 0; i < s.length; i++) {
					if(s[i] != null && s[i] > key) {
						throw new CyouSysException("锁定错误：type :" + type + " 已经锁定了：" + s[i] + " 还要锁定：" + key);
					}else if(s[i] == null) {
						s[i] = key;
						break;
					}else if(i == s.length - 1) {
						throw new CyouSysException("锁定的资源过多 type：" + type);
					}
				}
			}
		}
	}

}
