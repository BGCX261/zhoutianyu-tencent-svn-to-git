package com.cyou.wg.sns.relayserver.core.protocol.base;

public abstract class BaseResponseProtocol extends BaseProtocol
{
	
	public abstract byte[] encode() throws Exception;
	
}
