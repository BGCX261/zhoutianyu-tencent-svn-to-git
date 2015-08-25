package com.cyou.wg.sns.ctsvr.core.util;

public class ServerProperties
{

	private int id;
	private String type;
	private String ip;
	private int port;
	private int threadNum;
	private String handler;
	private int workThreadNum;
	private int interval;
	private String tmpFielPath;
	private int createFileInterval;
	private int readFileInterval;
	private int delFileInterval;

	public String getTmpFielPath()
	{
		return tmpFielPath;
	}

	public void setTmpFielPath(String tmpFielPath)
	{
		this.tmpFielPath = tmpFielPath;
	}

	public int getCreateFileInterval()
	{
		return createFileInterval;
	}

	public void setCreateFileInterval(int createFileInterval)
	{
		this.createFileInterval = createFileInterval;
	}

	public int getReadFileInterval()
	{
		return readFileInterval;
	}

	public void setReadFileInterval(int readFileInterval)
	{
		this.readFileInterval = readFileInterval;
	}

	public int getDelFileInterval()
	{
		return delFileInterval;
	}

	public void setDelFileInterval(int delFileInterval)
	{
		this.delFileInterval = delFileInterval;
	}

	public int getInterval()
	{
		return interval;
	}

	public void setInterval(int interval)
	{
		this.interval = interval;
	}

	public int getWorkThreadNum()
	{
		return workThreadNum;
	}

	public void setWorkThreadNum(int workThreadNum)
	{
		this.workThreadNum = workThreadNum;
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

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
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
