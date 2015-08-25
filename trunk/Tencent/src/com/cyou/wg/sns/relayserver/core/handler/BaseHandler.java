package com.cyou.wg.sns.relayserver.core.handler;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;

/**
 * @Description handler基类
 * @author 周天宇
 */
public interface BaseHandler
{

	public abstract BaseResponseProtocol execute(BaseRequestProtocol baserequestprotocol);
}
