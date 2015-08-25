package com.cyou.wg.sns.relayserver.socketSvr.handler;

import com.cyou.wg.sns.relayserver.core.handler.BaseHandler;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import com.cyou.wg.sns.relayserver.protocol.request.ReqPlayGameMessage;

public class ReqPlayGameHandler implements BaseHandler
{

	public BaseResponseProtocol execute(BaseRequestProtocol rp)
	{
		ReqPlayGameMessage reqPlayGameMessage = (ReqPlayGameMessage)rp;
		if (reqPlayGameMessage.getRet() != 1);
		return null;
	}
}
