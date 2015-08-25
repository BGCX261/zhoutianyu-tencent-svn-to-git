package com.cyou.wg.sns.gs.core.factory.memcached;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.cyou.wg.sns.gs.core.exception.CyouSysException;

import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.vbucket.config.Config;

public class ConnectLocator implements NodeLocator{
	
	private MemcachedNode[] nodes;//最后一个是账户缓存，
	
	private HashAlgorithm hash;

	/**
	 * Construct an ArraymodNodeLocator over the given array of nodes and
	 * using the given hash algorithm.
	 *
	 * @param n the array of nodes
	 * @param alg the hash algorithm
	 */
	public ConnectLocator(List<MemcachedNode> n, HashAlgorithm hash) {
		nodes=n.toArray(new MemcachedNode[n.size()]);
		this.hash = hash;
	}

	public Collection<MemcachedNode> getAll() {
		return Arrays.asList(nodes);
	}

	public MemcachedNode getPrimary(String k) {
		return nodes[getServerForKey(k)];
	}

	public Iterator<MemcachedNode> getSequence(String k) {
		return new NodeIterator(getServerForKey(k));
	}

	public NodeLocator getReadonlyCopy() {
		throw new CyouSysException("不支持此方法");
	}

	@Override
	public void updateLocator(List<MemcachedNode> nodes, Config conf) {
		this.nodes=nodes.toArray(new MemcachedNode[nodes.size()]);
	}
	/**
	 * 
	 * @param key
	 * 1、index|key
	 * @return
	 */
	private int getServerForKey(String key) {
		String[] keys = key.split("\\|");
		int rv = Integer.valueOf(keys[0]);
		assert rv >= 0 : "Returned negative key for key " + key;
		assert rv < nodes.length : "Invalid server number " + rv + " for key " + key;
		return rv;
	}
	

	class NodeIterator implements Iterator<MemcachedNode> {

		private final int start;
		private int next=0;

		public NodeIterator(int keyStart) {
			start=keyStart;
			next=start;
			computeNext();
			assert next >= 0 || nodes.length == 1
				: "Starting sequence at " + start + " of "
					+ nodes.length + " next is " + next;
		}

		public boolean hasNext() {
			return next >= 0;
		}

		private void computeNext() {
			if(++next >= nodes.length) {
				next=0;
			}
			if(next == start) {
				next=-1;
			}
		}

		public MemcachedNode next() {
			try {
				return nodes[next];
			} finally {
				computeNext();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException("Can't remove a node");
		}

	}

}
