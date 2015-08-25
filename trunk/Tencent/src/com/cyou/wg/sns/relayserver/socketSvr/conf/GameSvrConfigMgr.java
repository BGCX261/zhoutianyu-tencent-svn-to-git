package com.cyou.wg.sns.relayserver.socketSvr.conf;

import com.cyou.wg.sns.relayserver.core.exception.CyouSysException;
import com.cyou.wg.sns.relayserver.socketSvr.domain.GameSvrConfig;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class GameSvrConfigMgr
{

	public GameSvrConfig gameSvrConfig;
	private static GameSvrConfigMgr instance;

	public GameSvrConfigMgr()
	{
		gameSvrConfig = new GameSvrConfig();
	}

	public static GameSvrConfigMgr getInstance()
	{
		if (instance == null)
			instance = new GameSvrConfigMgr();
		return instance;
	}

	public void init(String filePath)
		throws DocumentException, ClassNotFoundException
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
		File[] fileList = dir.listFiles();
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
				if (e.getName().equals("app"))
				{
					String appid = e.attribute("appid").getStringValue();
					String appkey = e.attribute("appkey").getStringValue();
					String url = e.attribute("url").getStringValue();
					gameSvrConfig.setAppid(appid);
					gameSvrConfig.setAppkey(appkey);
					gameSvrConfig.setUrl(url);
				}
			}
		}
	}
}
