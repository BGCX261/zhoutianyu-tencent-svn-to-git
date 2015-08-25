package com.cyou.wg.sns.ctsvr.app.dbSyn2.boot;

import com.cyou.wg.sns.ctsvr.app.dbSyn.work.InitIdMapping;
import com.cyou.wg.sns.ctsvr.app.dbSyn2.work.DbCommandCache;
import com.cyou.wg.sns.ctsvr.core.net.server.NioServer;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.ctsvr.core.util.ServerProperties;
import com.cyou.wg.sns.ctsvr.startboot.boot.BaseMain;

import java.io.File;

public class Main extends BaseMain {

	public void start() throws Exception {
		CenterServerConfig.initNetProperties(new File("conf/centerServer/centerServer.xml"), "dbSyn");
		InitIdMapping.initSqlMap();
		InitIdMapping.startCheckThread();
		ServerProperties properties = CenterServerConfig.dbSynServer[super.getServerId() - 1];
		NioServer server = new NioServer(properties.getPort(), properties.getThreadNum(), properties.getHandler());
		server.setType(CenterServerConfig.TYPE_CLIENT_DBSYN);
		server.setId(super.getServerId());
		server.startSvr();
		DbCommandCache.init(super.getServerId());
	}
}
