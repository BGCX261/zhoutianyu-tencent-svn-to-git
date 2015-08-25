package com.cyou.wg.sns.relayserver.httpClient.util;

public class TencentUtil
{
	public static boolean isOpenid(String openid)
	{
		return openid.length() == 32 && openid.matches("^[0-9A-Fa-f]+$");
	}
}
