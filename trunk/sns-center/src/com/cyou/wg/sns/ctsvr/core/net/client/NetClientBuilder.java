package com.cyou.wg.sns.ctsvr.core.net.client;

import com.cyou.wg.sns.ctsvr.core.exception.CyouSysException;
import com.cyou.wg.sns.ctsvr.core.util.*;

public class NetClientBuilder {

	public static NetClient buildNewClient(String type, int id)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (type.equals(CenterServerConfig.TYPE_CLIENT_DBSYN)) {
			ClientProperties cp = CenterServerConfig.dbSynClient[id - 1];
			ServerProperties sp = CenterServerConfig.dbSynServer[id - 1];
			NetClient netClient = new NetClient(id, sp.getIp(), sp.getPort(),
					cp.getThreadNum(), cp.getHandler());
			netClient.connect();
			return netClient;
		}
		if (type.equals(CenterServerConfig.TYPE_CLIENT_LOG2FILE)) {
			ClientProperties cp = CenterServerConfig.log2FileClient[id - 1];
			ServerProperties sp = CenterServerConfig.log2FileServer[id - 1];
			NetClient netClient = new NetClient(id, sp.getIp(), sp.getPort(),
					cp.getThreadNum(), cp.getHandler());
			netClient.connect();
			return netClient;
		} else {
			throw new CyouSysException((new StringBuilder())
					.append("Client type is error").append(type).toString());
		}
	}

	public static NetClient[] buildNewClientGroup(String type)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		NetClient net[] = null;
		if (type.equals(CenterServerConfig.TYPE_CLIENT_DBSYN)) {
			net = new NetClient[CenterServerConfig.dbSynClient.length];
			for (int i = 0; i < CenterServerConfig.dbSynClient.length; i++) {
				ClientProperties cp = CenterServerConfig.dbSynClient[i];
				ServerProperties sp = CenterServerConfig.dbSynServer[i];
				NetClient netClient = new NetClient(i + 1, sp.getIp(),
						sp.getPort(), cp.getThreadNum(), cp.getHandler());
				netClient.setClientName((new StringBuilder()).append(CenterServerConfig.TYPE_CLIENT_DBSYN)
						.append(i + 1).toString());
				netClient.setTimeOut(cp.getTimeOut());
				netClient.setTimeOutCheckInterval(cp.getTimeOutCheckInterval());
				netClient.init();
				netClient.connect();
				net[i] = netClient;
			}

			return net;
		}
		if (type.equals(CenterServerConfig.TYPE_CLIENT_LOG2FILE)) {
			net = new NetClient[CenterServerConfig.log2FileClient.length];
			for (int i = 0; i < CenterServerConfig.log2FileClient.length; i++) {
				ClientProperties cp = CenterServerConfig.log2FileClient[i];
				ServerProperties sp = CenterServerConfig.log2FileServer[i];
				NetClient netClient = new NetClient(i + 1, sp.getIp(),
						sp.getPort(), cp.getThreadNum(), cp.getHandler());
				netClient.setClientName((new StringBuilder())
						.append(CenterServerConfig.TYPE_CLIENT_LOG2FILE).append(i + 1).toString());
				netClient.setTimeOut(cp.getTimeOut());
				netClient.setTimeOutCheckInterval(cp.getTimeOutCheckInterval());
				netClient.init();
				netClient.connect();
				net[i] = netClient;
			}
			return net;
		} else {
			throw new CyouSysException((new StringBuilder())
					.append("client type is error").append(type).toString());
		}
	}
}
