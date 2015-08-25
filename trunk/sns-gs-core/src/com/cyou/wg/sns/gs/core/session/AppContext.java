package com.cyou.wg.sns.gs.core.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;

public class AppContext {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AppContext(HttpServletRequest arg0, HttpServletResponse arg1) {
		this.request = arg0;
		this.response = arg1;
	}
	
	public static void init(HttpServletRequest arg0, HttpServletResponse arg1) {
		ThreadLocalCache.httpContext.remove();
		AppContext app = new AppContext(arg0, arg1);
		ThreadLocalCache.httpContext.set(app);
	}
	
	public static AppContext getAppContext() {
		return ThreadLocalCache.httpContext.get();
	}
	
	public HttpServletRequest getHttpServletRequest() {
		return request;
	}
	
	public HttpSession getHttpSession() {
		return request.getSession();
	}
	
	
	

}
