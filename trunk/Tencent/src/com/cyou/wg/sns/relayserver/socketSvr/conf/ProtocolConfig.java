package com.cyou.wg.sns.relayserver.socketSvr.conf;

import com.cyou.wg.sns.relayserver.core.exception.CyouSysException;
import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class ProtocolConfig
{

	private static ProtocolConfig instance;
	private Class protocolMap[];
	private Class protocolMap2[];

	public ProtocolConfig()
	{
		protocolMap = new Class[32767];
		protocolMap2 = new Class[32767];
	}

	public static ProtocolConfig getInstance()
	{
		if (instance == null)
			instance = new ProtocolConfig();
		return instance;
	}

	public void init(String filePath) throws DocumentException, ClassNotFoundException
	{
		File file = new File(filePath);
		if (!file.exists())
		{
			throw new CyouSysException((new StringBuilder()).append("路径").append(filePath).append("不存在,").append(file.getAbsolutePath()).toString());
		} else
		{
			init(file);
			return;
		}
	}

	public void init(File dir)
		throws DocumentException, ClassNotFoundException
	{
		File fileList[] = dir.listFiles();
		int len = fileList.length;
		for (int i = 0; i < len; i++)
		{
			File f = fileList[i];
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			List list = root.elements();
			Iterator eleI = list.iterator();
			while(eleI.hasNext()){
				Element e = (Element)eleI.next();
				if (e.getName().equals("sendMessage"))
				{
					short id = Short.valueOf(e.attribute("id").getStringValue()).shortValue();
					char name[] = e.attribute("name").getStringValue().toCharArray();
					name[0] = (char)(name[0] >= 'Z' ? name[0] - 32 : name[0]);
					Class c = createClass(String.valueOf(name));
					if (id >= 0)
					{
						if (protocolMap[id] != null)
							throw new CyouSysException((new StringBuilder()).append("存在重复的请求协议id：").append(id).toString());
						protocolMap[id] = c;
					} else
					{
						if (protocolMap2[-id] != null)
							throw new CyouSysException((new StringBuilder()).append("存在重复的请求协议id：").append(id).toString());
						protocolMap2[-id] = c;
					}
				}
			}
		}
	}

	protected Class createClass(String name) throws ClassNotFoundException
	{
		return Class.forName((new StringBuilder()).append("com.cyou.wg.sns.relayserver.protocol.request.").append(name).append("Message").toString());
	}

	public BaseRequestProtocol getProtocolByName(Short id) throws InstantiationException, IllegalAccessException
	{
		Class c = protocolMap[id.shortValue()];
		if (c == null)
		{
			throw new CyouSysException((new StringBuilder()).append("不存在协议：").append(id).toString());
		} else
		{
			BaseRequestProtocol t = (BaseRequestProtocol)c.newInstance();
			t.setProtocolId(Short.valueOf(id.shortValue()).shortValue());
			return t;
		}
	}
}
