package com.cyou.wg.sns.gs.core.net;

import java.io.File;

import com.cyou.wg.sns.ctsvr.app.log2File.messageHandler.LogicalLogList;
import com.cyou.wg.sns.ctsvr.core.net.client.NetClient;
import com.cyou.wg.sns.ctsvr.core.net.client.NetClientBuilder;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.gs.core.domain.ClassPath;
/**
 * 逻辑日志服务器客户端
 * @author Administrator
 *
 */
public class LogServerClient {
	private static NetClient[] clients = null;
	private static int hashPara = 0;
	/**
	 * 初始化
	 * @throws Exception 
	 */
	public static void init() throws Exception {
		File file = ClassPath.location.createRelative("client/centerServerClient.xml").getFile();
		CenterServerConfig.initNetProperties(file, CenterServerConfig.TYPE_CLIENT_LOG2FILE);
		clients = NetClientBuilder.buildNewClientGroup(CenterServerConfig.TYPE_CLIENT_LOG2FILE);
		hashPara = 100/clients.length*clients.length < 100 ? 100/clients.length + 1 : 100/clients.length;
	}
	
	public static int logicalSvrHash(int userId) {
		return userId%100/(hashPara);
	}
	
	public static int getClientNum() {
		return clients.length;
	}
	
	public static void sendLogical(int clientId, LogicalLogList list) throws Exception {
		clients[clientId].write(list.encode());
	}
}
