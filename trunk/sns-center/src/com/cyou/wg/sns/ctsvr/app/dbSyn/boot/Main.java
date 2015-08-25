package com.cyou.wg.sns.ctsvr.app.dbSyn.boot;

import com.cyou.wg.sns.ctsvr.app.dbSyn.work.DbCommandCache;
import com.cyou.wg.sns.ctsvr.app.dbSyn.work.InitIdMapping;
import com.cyou.wg.sns.ctsvr.core.boot.BaseMain;
import com.cyou.wg.sns.ctsvr.core.net.server.NioServer;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.ctsvr.core.util.ServerProperties;
import java.io.File;
import java.io.PrintStream;

public class Main extends BaseMain
{

	public Main()
	{
	}

	public void start()
		throws Exception
	{
		CenterServerConfig.initNetProperties(new File("conf/centerServer/centerServer.xml"), "dbSyn");
		InitIdMapping.initSqlMap();
		InitIdMapping.startCheckThread();
		System.out.println((new StringBuilder()).append("getServerId :").append(super.getServerId()).toString());
		ServerProperties properties = CenterServerConfig.dbSynServer[super.getServerId() - 1];
		NioServer server = new NioServer(properties.getPort(), properties.getThreadNum(), properties.getHandler());
		server.setType("dbSyn");
		server.setId(super.getServerId());
		server.startSvr();
		DbCommandCache.init(super.getServerId());
	}
}
