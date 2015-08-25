package com.cyou.wg.sns.relayserver.web;

import com.cyou.wg.sns.relayserver.core.bean.BeanFactory;
import com.cyou.wg.sns.relayserver.core.util.ClassPath;
import com.cyou.wg.sns.relayserver.httpClient.api.API4Tencent;
import com.cyou.wg.sns.relayserver.socketSvr.conf.GameSvrConfigMgr;
import com.cyou.wg.sns.relayserver.socketSvr.conf.ProtocolConfig;
import com.cyou.wg.sns.relayserver.socketSvr.net.boot.Boot;
import java.io.IOException;
import javax.servlet.*;

public class InitServlet implements Servlet
{

	public void destroy()
	{
	}

	public ServletConfig getServletConfig()
	{
		return null;
	}

	public String getServletInfo()
	{
		return null;
	}

	public void init(ServletConfig arg0) throws ServletException
	{
		try
		{
			Boot.getInstance().start();
			ProtocolConfig.getInstance().init(ClassPath.createRelative("/protocol"));
			BeanFactory.getInstance().init(ClassPath.createRelative("/bean"));
			GameSvrConfigMgr.getInstance().init(ClassPath.createRelative("/gameSvr"));
			API4Tencent.init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void service(ServletRequest servletrequest, ServletResponse servletresponse)
		throws ServletException, IOException
	{
	}
}
