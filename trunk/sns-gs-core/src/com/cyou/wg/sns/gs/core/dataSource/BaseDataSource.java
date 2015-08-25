package com.cyou.wg.sns.gs.core.dataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 单个数据库连接
 * @author Administrator
 *
 */
public class BaseDataSource extends BasicDataSource{
	
	
	public void setConnectionProperties(Properties connectionProperties) {
	    for (Enumeration names = connectionProperties.propertyNames(); names.hasMoreElements(); ) {
	      String name = (String)names.nextElement();
	      String value = connectionProperties.getProperty(name);
	      addConnectionProperty(name, value);
	    }
	}
	
}
