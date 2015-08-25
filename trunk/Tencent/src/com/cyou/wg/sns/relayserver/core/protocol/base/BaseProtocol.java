package com.cyou.wg.sns.relayserver.core.protocol.base;

public abstract class BaseProtocol
{

	protected short protocolId;

	public abstract short getProtocolId();

	public void setProtocolId(short protocolId)
	{
		this.protocolId = protocolId;
	}
}
