package com.cyou.wg.sns.gs.core.cache;

import java.util.List;
import java.util.Map;

import com.cyou.wg.sns.gs.core.domain.ThreadData;
import com.cyou.wg.sns.gs.core.protocol.RequestProtocol;
import com.cyou.wg.sns.gs.core.protocol.ResponseProtocol;
import com.cyou.wg.sns.gs.core.session.AppContext;

/**
 * 线程缓存
 * @author Administrator
 *
 */
public class ThreadLocalCache {
	
	public static ThreadLocal<RequestProtocol> currReq = new ThreadLocal<RequestProtocol>();//当前请求的参数
	public static ThreadLocal<List<ResponseProtocol>> responseList = new ThreadLocal<List<ResponseProtocol>>();//请求返回值缓存
	public static ThreadLocal<AppContext> httpContext = new ThreadLocal<AppContext>();//http请求的线程缓存
	public static ThreadLocal<ThreadData> data = new ThreadLocal<ThreadData>();//线程数据更新缓存
	
	
}
