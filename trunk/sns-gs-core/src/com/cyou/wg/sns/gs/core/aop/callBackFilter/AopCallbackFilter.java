package com.cyou.wg.sns.gs.core.aop.callBackFilter;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.CallbackFilter;

public class AopCallbackFilter implements CallbackFilter{
	
	/**
	 * 本工程service包，以Service结尾的类，方法名以WithCommit结尾的方法带上单级事务
	 * 此事务为逻辑层的，非数据库事务
	 */
	public int accept(Method arg0) {
		String className = arg0.getDeclaringClass().getName();
		if(className.startsWith("com.cyou.wg.spriteTales") && className.contains("service") && className.endsWith("Service") && arg0.getName().endsWith("WithCommit")) {
			return 0;
		}
		return 1;
	}

}
