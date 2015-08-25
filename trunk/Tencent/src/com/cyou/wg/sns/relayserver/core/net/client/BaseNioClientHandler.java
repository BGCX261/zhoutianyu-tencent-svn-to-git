package com.cyou.wg.sns.relayserver.core.net.client;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import org.jboss.netty.channel.*;

public class BaseNioClientHandler extends SimpleChannelHandler
{

	protected NioClient client;

	public BaseNioClientHandler()
	{
	}

	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
		throws Exception
	{
		LogFactory.getLogger("sys_info").info((new StringBuilder()).append("Connect channel to the Server: ").append(e.getChannel().getRemoteAddress().toString()).toString());
		super.channelConnected(ctx, e);
	}

	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
	{
		LogFactory.getLogger("sys_info").info((new StringBuilder()).append("Open channel to the server:").append(client.getHost()).append(":").append(client.getPort()).toString());
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception
	{
		byte src[] = (byte[])(byte[])e.getMessage();
		execute(e.getChannel(), src);
	}

	public void execute(Channel channel1, Object obj)
		throws Exception
	{
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		if (e.getChannel() != null)
			LogFactory.getLogger("sys_error").error(client.getClientName(), e.getCause());
		else
			LogFactory.getLogger("sys_error").error(client.getClientName(), e.getCause());
	}

	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
	{
		LogFactory.getLogger("sys_info").info((new StringBuilder()).append("Disconnect channel to the server:").append(e.getChannel().getRemoteAddress().toString()).toString());
		client.setConnected(false);
	}

	public void setClient(NioClient client)
	{
		this.client = client;
	}
}
