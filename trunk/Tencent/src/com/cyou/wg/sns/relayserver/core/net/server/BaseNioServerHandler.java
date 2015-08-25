package com.cyou.wg.sns.relayserver.core.net.server;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import org.jboss.netty.channel.*;

public class BaseNioServerHandler extends SimpleChannelHandler
{

	public BaseNioServerHandler()
	{
	}

	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
		throws Exception
	{
		super.channelConnected(ctx, e);
		NioServer.allChannels.add(e.getChannel());
		if (e.getChannel() != null)
			LogFactory.getLogger("sys_info").info((new StringBuilder()).append("Connect channel to the server:").append(e.getChannel().getRemoteAddress().toString()).toString());
		else
			LogFactory.getLogger("sys_info").info("The channel has already connected");
	}

	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
		throws Exception
	{
		super.channelClosed(ctx, e);
		if (e.getChannel() != null)
			LogFactory.getLogger("sys_info").info((new StringBuilder()).append("Close channel to the server:").append(e.getChannel().getRemoteAddress().toString()).toString());
		else
			LogFactory.getLogger("sys_info").info("The channel has already closed");
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception
	{
		BaseRequestProtocol rep = (BaseRequestProtocol)e.getMessage();
		com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol res = rep.execute();
		if (res != null)
			e.getChannel().write(res);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
		throws Exception
	{
		LogFactory.getLogger("sys_error").error(e.getChannel().getRemoteAddress().toString(), e.getCause());
	}
}
