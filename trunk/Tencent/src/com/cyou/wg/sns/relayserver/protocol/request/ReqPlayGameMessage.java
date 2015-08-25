package com.cyou.wg.sns.relayserver.protocol.request;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import org.jboss.netty.buffer.ChannelBuffer;

public class ReqPlayGameMessage extends BaseRequestProtocol
{

	private static final String handler = "ReqPlayGameHandler";
	private byte ret;

	public ReqPlayGameMessage()
	{
	}

	public void decode(ChannelBuffer buff)
		throws Exception
	{
		ret = buff.readByte();
	}

	public BaseResponseProtocol execute()
	{
		return super.execute(this, handler);
	}

	public byte getRet()
	{
		return ret;
	}

	public void setRet(byte ret)
	{
		this.ret = ret;
	}
}
