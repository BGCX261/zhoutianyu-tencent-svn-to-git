package com.cyou.wg.sns.gs.core.net;

import java.io.File;

import com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler.DbCommandList;
import com.cyou.wg.sns.ctsvr.core.net.client.NetClient;
import com.cyou.wg.sns.ctsvr.core.net.client.NetClientBuilder;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.gs.core.domain.ClassPath;

public class DbSynClient {
	private static NetClient[] clients = null;
	/**
	 * 初始化
	 * @throws Exception 
	 */
	public static void init() throws Exception {
		File file = ClassPath.location.createRelative("client/centerServerClient.xml").getFile();
		CenterServerConfig.initNetProperties(file, CenterServerConfig.TYPE_CLIENT_DBSYN);
		clients = NetClientBuilder.buildNewClientGroup(CenterServerConfig.TYPE_CLIENT_DBSYN);
	}
	/**
	 * 把对应的更新命令队列发送到目标服务器
	 * @param dbIndex 需要更新的数据库同步服务器的序号，此序号和数据库的序号相同
	 * @param command
	 * @throws Exception 
	 */
	public static void writeCommand(byte dbIndex, DbCommandList command) throws Exception {
		clients[dbIndex].write(command.encode());
	}
	/**
	 * 得到连接的客户端的数量
	 * @return
	 */
	public static int getClientNum() {
		return clients == null ? 0 : clients.length;
	}

}
