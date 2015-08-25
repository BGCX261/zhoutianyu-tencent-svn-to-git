package com.cyou.wg.sns.relayserver.core.protocol.base;

import com.cyou.wg.sns.relayserver.core.bean.BeanFactory;
import com.cyou.wg.sns.relayserver.core.exception.CyouSysException;
import com.cyou.wg.sns.relayserver.core.handler.BaseHandler;
import org.jboss.netty.buffer.ChannelBuffer;

public abstract class BaseRequestProtocol extends BaseProtocol
{

	public abstract void decode(ChannelBuffer channelbuffer) throws Exception;

	public abstract BaseResponseProtocol execute();

	public BaseResponseProtocol execute(BaseRequestProtocol req, String handler)
	{
		BaseHandler rph = (BaseHandler)BeanFactory.getInstance().getBean(handler);
		if (rph == null)
			throw new CyouSysException((new StringBuilder()).append("Not exists hanlder:").append(handler).toString());
		else
			return rph.execute(req);
	}

	public short getProtocolId()
	{
		return protocolId;
	}
}
