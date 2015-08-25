package com.cyou.wg.sns.ctsvr.startboot.boot;

/**
 * @Description 
 * @author 周天宇
 */
public abstract class BaseMain {

	private int serverId;
	
	public abstract void start() throws Exception;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
}
