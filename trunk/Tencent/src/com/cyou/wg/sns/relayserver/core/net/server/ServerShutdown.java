package com.cyou.wg.sns.relayserver.core.net.server;

public class ServerShutdown extends Thread
{

	private NioServer server;

	public ServerShutdown(NioServer server)
	{
		this.server = server;
	}

	public void run()
	{
		server.stop();
	}
}
