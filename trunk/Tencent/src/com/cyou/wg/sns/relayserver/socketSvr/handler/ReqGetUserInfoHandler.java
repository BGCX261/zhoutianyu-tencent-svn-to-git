package com.cyou.wg.sns.relayserver.socketSvr.handler;

import com.cyou.wg.sns.relayserver.core.handler.BaseHandler;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import com.cyou.wg.sns.relayserver.protocol.request.ReqGetUserInfoMessage;
import com.cyou.wg.sns.relayserver.protocol.response.PlayGameResMessage;

public class ReqGetUserInfoHandler implements BaseHandler
{

	public BaseResponseProtocol execute(BaseRequestProtocol rp)
	{
		ReqGetUserInfoMessage reqMsg = (ReqGetUserInfoMessage)rp;
		PlayGameResMessage resMsg = new PlayGameResMessage();
		resMsg.setOpenid("tongnian");
		resMsg.setOpenkey("tongnian");
		resMsg.setPf("tongnian");
		return resMsg;
	}
}
