package com.cyou.wg.sns.gs.core.protocol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpMessageHandler {
	
	public Object beforeHandler(HttpServletRequest request,HttpServletResponse response);
	
	public Object handler(HttpServletRequest request);
	
	public Object afterHandler(HttpServletResponse response, Object obj) ;

}
