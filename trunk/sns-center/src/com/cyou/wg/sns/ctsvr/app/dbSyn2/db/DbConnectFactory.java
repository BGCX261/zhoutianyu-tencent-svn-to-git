package com.cyou.wg.sns.ctsvr.app.dbSyn2.db;

import org.apache.commons.dbcp.BasicDataSource;

public class DbConnectFactory
{

	public static DbConnect getDbConnect(BasicDataSource basicDataSource)
	{
		return new DbConnect(basicDataSource.getDriverClassName(), basicDataSource.getUrl(), basicDataSource.getUsername(), basicDataSource.getPassword());
	}
	
}
