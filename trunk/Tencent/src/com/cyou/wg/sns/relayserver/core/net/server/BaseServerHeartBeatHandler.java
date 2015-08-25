package com.cyou.wg.sns.relayserver.core.net.server;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import com.cyou.wg.sns.relayserver.core.net.client.NioClient;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.*;

public class BaseServerHeartBeatHandler extends IdleStateAwareChannelHandler
{

	public BaseServerHeartBeatHandler()
	{
	}

	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
		throws Exception
	{
		super.channelIdle(ctx, e);
		if (e.getState() == IdleState.READER_IDLE)
		{
			e.getChannel().close();
			LogFactory.getLogger("sys_warn").warn((new StringBuilder()).append("Channel").append(e.getChannel().getId()).append("Disconnected").toString());
		}
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception
	{
		Object message = e.getMessage();
		if (message instanceof byte[])
		{
			byte mes[] = (byte[])(byte[])message;
			if (mes.length == 1 && mes[0] == NioClient.HeartBeat_MSG)
			{
				e.getChannel().write(new byte[] {
					NioClient.HeartBeat_MSG
				});
				return;
			}
		}
		super.messageReceived(ctx, e);
	}
}
