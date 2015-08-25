package com.cyou.wg.sns.relayserver.core.net.boot;

import java.io.File;
import java.util.Iterator;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class StartServer
{

	public StartServer()
	{
	}

	public static void main(String args[])
		throws Exception
	{
		Class clazz = getBootClazz(new File("conf/net/boot.xml"), args[0]);
		BaseServerBoot bm = (BaseServerBoot)clazz.newInstance();
		bm.setServerId(Integer.valueOf(args[1]).intValue());
		bm.init();
		bm.start();
	}

	public static Class getBootClazz(File file, String type)
		throws ClassNotFoundException, DocumentException
	{
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		Iterator eleI = root.elements().iterator();
		while(eleI.hasNext()){
			Element e = (Element)eleI.next();
			if (e.getName().equals(type))
				return Class.forName(e.attribute("bootClass").getStringValue());
		}
		return null;
	}
}
