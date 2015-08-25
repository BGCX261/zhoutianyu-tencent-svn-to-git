package com.cyou.wg.sns.gs.core.protocol;


public class BaseProtocol {
	
	public static final int DEFAULT_SYS_ERROR_ID = -1;//默认系统错误协议id
	
	private short protocolId;//协议

	public short getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(short protocolId) {
		this.protocolId = protocolId;
	}
	
	
}
