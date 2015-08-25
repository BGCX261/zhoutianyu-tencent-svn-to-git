package com.cyou.wg.sns.gs.core.factory.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedConnection;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.ops.Operation;

/**
 * Connection to a cluster of memcached servers.
 */
public class ConnectManager extends MemcachedConnection{

	public ConnectManager(int bufSize, ConnectionFactory f,
			List<InetSocketAddress> a, Collection<ConnectionObserver> obs,
			FailureMode fm, OperationFactory opfactory) throws IOException {
		super(bufSize, f, a, obs, fm, opfactory);
	}
	
	/**
	 * Add an operation to the given connection.
	 *
	 * @param key the key the operation is operating upon
	 * @param o the operation
	 */
	public void addOperation(final String key, final Operation o) {
		MemcachedNode placeIn=null;
		MemcachedNode primary = locator.getPrimary(key);
		if(primary.isActive() || failureMode == FailureMode.Retry) {
			placeIn=primary;
		} else if(failureMode == FailureMode.Cancel) {
			o.cancel();
		} else {
			placeIn = primary;
		}
		if(placeIn != null) {
			addOperation(placeIn, o);
		} 
	}
	
	
	
	
	

}
