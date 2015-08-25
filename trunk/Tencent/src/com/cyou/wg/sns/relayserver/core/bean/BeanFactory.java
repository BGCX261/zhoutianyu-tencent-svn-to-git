package com.cyou.wg.sns.relayserver.core.bean;

import com.cyou.wg.sns.relayserver.core.exception.CyouSysException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import net.sf.cglib.beans.BeanMap;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * @Description bean工厂，自动组装bean，没有aop功能
 * @author 周天宇
 */
public class BeanFactory
{

	protected Map beanMap;
	protected Map beanTypeNameMap;
	protected Map beanPropertyMap;
	private static BeanFactory beanFactory;
	private static Object lockBeanFactory = new Object();

	public BeanFactory()
	{
		beanMap = new HashMap();
		beanTypeNameMap = new HashMap();
		beanPropertyMap = new HashMap();
	}

	public static BeanFactory getInstance()
	{
		if (beanFactory == null)
			synchronized (lockBeanFactory)
			{
				if (beanFactory == null)
					beanFactory = new BeanFactory();
			}
		return beanFactory;
	}

	public void init(String dir)
		throws DocumentException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		File fileList[] = (new File(dir)).listFiles();
		File arr[] = fileList;
		int len$ = arr.length;
		for (int i = 0; i < len$; i++)
		{
			File file = arr[i];
			if (!file.isFile() || file.isHidden() || !file.getName().endsWith(".xml"))
				continue;
			SAXReader saxReader = new SAXReader();
			Element root = saxReader.read(file).getRootElement();
			Iterator eleI = root.elements().iterator();
			while(eleI.hasNext()){
				Element e = (Element)eleI.next();
				initOneBean(e.attribute("id").getStringValue(),e.attribute("class").getStringValue());
			}
		}

		Iterator beanMapI = beanMap.keySet().iterator();
		while(beanMapI.hasNext()){
			String key = (String)beanMapI.next();
			fixBean(key);
		}

	}

	protected void initOneBean(String name, String type)
		throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (beanMap.containsKey(name))
			throw new CyouSysException((new StringBuilder()).append("重复定义bean :").append(name).toString());
		if (beanTypeNameMap.containsKey(type))
		{
			throw new CyouSysException((new StringBuilder()).append("重复定义类型 :").append(type).toString());
		} else
		{
			beanTypeNameMap.put(type, name);
			beanPropertyMap.put(name, Class.forName(type).getDeclaredFields());
			beanMap.put(name, Class.forName(type).newInstance());
			return;
		}
	}

	public Object getBean(String name)
	{
		return beanMap.get(name);
	}

	protected void fixBean(String name)
	{
		Object preFixBean = beanMap.get(name);
		Field preFixFieldList[] = (Field[])beanPropertyMap.get(name);
		BeanMap preMap = BeanMap.create(preFixBean);
		Field arr[] = preFixFieldList;
		int len = arr.length;
		for (int i = 0; i < len; i++)
		{
			Field preField = arr[i];
			String fieldBeanName = (String)beanTypeNameMap.get(preField.getType().getName());
			if (fieldBeanName == null)
				continue;
			if (check(preField.getType(), preFixBean.getClass().getSuperclass().getName()))
				throw new CyouSysException((new StringBuilder()).append("检查到循环注入bean:").append(name).append("属性：").append(fieldBeanName).toString());
			preMap.put(preField.getName(), beanMap.get(fieldBeanName));
		}

	}

	protected boolean check(Class propertyClass, String checkType)
	{
		Field fs[] = propertyClass.getFields();
		Field arr[] = fs;
		int len = arr.length;
		for (int i = 0; i < len; i++)
		{
			Field f = arr[i];
			if (f.getClass().getName().equals(checkType))
				return true;
		}

		return false;
	}
}
