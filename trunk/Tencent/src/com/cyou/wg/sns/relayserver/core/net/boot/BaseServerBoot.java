package com.cyou.wg.sns.relayserver.core.net.boot;

public abstract class BaseServerBoot
{

	private int serverId;

	public BaseServerBoot()
	{
	}

	public abstract void init()
		throws Exception;

	public abstract void start()
		throws Exception;

	public int getServerId()
	{
		return serverId;
	}

	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}
}
