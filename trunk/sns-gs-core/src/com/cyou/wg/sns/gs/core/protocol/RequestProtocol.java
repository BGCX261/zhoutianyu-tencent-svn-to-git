package com.cyou.wg.sns.gs.core.protocol;

import org.apache.mina.core.buffer.IoBuffer;

import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.spring.ServiceFactory;


public abstract class RequestProtocol extends BaseProtocol{
	
	public abstract void decode(IoBuffer src);//解码
	public abstract ResponseProtocol execute();//执行协议
	
	public ResponseProtocol execute(RequestProtocol req,String handler) {
		RequestProtocolHandler rph = (RequestProtocolHandler)ServiceFactory.getSpringBean(handler);
		if(rph == null) {
			throw new CyouSysException("不存在处理逻辑：" + handler);
		}
		return rph.execute(req);
	}
}
