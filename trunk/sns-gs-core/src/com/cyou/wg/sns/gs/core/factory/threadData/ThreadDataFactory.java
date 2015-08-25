package com.cyou.wg.sns.gs.core.factory.threadData;

import com.cyou.wg.sns.gs.core.domain.ThreadData;

/**
 * 线程数据层
 * @author Administrator
 *
 */
public class ThreadDataFactory {
	
	public ThreadData createNew() {
		return new ThreadData();
	}
}
