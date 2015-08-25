package com.cyou.wg.sns.relayserver.core.net.client;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.*;

public class BaseClientHeartBeatHandler extends IdleStateAwareChannelHandler
{

	private NioClient client;

	public BaseClientHeartBeatHandler()
	{
	}

	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
		throws Exception
	{
		super.channelIdle(ctx, e);
		if (e.getState() == IdleState.READER_IDLE)
		{
			if (e.getChannel() != null)
				e.getChannel().close();
			if (client == null)
				throw new Exception("client is null, it must be set");
			client.setConnected(false);
			LogFactory.getLogger("sys_info").info((new StringBuilder()).append("Channel with ").append(e.getChannel().getRemoteAddress()).append(" Disconnected").toString());
		} else
		if (e.getState() == IdleState.WRITER_IDLE)
			sendHeartBeatMsg(ctx, e);
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception
	{
		Object message = e.getMessage();
		if (message instanceof byte[])
		{
			byte mes[] = (byte[])(byte[])message;
			if (mes.length == 1 && mes[0] == NioClient.HeartBeat_MSG)
				return;
		}
		super.messageReceived(ctx, e);
	}

	public void sendHeartBeatMsg(ChannelHandlerContext ctx, IdleStateEvent e)
	{
		e.getChannel().write(new byte[] {
			NioClient.HeartBeat_MSG
		});
	}

	public NioClient getClient()
	{
		return client;
	}

	public void setClient(NioClient client)
	{
		this.client = client;
	}
}
