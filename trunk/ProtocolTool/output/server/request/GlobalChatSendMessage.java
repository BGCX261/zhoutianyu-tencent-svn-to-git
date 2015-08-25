package com.cyou.wg.jwgDemo.protocol.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.cyou.wg.wgf.exception.CyouSysException;
import com.cyou.wg.wgf.protocol.base.BaseRequestProtocol;
import com.cyou.wg.wgf.protocol.base.response.IResponseProtocol;
import com.cyou.wg.wgf.protocol.base.BaseResponseProtocol;
import com.cyou.wg.wgf.util.ByteUtil;

import com.cyou.wg.jwgDemo.protocol.vo.*;

/**
 * name：GlobalChatSendMessage.java
 * description：全服广播协议
 */
public class GlobalChatSendMessage extends BaseRequestProtocol{
	
	private final static String handler = "globalChatSendHandler";
		
	/** 聊天内容 */
	private string message;
	
	public void setMessage(string message){
		this.message = message;
	}
	
	public string getMessage(){
		return message;
	}
	
	
	@Override
	public void decode(ChannelBuffer src) {
		try {
			/* read data */
			this.message = ByteUtil.getStringFromBuff(src);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("Send Message Wrong: Protocol " + this.getProtocolId() + " Decode Failed");
		}
	}
	
	public short getProtocolId() {
	    return 102;
	}
	
	public IResponseProtocol execute() {
		return super.execute(this,handler);
	}
	
	@Override
	public BaseRequestProtocol newInstance() {
		return new GlobalChatSendMessage();
	}
	
}