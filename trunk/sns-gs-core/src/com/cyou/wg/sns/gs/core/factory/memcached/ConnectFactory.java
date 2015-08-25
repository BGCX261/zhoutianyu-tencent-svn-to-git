package com.cyou.wg.sns.gs.core.factory.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedConnection;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;

public class ConnectFactory extends DefaultConnectionFactory{
	
	@Override
	public NodeLocator createLocator(List<MemcachedNode> nodes) {
		return new ConnectLocator(nodes, HashAlgorithm.NATIVE_HASH);
	}
	
	public MemcachedConnection createConnection(List<InetSocketAddress> addrs)
			throws IOException {
		return new ConnectManager(getReadBufSize(), this, addrs,
				getInitialObservers(), getFailureMode(), getOperationFactory());
	}

}
