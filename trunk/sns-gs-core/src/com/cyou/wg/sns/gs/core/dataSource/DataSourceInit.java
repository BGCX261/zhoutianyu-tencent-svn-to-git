package com.cyou.wg.sns.gs.core.dataSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import net.sf.cglib.beans.BeanMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.spring.ServiceFactory;

public class DataSourceInit {
	public static BaseDataSource[] init(String type) throws IOException, DocumentException {
		File file = ClassPath.location.createRelative("/dataSource/dataSource.xml").getFile();
		return DataSourceInit.init(file, type);
	}
	
	public static BaseDataSource[] init(File file, String type) throws IOException, DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		List<Element> list = doc.getRootElement().elements();
		BaseDataSource[] res = new BaseDataSource[list.size()];
		for(Element groupE : list) {
			List<Element> tList = groupE.elements();
			for(Element te : tList) {
				if(te.getName().equals(type)) {
					res[Integer.valueOf(groupE.attribute("id").getStringValue()) - 1] = create(te);
					break;
				}
			}
		}
		return res;
	}
	
	public static BaseDataSource init(File file, String type, int id) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		List<Element> list = doc.getRootElement().elements();
		for(Element groupE : list) {
			List<Element> tList = groupE.elements();
			for(Element te : tList) {
				if(te.getName().equals(type) && Integer.valueOf(groupE.attribute("id").getStringValue()) == id) {
					return create(te);
				}
			}
		}
		throw new CyouSysException("参数：file:" + file.getAbsolutePath() + " type:" + type + " id:" + id + "不存在合法的数据源");
		
	}
	
	public static BaseDataSource create(Element e) {
		BaseDataSource baseDataSource = new BaseDataSource();
		BeanMap beanMap = BeanMap.create(baseDataSource);
		List<Element> list = e.elements();
		Properties properties = new Properties();
		for(Element te : list) {
			if(!te.attribute("name").getStringValue().equals("connectionProperties")) {
				Object value = beanMap.get(te.attribute("name").getStringValue());
				if(value instanceof Integer) {
					value = Integer.valueOf(te.attribute("value").getStringValue());
				}else if(value instanceof Long) {
					value = Long.valueOf(te.attribute("value").getStringValue());
				}else if(value instanceof Boolean) {
					value = Boolean.valueOf(te.attribute("value").getStringValue());
				}else if(value instanceof String || value == null){
					value = te.attribute("value").getStringValue();
				}
				beanMap.put(te.attribute("name").getStringValue(), value);
			}else {
				List<Element> tl = te.elements();
				for(Element tte : tl) {
					properties.put(tte.attribute("key").getStringValue(), tte.getTextTrim());
				}
			}
		}
		baseDataSource = (BaseDataSource)beanMap.getBean();
		baseDataSource.setConnectionProperties(properties);
		return baseDataSource;
	}

}
