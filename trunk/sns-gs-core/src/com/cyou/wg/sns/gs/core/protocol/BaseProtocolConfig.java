package com.cyou.wg.sns.gs.core.protocol;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cyou.wg.sns.gs.core.exception.CyouSysException;

public class BaseProtocolConfig {
	
	private Class<RequestProtocol>[] protocolMap = new Class[Short.MAX_VALUE];//id为正数
	
	private Class<RequestProtocol>[] protocolMap2 = new Class[Short.MAX_VALUE];//id为负数
	
	/**
	 * 初始化协议配置文件
	 * @throws DocumentException 
	 * @throws ClassNotFoundException 
	 */
	public  void init(String filePath) throws DocumentException, ClassNotFoundException {
		File file = new File(filePath);
		if(!file.exists()) {
			throw new CyouSysException("路径" + filePath + "不存在," + file.getAbsolutePath());
		}
		init(file);
	}
	
	public  void init(File dir) throws DocumentException, ClassNotFoundException {
		File[] fileList = dir.listFiles();
		for(File f : fileList) {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for(Element e : list) {
				if(e.getName().equals("sendMessage")) {
					short id = Short.valueOf(e.attribute("id").getStringValue());
					char[] name = e.attribute("name").getStringValue().toCharArray();
					name[0] = (char) (name[0] < 'Z'? name[0] : name[0] - 32);
					Class<RequestProtocol> c = this.createClass(String.valueOf(name));
					if(id >= 0) {
						if(protocolMap[id] != null) {
							throw new CyouSysException("存在重复的请求协议id：" + id);
						}
						protocolMap[id]=c;
					}else {
						if(protocolMap2[-id] != null) {
							throw new CyouSysException("存在重复的请求协议id：" + id);
						}
						protocolMap2[-id]=c;
					}
				}
			}
		}
	}
	
	protected Class<RequestProtocol> createClass(String name) throws ClassNotFoundException {
		return (Class<RequestProtocol>) Class.forName("com.cyou.wg.spriteTales.protocol.request." + name + "Message");
	}
	
	public  RequestProtocol getProtocolByName(Short id) throws InstantiationException, IllegalAccessException {
		Class<RequestProtocol> c = protocolMap[id];
		if(c == null) {
			throw new CyouSysException("不存在协议：" + id);
		}
		RequestProtocol t = c.newInstance();
		t.setProtocolId(Short.valueOf(id));
		return t;
	}

}
