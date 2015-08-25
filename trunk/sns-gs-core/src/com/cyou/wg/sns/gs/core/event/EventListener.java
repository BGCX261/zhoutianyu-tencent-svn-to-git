package com.cyou.wg.sns.gs.core.event;

import java.util.ArrayList;
import java.util.List;

import com.cyou.wg.sns.gs.core.aop.interceptor.TransInterceptor;
import com.cyou.wg.sns.gs.core.threadPool.CoreThreadPool;

public class EventListener {
	private static ThreadLocal<List<BaseEvent>> t = new ThreadLocal<List<BaseEvent>>();
	
	public static void addEvent(BaseEvent baseEvent) {
		baseEvent.synExecute();
		if(baseEvent.isNeedUnSyn()) {
			if(TransInterceptor.isStartTrans()) {
				if(t.get() == null) {
					t.set(new ArrayList<BaseEvent>());
				}
				baseEvent.setException();
				t.get().add(baseEvent);
			}else {
				baseEvent.setException();
				CoreThreadPool.addTast(new ExecuteEvent(baseEvent));
			}
		}
	}
	
	public static void commit() {
		List<BaseEvent> list = t.get();
		if(list != null && list.size() > 0) {
			t.remove();
			for(BaseEvent be : list) {
				CoreThreadPool.addTast(new ExecuteEvent(be));
			}
		}
	}
}
