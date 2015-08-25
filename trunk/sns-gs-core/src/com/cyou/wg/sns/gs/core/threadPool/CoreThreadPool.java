package com.cyou.wg.sns.gs.core.threadPool;

public class CoreThreadPool {
	public static ThreadPool threadpool;
	
	public static void init() {
		threadpool = new SimpleThreadPool();
	}
	
	public static void addTast(Runnable r) {
		threadpool.addTast(r);
	}
	
	

}
