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
 * name：UserChatSendMessage.java
 * description：玩家聊天协议
 */
public class UserChatSendMessage extends BaseRequestProtocol{
	
	private final static String handler = "userChatSendHandler";
		
	/** 聊天内容 */
	private string message;
	
	public void setMessage(string message){
		this.message = message;
	}
	
	public string getMessage(){
		return message;
	}
		
	/** 发送玩家的ID */
	private string toUserID;
	
	public void setToUserID(string toUserID){
		this.toUserID = toUserID;
	}
	
	public string getToUserID(){
		return toUserID;
	}
	
	
	@Override
	public void decode(ChannelBuffer src) {
		try {
			/* read data */
			this.message = ByteUtil.getStringFromBuff(src);
			this.toUserID = ByteUtil.getStringFromBuff(src);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("Send Message Wrong: Protocol " + this.getProtocolId() + " Decode Failed");
		}
	}
	
	public short getProtocolId() {
	    return 101;
	}
	
	public IResponseProtocol execute() {
		return super.execute(this,handler);
	}
	
	@Override
	public BaseRequestProtocol newInstance() {
		return new UserChatSendMessage();
	}
	
}