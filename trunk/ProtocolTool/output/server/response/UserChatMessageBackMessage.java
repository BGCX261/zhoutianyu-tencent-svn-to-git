package com.cyou.wg.jwgDemo.protocol.response;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.cyou.wg.wgf.exception.CyouSysException;
import com.cyou.wg.wgf.protocol.base.BaseRequestProtocol;
import com.cyou.wg.wgf.protocol.base.response.IResponseProtocol;
import com.cyou.wg.wgf.protocol.base.BaseResponseProtocol;
import com.cyou.wg.wgf.util.ByteUtil;

import com.cyou.wg.jwgDemo.protocol.vo.*;

/**
 * name：UserChatMessageBackMessage.java
 * description：发送玩家聊天消息
 */
public class UserChatMessageBackMessage extends BaseResponseProtocol{
		
	/** 玩家聊天消息列表 */
	private NetChatMessageVO[] chatMessageList = new NetChatMessageVO[0];
	
	public void setChatMessageList(NetChatMessageVO[] chatMessageList){
		this.chatMessageList = chatMessageList;
	}
	
	public NetChatMessageVO[] getChatMessageList(){
		return chatMessageList;
	}

	
	
	public short getProtocolId() {
	    return 60;
	}
	
	@Override
	public byte[] encode() throws Exception {
		ChannelBuffer src = ChannelBuffers.dynamicBuffer(64);
		src.writeShort(super.getProtocolId());
		
		/* write data*/
		src.writeShort((short)this.chatMessageList.length);
		for(int chatMessageListIndex = 0; chatMessageListIndex < this.chatMessageList.length; chatMessageListIndex++){
			src.writeBytes(this.chatMessageList[chatMessageListIndex].encode());
		}
		return ByteUtil.buff2Array(src);
	}
	
}