package com.cyou.wg.sns.ctsvr.core.util;


public class ClientProperties
{

	private int id;
	private String type;
	private int serverId;
	private int threadNum;
	private String handler;
	private int timeOutCheckInterval;
	private int timeOut;

	public int getTimeOutCheckInterval()
	{
		return timeOutCheckInterval;
	}

	public void setTimeOutCheckInterval(int timeOutCheckInterval)
	{
		this.timeOutCheckInterval = timeOutCheckInterval;
	}

	public int getTimeOut()
	{
		return timeOut;
	}

	public void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public int getServerId()
	{
		return serverId;
	}

	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}

	public int getThreadNum()
	{
		return threadNum;
	}

	public void setThreadNum(int threadNum)
	{
		this.threadNum = threadNum;
	}

	public String getHandler()
	{
		return handler;
	}

	public void setHandler(String handler)
	{
		this.handler = handler;
	}
}
