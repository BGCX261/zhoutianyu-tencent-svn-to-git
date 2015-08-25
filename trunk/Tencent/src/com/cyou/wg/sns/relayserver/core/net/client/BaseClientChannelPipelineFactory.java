package com.cyou.wg.sns.relayserver.core.net.client;

import com.cyou.wg.sns.relayserver.core.protocol.codec.ByteArrayDecoder;
import com.cyou.wg.sns.relayserver.core.protocol.codec.ByteArrayEncoder;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.HashedWheelTimer;

public class BaseClientChannelPipelineFactory
	implements ChannelPipelineFactory, ExternalResourceReleasable
{

	public static final int CHANNEL_READ_TIMEOUT = 30;
	public static final int HEARBEAT_INTERVAL = 3;
	private BaseNioClientHandler logicHandler;
	private BaseClientHeartBeatHandler heartBeatHandler;
	private HashedWheelTimer timer;
	private IdleStateHandler idleStateHandler;

	public BaseClientChannelPipelineFactory()
	{
		logicHandler = new BaseNioClientHandler();
		heartBeatHandler = new BaseClientHeartBeatHandler();
		timer = new HashedWheelTimer();
	}

	public BaseClientChannelPipelineFactory(BaseNioClientHandler logicHandler, BaseClientHeartBeatHandler heartBeatHandler)
	{
		this(logicHandler, heartBeatHandler, 30, 3);
	}

	public BaseClientChannelPipelineFactory(BaseNioClientHandler logicHandler, BaseClientHeartBeatHandler heartBeatHandler, int readerTimeout, int heartBeatInterval)
	{
		this.logicHandler = new BaseNioClientHandler();
		this.heartBeatHandler = new BaseClientHeartBeatHandler();
		timer = new HashedWheelTimer();
		this.logicHandler = logicHandler;
		this.heartBeatHandler = heartBeatHandler;
		idleStateHandler = new IdleStateHandler(timer, readerTimeout, heartBeatInterval, 0);
	}

	public ChannelPipeline getPipeline()
		throws Exception
	{
		ChannelPipeline p = Channels.pipeline();
		p.addLast("encoder", new ByteArrayDecoder());
		p.addLast("decoder", new ByteArrayEncoder());
		p.addLast("timeout", idleStateHandler);
		p.addLast("hearbeat", heartBeatHandler);
		p.addLast("logic", logicHandler);
		return p;
	}

	public void releaseExternalResources()
	{
		if (idleStateHandler != null)
			idleStateHandler.releaseExternalResources();
	}

	public BaseNioClientHandler getLogicHandler()
	{
		return logicHandler;
	}

	public void setLogicHandler(BaseNioClientHandler logicHandler)
	{
		this.logicHandler = logicHandler;
	}

	public BaseClientHeartBeatHandler getHeartBeatHandler()
	{
		return heartBeatHandler;
	}

	public void setHeartBeatHandler(BaseClientHeartBeatHandler heartBeatHandler)
	{
		this.heartBeatHandler = heartBeatHandler;
	}
}
