package com.cyou.wg.sns.relayserver.socketSvr.net.boot;

import com.cyou.wg.sns.relayserver.core.bean.BeanFactory;
import com.cyou.wg.sns.relayserver.core.net.boot.BaseServerBoot;
import com.cyou.wg.sns.relayserver.core.net.server.NioServer;
import com.cyou.wg.sns.relayserver.core.util.ClassPath;
import com.cyou.wg.sns.relayserver.socketSvr.conf.GameSvrConfigMgr;
import com.cyou.wg.sns.relayserver.socketSvr.conf.ProtocolConfig;

public class Boot extends BaseServerBoot
{

	private NioServer nioServer;
	private static Boot instance;

	public static Boot getInstance()
	{
		if (instance == null)
			instance = new Boot();
		return instance;
	}

	public void init() throws Exception
	{
		nioServer = new NioServer(1, 18888);
		nioServer.init();
	}

	public void start() throws Exception
	{
		init();
		nioServer.startSvr();
	}
	
	public static void main(String[] args) throws Exception{
		Boot.getInstance().start();
		ProtocolConfig.getInstance().init(ClassPath.createRelative("/protocol"));
		BeanFactory.getInstance().init(ClassPath.createRelative("/bean"));
		GameSvrConfigMgr.getInstance().init(ClassPath.createRelative("/gameSvr"));
	}
}
