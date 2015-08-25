package com.cyou.wg.sns.ctsvr.app.log2File.boot;

import com.cyou.wg.sns.ctsvr.app.log2File.work.*;
import com.cyou.wg.sns.ctsvr.core.net.server.NioServer;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.ctsvr.core.util.ServerProperties;
import com.cyou.wg.sns.ctsvr.startboot.boot.BaseMain;

import java.io.File;

public class Main extends BaseMain {

	public void start() throws Exception {
		CenterServerConfig.initNetProperties(new File("conf/centerServer/centerServer.xml"), CenterServerConfig.TYPE_SVR_LOG2FILE);
		InitSchemaMapping.initKeyMap();
		InitSchemaMapping.startCheckThread();
		ServerProperties properties = CenterServerConfig.log2FileServer[super.getServerId() - 1];
		NioServer server = new NioServer(properties.getPort(), properties.getThreadNum(), properties.getHandler());
		server.setType(CenterServerConfig.TYPE_SVR_LOG2FILE);
		server.setId(super.getServerId());
		server.startSvr();
		Log2FileConf.init("conf/log2File/conf.xml");
		MessageCache.init();
		MessageCache.startWriteThread();
		
		ErrorCache.init();
		ErrorCache.startWriteThread();
	}
}
