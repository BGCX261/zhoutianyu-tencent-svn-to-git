package com.cyou.wg.sns.ctsvr.app.log2File.work;

import java.io.File;
import java.util.List;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class Log2FileConf {

	public static int recordInterval = 10;
	public static String recordDir = "/var/log/logicalLog";
	public static String fileName = "logicalLog";
	public static int fileNameSubFfixInterval = 5;
	public static String errRecordDir = "/var/log/logicalLog";
	public static String errFileName = "ErrorLog";

	public static void init(String fileName) throws DocumentException {
		File file = new File(fileName);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		List<Element> list = doc.getRootElement().elements();
		for(Element e : list) {
			if (e.getName().equals("recordInterval"))
				recordInterval = Integer.valueOf(e.attribute("value").getStringValue()).intValue();
			else if (e.getName().equals("recordDir"))
				recordDir = e.attribute("value").getStringValue();
			else if (e.getName().equals("fileName"))
				fileName = e.attribute("value").getStringValue();
			else if (e.getName().equals("fileNameSubFfixInterval"))
				fileNameSubFfixInterval = Integer.valueOf(e.attribute("value").getStringValue()).intValue();
			else if (e.getName().equals("errRecordDir"))
				errRecordDir = e.attribute("value").getStringValue();
			else if (e.getName().equals("errFileName"))
				errFileName = e.attribute("value").getStringValue();
		}
	}

}
