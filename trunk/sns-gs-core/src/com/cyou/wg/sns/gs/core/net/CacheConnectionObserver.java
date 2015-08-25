package com.cyou.wg.sns.gs.core.net;

import java.net.SocketAddress;

import com.cyou.wg.sns.gs.core.factory.log.LogFactory;

import net.spy.memcached.ConnectionObserver;

public class CacheConnectionObserver implements ConnectionObserver{

	@Override
	public void connectionEstablished(SocketAddress sa, int reconnectCount) {
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("连接缓存服务器：" + sa.toString() + "成功，reconnectCount ：" + reconnectCount);
		
	}

	@Override
	public void connectionLost(SocketAddress sa) {
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("与缓存服务器：" + sa.toString() + "断开连接");
		
	}

}
