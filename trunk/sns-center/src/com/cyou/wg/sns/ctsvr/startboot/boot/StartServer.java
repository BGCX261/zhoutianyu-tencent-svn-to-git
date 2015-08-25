package com.cyou.wg.sns.ctsvr.startboot.boot;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;

/**
 * @Description 根据配置文件，启动服务器
 * @author 周天宇
 *
 */
public class StartServer implements BootInterface {

	private static Map<String,Class<BaseMain>> bootClass = new HashMap<String, Class<BaseMain>>();	//启动服务的入口类配置
	
	@Override
	public void start(String type, String id) throws Exception {
		initBootClass(new File("conf/boot/boot.xml"));
		Class<BaseMain> clazz = getBootClassByType(type);
		BaseMain bm = clazz.newInstance();
		bm.setServerId(Integer.valueOf(id));
		bm.start();
	}

	/**
	 * 初始化服务器启动入口配置
	 * @param file
	 * @throws DocumentException
	 * @throws ClassNotFoundException
	 */
	public static void initBootClass(File file) throws DocumentException, ClassNotFoundException{
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		List<Element> list = root.elements();
		for(Element e : list) {
			bootClass.put(e.getName(), (Class<BaseMain>) Class.forName(e.attribute(CenterServerConfig.BOOT_CLASS).getStringValue()));
		}
	}
	
	/**
	 * 根据类型得到启动服务的主类
	 * @param type
	 * @return
	 */
	public static Class<BaseMain> getBootClassByType(String type) {
		return bootClass.get(type);
	}
}
