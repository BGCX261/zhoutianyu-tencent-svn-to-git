package com.cyou.wg.sns.gs.core.dataSource;

import com.cyou.wg.sns.gs.core.net.CachedClient;

/**
 * 数据hash
 * @author Administrator
 *
 */
public class DataClientHash {
	
	private static DataClientHash hash = new DataClientHash();
	
	public static void setHash(DataClientHash h) {
		hash = h;
	}
	
	public static int dbHash4User(int userId) {
		return hash.dbHash4User0(userId);
	}
	
	public static int cacheHash4User(int userId) {
		return hash.cacheHash4User0(userId);
	}
	
	public static int cacheHash4Lock(int key) {
		return hash.cacheHash4Lock0(key);
	}
	
	public int dbHash4User0(int userId) {
		return (byte)(userId/10000000);
	}
	
	public int cacheHash4User0(int userId) {
		return userId % (CachedClient.dataClientLength - 2);
	}
	
	public int cacheHash4Lock0(int key) {
		return Math.abs(key % CachedClient.lockClientLength);
	}
}
