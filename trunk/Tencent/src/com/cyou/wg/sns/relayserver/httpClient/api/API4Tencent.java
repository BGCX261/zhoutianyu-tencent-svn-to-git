package com.cyou.wg.sns.relayserver.httpClient.api;

import com.cyou.wg.sns.relayserver.socketSvr.conf.GameSvrConfigMgr;
import com.cyou.wg.sns.relayserver.socketSvr.domain.GameSvrConfig;
import com.qq.open.OpenApiV3;

public class API4Tencent
{

	public static OpenApiV3 sdk;

	public static void init()
	{
		GameSvrConfig gmSvrConfig = GameSvrConfigMgr.getInstance().gameSvrConfig;
		sdk = new OpenApiV3(gmSvrConfig.getAppid(), gmSvrConfig.getAppkey());
	}
}
