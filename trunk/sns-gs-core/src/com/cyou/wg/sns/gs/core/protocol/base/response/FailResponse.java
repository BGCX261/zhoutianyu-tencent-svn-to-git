package com.cyou.wg.sns.gs.core.protocol.base.response;

import com.cyou.wg.sns.gs.core.protocol.ResponseProtocol;
/**
 * 失败请求返回值
 * @author Administrator
 *
 */
public abstract class FailResponse{
	
	public abstract ResponseProtocol createProtocol(String message, int protocolId);
}
