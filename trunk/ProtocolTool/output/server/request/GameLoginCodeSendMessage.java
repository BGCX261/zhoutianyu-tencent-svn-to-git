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
 * name：GameLoginCodeSendMessage.java
 * description：请求验证游戏,登录流程第一步
 */
public class GameLoginCodeSendMessage extends BaseRequestProtocol{
	
	private final static String handler = "gameLoginCodeSendHandler";
		
	/** 用户平台ID */
	private string accID;
	
	public void setAccID(string accID){
		this.accID = accID;
	}
	
	public string getAccID(){
		return accID;
	}
	
	
	@Override
	public void decode(ChannelBuffer src) {
		try {
			/* read data */
			this.accID = ByteUtil.getStringFromBuff(src);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("Send Message Wrong: Protocol " + this.getProtocolId() + " Decode Failed");
		}
	}
	
	public short getProtocolId() {
	    return 30;
	}
	
	public IResponseProtocol execute() {
		return super.execute(this,handler);
	}
	
	@Override
	public BaseRequestProtocol newInstance() {
		return new GameLoginCodeSendMessage();
	}
	
}