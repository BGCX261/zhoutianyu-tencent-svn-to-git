package com.cyou.wg.sns.gs.core.aop;
import com.cyou.wg.sns.gs.core.aop.callBackFilter.AopCallbackFilter;
import com.cyou.wg.sns.gs.core.aop.interceptor.TransInterceptor;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class AopAround {
	
	private static Enhancer enhancer = new Enhancer();
	private static CallbackFilter cbf = new AopCallbackFilter();
	private static Callback[] s = new Callback[2];
	static {
//		s[0] = new TransInterceptor();
//		s[1] = NoOp.INSTANCE;
	}
	/**
	 * 使用aop，处理类
	 * @param clazz
	 * @return
	 */
	public static Object around(Class clazz) {
		return enhancer.create(clazz, null, cbf, s);
	}
	/**
	 * 使用aop处理类
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Object around(String className) throws ClassNotFoundException {
		Class clazz = Class.forName(className);
		return around(clazz);
	}

}
