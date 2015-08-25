package com.cyou.wg.sns.relayserver.core.net.server;

import com.cyou.wg.sns.relayserver.core.protocol.codec.ByteArrayDecoder;
import com.cyou.wg.sns.relayserver.core.protocol.codec.ByteArrayEncoder;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

public class BaseServerChannelPipelineFactory implements ChannelPipelineFactory
{

	public static final int CHANNLE_READ_TIMEOUT = 30;
	private BaseNioServerHandler logicHandler;
	private HashedWheelTimer timer;
	private IdleStateHandler idleStateHandler;

	public BaseServerChannelPipelineFactory()
	{
		logicHandler = new BaseNioServerHandler();
		timer = new HashedWheelTimer();
	}

	public BaseServerChannelPipelineFactory(BaseNioServerHandler logicHandler)
	{
		this(logicHandler, 30);
	}

	public BaseServerChannelPipelineFactory(BaseNioServerHandler logicHandler, int channelTimeout)
	{
		this.logicHandler = new BaseNioServerHandler();
		timer = new HashedWheelTimer();
		this.logicHandler = logicHandler;
		idleStateHandler = new IdleStateHandler(timer, channelTimeout, 0, 0);
	}

	public ChannelPipeline getPipeline()
		throws Exception
	{
		ChannelPipeline p = Channels.pipeline();
		p.addLast("encoder", new ByteArrayEncoder());
		p.addLast("decoder", new ByteArrayDecoder());
		p.addLast("logic", logicHandler);
		return p;
	}

	public void releaseExternalResources()
	{
		if (idleStateHandler != null)
			idleStateHandler.releaseExternalResources();
	}

	public BaseNioServerHandler getLogicHandler()
	{
		return logicHandler;
	}

	public void setLogicHandler(BaseNioServerHandler logicHandler)
	{
		this.logicHandler = logicHandler;
	}

	public IdleStateHandler getIdleStateHandler()
	{
		return idleStateHandler;
	}

	public void setIdleStateHandler(IdleStateHandler idleStateHandler)
	{
		this.idleStateHandler = idleStateHandler;
	}
}
