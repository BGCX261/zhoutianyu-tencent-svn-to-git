package com.cyou.wg.sns.gs.core.domain;

import java.util.HashMap;
import java.util.Map;

import com.cyou.wg.sns.gs.core.exception.CyouSysException;

public class BaseObjectConfig {
	
	protected  Map<Short,Class> baseObjMap = new HashMap<Short, Class>();
	
	public BaseObjectConfig() {
		init();
	}
	
	protected void init() {
		throw new CyouSysException("需要在子类中实现此方法");
	}
	
	public  BaseInstanceObject getBaseObjectById(short id) throws InstantiationException, IllegalAccessException {
		Class<BaseInstanceObject> c = baseObjMap.get(id);
		if(c == null) {
			throw new CyouSysException("不存在对象：" + id);
		}
		return c.newInstance();
	}

}
