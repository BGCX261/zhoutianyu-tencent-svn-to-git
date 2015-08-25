package com.cyou.wg.sns.gs.core.domain;

/**
 * 基础的数据库对象
 * @author Administrator
 *
 */
public abstract class BaseDbObject extends BaseInstanceObject{
	
	public int getTableIndex() {////分表时的表名索引
		return this.userId % 100;
	}
	
}
