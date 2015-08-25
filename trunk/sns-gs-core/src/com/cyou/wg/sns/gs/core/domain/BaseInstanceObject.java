package com.cyou.wg.sns.gs.core.domain;

import com.cyou.wg.sns.gs.core.dataSource.DataClientHash;
import com.cyou.wg.sns.gs.core.exception.CyouAppException;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.net.CachedClient;

public abstract class BaseInstanceObject extends BaseObject implements Cloneable {
	
	protected int userId;
	
	
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public abstract Object createResponseProtocol();//更新（insert,update,delete）此对象时产生的数据层更新,返回值为ResponseProtocol或List<ResponseProtocol>
	
	public abstract byte version();//得到当前实体类版本
	
	public BaseInstanceObject clone() {
		try {
			return (BaseInstanceObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new CyouSysException("不支持的clone类型 : "  + super.getClass().getName());
		}
	}
	
	public BaseInstanceObject newObject() {
		throw new CyouAppException("need impl");
	}
	/**
	 * 得到数据缓存分布策略
	 * @return
	 */
	public int getCacheIndex() {
		return DataClientHash.cacheHash4User(userId);
	}
	/**
	 * 得到带缓存定位信息的key
	 * @return
	 */
	public String getRemoteCacheKey() {
		return getCacheIndex() + "|" + getBaseObjectKey();
	}
}
