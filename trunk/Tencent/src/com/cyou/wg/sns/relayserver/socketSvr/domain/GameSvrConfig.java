package com.cyou.wg.sns.relayserver.socketSvr.domain;

public class GameSvrConfig
{

	private String appid;
	private String appkey;
	private String url;

	public String getAppid()
	{
		return appid;
	}

	public String getAppkey()
	{
		return appkey;
	}

	public String getUrl()
	{
		return url;
	}

	public void setAppid(String appid)
	{
		this.appid = appid;
	}

	public void setAppkey(String appkey)
	{
		this.appkey = appkey;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}
