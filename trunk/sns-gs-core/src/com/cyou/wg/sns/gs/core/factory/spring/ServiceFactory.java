package com.cyou.wg.sns.gs.core.factory.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServiceFactory implements ApplicationContextAware{
	private ApplicationContext applicationContext;
	private static ServiceFactory instance = null;
	private static Map<String, Object> beans = new HashMap<String, Object>();
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		this.applicationContext = arg0;
	}
	
	public static ServiceFactory createInstance()
	  {
	    if (instance == null)
	    {
	      instance = new ServiceFactory();
	    }
	    return instance;
	  }

	  public static ServiceFactory getInstance()
	  {
	    return instance;
	  }
	  
	  public ApplicationContext getApplicationContext()
	  {
	    return this.applicationContext;
	  }

	  public static ApplicationContext getContext()
	  {
	    return getInstance().getApplicationContext();
	  }

	  public static Object getSpringBean(String beanId)
	  {
	    try
	    {
	      Object ret = beans.get(beanId);
	      if (ret != null) {
	        return ret;
	      }
	      ret = getContext().getBean(beanId);
	      if (ret != null) {
	        beans.put(beanId, ret);
	      }
	      return ret;
	    }
	    catch (Exception e) {
	    }
	    return null;
	  }
	
}
