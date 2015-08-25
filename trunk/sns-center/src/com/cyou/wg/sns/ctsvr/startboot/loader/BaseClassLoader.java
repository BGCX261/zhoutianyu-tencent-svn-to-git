package com.cyou.wg.sns.ctsvr.startboot.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class BaseClassLoader extends URLClassLoader {

	public BaseClassLoader(URL[] urls) {
		super(urls);
	}

	public Class<?> findClass(String name) throws ClassNotFoundException{
			return super.findClass(name);
	}
}
