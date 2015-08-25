package com.cyou.wg.sns.ctsvr.core.net.server;

public class ServerShutdown extends Thread {

	private NioServer s;

	public ServerShutdown(NioServer s) {
		this.s = s;
	}

	public void run() {
		s.stop();
	}
}
