package com.cyou.wg.sns.gs.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cyou.wg.sns.gs.core.protocol.ResponseProtocol;

/**
 * 玩家的消息缓存，保存需要被请求返回的非线程缓存内的数据
 * 比如异步执行结果
 * @author Administrator
 *
 */
public class UserMessageCache<T> {
	
	private static UserMessageCache<ResponseProtocol> res = null;
	
	public static UserMessageCache<ResponseProtocol> getInstance() {
		if(res == null) {
			synchronized (UserMessageCache.class) {
				if(res == null) {
					res = new UserMessageCache<ResponseProtocol>();
				}
			}
		}
		return res;
	}
	
	private UserMessageCache() {
		
	}
	
	private Map<Integer, MessageCache<T>> userMessageCache = new ConcurrentHashMap<Integer, MessageCache<T>>();
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<T> getMessage(int userId) {
		MessageCache<T> mc = userMessageCache.get(userId);
		if(mc == null) {
			return null; 
		}
		return mc.getMessage();
	}
	
	public void addMessage(int userId, T message) {
		MessageCache<T> mc = userMessageCache.get(userId);
		if(mc == null) {
			mc = new MessageCache<T>();
			userMessageCache.put(userId, mc);
		}
		mc.addMessage(message);
		
	}
	
    class MessageCache<T> {
		private List<T> receiveList = new ArrayList<T>();
		private Object lock = new Object();
		
		public List<T> getMessage() {
			if(receiveList.size() <= 0) {
				return null;
			}
			List<T> sendList = null;
			synchronized (lock) {
				sendList = receiveList;
				receiveList = new ArrayList<T>();
			}
			return sendList;
		}
		
		public synchronized void addMessage(T e) {
			receiveList.add(e);
		}
	}
}
