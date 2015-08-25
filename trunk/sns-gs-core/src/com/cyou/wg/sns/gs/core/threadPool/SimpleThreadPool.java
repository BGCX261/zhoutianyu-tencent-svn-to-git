package com.cyou.wg.sns.gs.core.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadPool implements ThreadPool{

	 private  ExecutorService threadPool;
	 
	 public SimpleThreadPool() {
		 threadPool =  new ThreadPoolExecutor(8, 16, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new createThreadFactory("CoreThreadPool"));
	 }
	 
	 public SimpleThreadPool(String poolName) {
		 threadPool =  new ThreadPoolExecutor(8, 16, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new createThreadFactory(poolName));
	 }
	 
	 class createThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        createThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,namePrefix + threadNumber.getAndIncrement(),0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
		
	}
	
	public void addTast(Runnable r) {
		threadPool.execute(r);
	}

}
