package com.cyou.wg.sns.gs.core.exception;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cyou.wg.sns.gs.core.protocol.BaseProtocolConfig;
import com.cyou.wg.sns.gs.core.protocol.RequestProtocol;

public class ExceptionId2Text {
	public static Map<Integer, String> map = new HashMap<Integer, String>();
	
	/**
	 * 初始化协议配置文件
	 * @throws DocumentException 
	 * @throws ClassNotFoundException 
	 */
	public static void init(String filePath) throws DocumentException, ClassNotFoundException {
		File file = new File(filePath);
		if(!file.exists()) {
			throw new CyouSysException("文件" + filePath + "不存在," + file.getAbsolutePath());
		}
		ExceptionId2Text.init(file);
	}
	
	public static void init(File file) throws DocumentException, ClassNotFoundException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		List<Element> list = root.elements();
		for(Element e : list) {
			reader = new SAXReader();
			File childFile = new File(file.getParent() + "\\" + e.attribute("fileName").getStringValue());
			if(!childFile.exists()) {
				throw new CyouSysException("文件" + childFile.getPath() + "不存在");
			}
			Document td = reader.read(childFile);
			initMap(td);
		}
	}
	/**
	 * 执行初始化
	 * @param td
	 * @throws ClassNotFoundException 
	 */
	public static void initMap(Document doc) throws ClassNotFoundException {
		Element root = doc.getRootElement();
		List<Element> list = root.elements();
		for(Element e : list) {
			int id = Integer.valueOf(e.attribute("id").getStringValue());
			if(map.containsKey(id)) {
				throw new CyouSysException("存在重复的异常配置id：" + id);
			}
			String exception = e.attribute("protocolClass").getStringValue();
			map.put(id, exception);
		}
	}
	
	public static String getExceptionStr(int id) {
		return map.get(id);
	}
}
