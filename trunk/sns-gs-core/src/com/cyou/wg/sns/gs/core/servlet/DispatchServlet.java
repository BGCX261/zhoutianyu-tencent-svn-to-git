package com.cyou.wg.sns.gs.core.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyou.wg.sns.gs.core.protocol.DefaultHttpMessageHandler;
import com.cyou.wg.sns.gs.core.protocol.HttpMessageHandler;

public class DispatchServlet implements Servlet{

	
	protected HttpMessageHandler handler;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		handler = new DefaultHttpMessageHandler();
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		handler.beforeHandler((HttpServletRequest)arg0, (HttpServletResponse)arg1);
		Object obj = handler.handler((HttpServletRequest)arg0);
		handler.afterHandler((HttpServletResponse)arg1, obj);
	}
	

}
