package com.cyou.wg.sns.ctsvr.startboot.boot;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.cyou.wg.sns.ctsvr.startboot.loader.BaseClassLoader;
import com.cyou.wg.sns.ctsvr.startboot.log4j.LogFactory;

@SuppressWarnings("rawtypes")
public class StartBoot {
	
	private static BaseClassLoader loader;
	
	public static void main(String[] args) {
		try {
			init(args[0]);
			Thread.currentThread().setContextClassLoader(loader);
			Class<?> cc = loader.findClass(args[1]);
			Method m = cc.getMethod("start", String.class, String.class);
			m.invoke(cc.newInstance(), args[2], args[3]);
		}catch (Exception e){
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG);
		}
	}

	public static void init(String loadName) throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		File[] files = new File("./lib/").listFiles();
		List<URL> list = new ArrayList<URL>();
		for(File file : files) {
			if(!file.getName().endsWith(".jar")) {
				continue;
			}
			if((!file.exists())||(!file.canRead())) {
				continue;
			}
			list.add(file.toURI().toURL());
		}
		URL[] url = list.toArray(new URL[0]);
		Class<?> loaderClass = ClassLoader.getSystemClassLoader().loadClass(loadName);
		Constructor c = loaderClass.getDeclaredConstructor(URL[].class);
		loader = (BaseClassLoader) c.newInstance((Object)url);
	}
}
