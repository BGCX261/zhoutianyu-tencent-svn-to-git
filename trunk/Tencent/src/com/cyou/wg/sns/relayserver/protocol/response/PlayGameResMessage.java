package com.cyou.wg.sns.relayserver.protocol.response;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import com.cyou.wg.sns.relayserver.core.util.ByteUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class PlayGameResMessage extends BaseResponseProtocol
{

	private String openid;
	private String openkey;
	private String pf;

	public byte[] encode()
		throws Exception
	{
		ChannelBuffer buff = ChannelBuffers.buffer(96);
		ByteUtil.putString2Buff(buff, openid, 32);
		ByteUtil.putString2Buff(buff, openkey, 32);
		ByteUtil.putString2Buff(buff, pf, 32);
		return buff.array();
	}

	public String getOpenid()
	{
		return openid;
	}

	public String getOpenkey()
	{
		return openkey;
	}

	public String getPf()
	{
		return pf;
	}

	public void setOpenid(String openid)
	{
		this.openid = openid;
	}

	public void setOpenkey(String openkey)
	{
		this.openkey = openkey;
	}

	public void setPf(String pf)
	{
		this.pf = pf;
	}

	public short getProtocolId()
	{
		return 6001;
	}
}
