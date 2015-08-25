package com.cyou.wg.sns.ctsvr.core.util;

import com.cyou.wg.sns.ctsvr.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.dataSource.DataSourceInit;
import java.io.File;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class CenterServerConfig {

	public static final String BOOT_CLASS = "bootClass";
	public static final String TYPE_SVR_DBSYN = "dbSyn";
	public static final String TYPE_SVR_LOG2FILE = "log2File";
	public static final String TYPE_SVR_LOG_FILE2DB = "logFile2Db";
	public static final String TYPE_CLIENT_DBSYN = "dbSyn";
	public static final String TYPE_CLIENT_LOG2FILE = "log2File";
	public static final String DOC_TYPE_SERVERS = "servers";
	public static final String DOC_TYPE_CLIENTS = "clients";
	public static ServerProperties dbSynServer[] = null;
	public static ServerProperties log2FileServer[] = null;
	public static LogFile2DbProperties logFile2DbServer[] = null;
	public static ClientProperties dbSynClient[] = null;
	public static ClientProperties log2FileClient[] = null;

	public static void initNetProperties(File file, String type)
			throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		List<Element> list = root.elements();
		for (Element e : list) {
			if (e.getName().equals(DOC_TYPE_SERVERS))
				initServerProperties(e, type);
			else if (e.getName().equals(DOC_TYPE_CLIENTS))
				initClientProperties(e, type);
		}
	}

	private static void initServerProperties(Element element, String type) {
		List<Element> list = element.elements();
		for (Element e : list) {
			if (e.getName().equals(type))
				if (type.equals(TYPE_SVR_DBSYN))
					initDbSynServer(e);
				else if (type.equals(TYPE_SVR_LOG2FILE))
					initLog2FileServer(e);
				else if (type.equals(TYPE_SVR_LOG_FILE2DB))
					initLogFile2DbServer(e);
				else
					throw new CyouSysException((new StringBuilder()).append("Server type error").append(type).toString());
		}
	}

	private static void initDbSynServer(Element element) {
		List<Element> list = element.elements();
		dbSynServer = new ServerProperties[list.size()];
		for (Element e : list) {
			List<Element> properties = e.elements();
			int id = Integer.valueOf(e.attribute("id").getStringValue()).intValue();
			ServerProperties sp = new ServerProperties();
			sp.setId(id);
			sp.setType(TYPE_SVR_DBSYN);
			for (Element p : properties) {
				if (p.getName().equals("host")) {
					String host = p.attribute("value").getStringValue();
					sp.setIp(host.split(":")[0]);
					sp.setPort(Integer.valueOf(host.split(":")[1]).intValue());
				} else if (p.getName().equals("threadNum")) {
					sp.setThreadNum(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				} else if (p.getName().equals("handler")) {
					sp.setHandler(p.attribute("value").getStringValue());
				} else if (p.getName().equals("workThreadNum")) {
					sp.setWorkThreadNum(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				} else if (p.getName().equals("interval")) {
					sp.setInterval(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				} else if (p.getName().equals("createFileInterval")) {
					sp.setCreateFileInterval(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				} else if (p.getName().equals("readFileInterval")) {
					sp.setReadFileInterval(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				} else if (p.getName().equals("delFileInterval")) {
					sp.setDelFileInterval(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				} else if (p.getName().equals("tmpFilePath")) {
					sp.setTmpFielPath(p.attribute("value").getStringValue());
				}
				dbSynServer[id-1] = sp;
			}
		}

	}

	private static void initLog2FileServer(Element element) {
		List<Element> list = element.elements();
		log2FileServer = new ServerProperties[list.size()];
		for (Element e : list) {
			List<Element> properties = e.elements();
			int id = Integer.valueOf(e.attribute("id").getStringValue()).intValue();
			ServerProperties sp = new ServerProperties();
			sp.setId(id);
			sp.setType(TYPE_SVR_LOG2FILE);
			for (Element p : properties) {
				if (p.getName().equals("host")) {
					String host = p.attribute("value").getStringValue();
					sp.setIp(host.split(":")[0]);
					sp.setPort(Integer.valueOf(host.split(":")[1]).intValue());
				} else if (p.getName().equals("threadNum"))
					sp.setThreadNum(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				else if (p.getName().equals("handler"))
					sp.setHandler(p.attribute("value").getStringValue());
			}
		}
	}

	private static void initLogFile2DbServer(Element element) {
		List<Element> list = element.elements();
		logFile2DbServer = new LogFile2DbProperties[list.size()];
		for (Element e : list) {
			List<Element> properties = e.elements();
			int id = Integer.valueOf(e.attribute("id").getStringValue()).intValue();
			LogFile2DbProperties lp = new LogFile2DbProperties();
			lp.setId(id);
			for (Element p : properties) {
				if (p.getName().equals("dbHost")) {
					javax.sql.DataSource dataSource = DataSourceInit.create(p);
					lp.setDataSource(dataSource);
				} else if (p.getName().equals("interval")) {
					lp.setInterval(Integer.valueOf(p.attribute("value").getStringValue()).intValue());
				}
				else if (p.getName().equals("logFileDir")) {
					lp.setLogFileDir(p.attribute("value").getStringValue());
				}
			}
		}
		
	}

	public static void initClientProperties(Element element, String type) {
		List<Element> list = element.elements();
		for(Element e : list) {
			if (e.getName().equals(type)) {
				if (type.equals(TYPE_SVR_DBSYN)) {
					initDbSynClient(e);
				}
				else if (type.equals(TYPE_SVR_LOG2FILE)) {
					initLog2FileClient(e);
				}
				else {
					throw new CyouSysException((new StringBuilder()).append("Server initializtion type error").append(type).toString());
				}
			}
		}
	}

	public static void initDbSynClient(Element element) {
		List<Element> list = element.elements();
		dbSynClient = new ClientProperties[list.size()];
		for (Element e : list) {
			List<Element> properties = e.elements();
			int id = Integer.valueOf(e.attribute("id").getStringValue()).intValue();
			ClientProperties cp = new ClientProperties();
			cp.setId(id);
			cp.setType(TYPE_SVR_DBSYN);
			for (Element p : properties) {
				if (p.getName().equals("serverId")) {
					int serverId = Integer.valueOf(
							p.attribute("value").getStringValue()).intValue();
					cp.setServerId(serverId);
				} else if (p.getName().equals("threadNum"))
					cp.setThreadNum(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				else if (p.getName().equals("handler"))
					cp.setHandler(p.attribute("value").getStringValue());
				else if (p.getName().equals("timeOut"))
					cp.setTimeOut(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue() * 1000);
				else if (p.getName().equals("timeOutCheckInterval"))
					cp.setTimeOutCheckInterval(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue() * 1000);
			}
		}
		
	}

	public static void initLog2FileClient(Element element) {
		List<Element> list = element.elements();
		log2FileClient = new ClientProperties[list.size()];
		for(Element e : list) {
			List<Element> properties = e.elements();
			int id = Integer.valueOf(e.attribute("id").getStringValue()).intValue();
			ClientProperties cp  = new ClientProperties();
			cp.setId(id);
			cp.setType(TYPE_SVR_LOG2FILE);
			for(Element p : properties) {
				if (p.getName().equals("serverId")) {
					int serverId = Integer.valueOf(
							p.attribute("value").getStringValue()).intValue();
					cp.setServerId(serverId);
				} else if (p.getName().equals("threadNum"))
					cp.setThreadNum(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue());
				else if (p.getName().equals("handler"))
					cp.setHandler(p.attribute("value").getStringValue());
				else if (p.getName().equals("timeOut"))
					cp.setTimeOut(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue() * 1000);
				else if (p.getName().equals("timeOutCheckInterval"))
					cp.setTimeOutCheckInterval(Integer.valueOf(
							p.attribute("value").getStringValue()).intValue() * 1000);
			}
		}
		
	}

}
