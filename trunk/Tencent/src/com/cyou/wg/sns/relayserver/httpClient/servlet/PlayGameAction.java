package com.cyou.wg.sns.relayserver.httpClient.servlet;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import com.cyou.wg.sns.relayserver.core.net.server.NioServer;
import com.cyou.wg.sns.relayserver.httpClient.util.TencentUtil;
import com.cyou.wg.sns.relayserver.protocol.response.RespPlayGameMessage;
import com.cyou.wg.sns.relayserver.socketSvr.conf.GameSvrConfigMgr;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.netty.channel.Channel;

public class PlayGameAction
	implements Servlet
{

	public PlayGameAction()
	{
	}

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

	public void init(ServletConfig servletconfig)
		throws ServletException
	{
	}

	public void service(ServletRequest arg0, ServletResponse arg1)
		throws ServletException, IOException
	{
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
		String openkey = req.getParameter("openkey");
		String pf = req.getParameter("pf");
		String openid = req.getParameter("openid");
		String pfkey = req.getParameter("pfkey");
		if (!TencentUtil.isOpenid(openid))
		{
			LogFactory.getLogger("app_error").error((new StringBuilder()).append("openid�����Ϲ��� ��֤ʧ�� openid=").append(openid).toString());
			return;
		}
		if (NioServer.allChannels.size() == 0)
		{
			LogFactory.getLogger("sys_error").error("û��game server����socket������");
			return;
		}
		RespPlayGameMessage resPlayGameMessage = new RespPlayGameMessage();
		resPlayGameMessage.setOpenid(openid);
		resPlayGameMessage.setOpenkey(openkey);
		resPlayGameMessage.setPf(pf);
		resPlayGameMessage.setPfkey(pfkey);
		Channel loginChannel;
		for (Iterator itChannel = NioServer.allChannels.iterator(); itChannel.hasNext(); loginChannel.write(resPlayGameMessage))
			loginChannel = (Channel)itChannel.next();

		String url = (new StringBuilder()).append(GameSvrConfigMgr.getInstance().gameSvrConfig.getUrl()).append("?openid=").append(openid).append("&openkey=").append(openkey).toString();
		res.sendRedirect(url);
	}
}
