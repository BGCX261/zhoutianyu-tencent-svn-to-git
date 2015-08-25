package com.cyou.wg.sns.relayserver.protocol.request;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import com.cyou.wg.sns.relayserver.core.util.ByteUtil;
import org.jboss.netty.buffer.ChannelBuffer;

public class ReqGetUserInfoMessage extends BaseRequestProtocol
{

	private static final String handler = "ReqGetUserInfoHandler";
	private String openid;
	private String openkey;
	private String pf;

	public void decode(ChannelBuffer buff) throws Exception
	{
		openid = ByteUtil.getStringFromBuff(buff, 32);
		openkey = ByteUtil.getStringFromBuff(buff, 32);
		pf = ByteUtil.getStringFromBuff(buff, 32);
	}

	public BaseResponseProtocol execute()
	{
		return super.execute(this, handler);
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
}
