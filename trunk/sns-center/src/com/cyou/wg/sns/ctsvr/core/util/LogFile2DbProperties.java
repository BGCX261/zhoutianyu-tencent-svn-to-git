package com.cyou.wg.sns.ctsvr.core.util;

import javax.sql.DataSource;

public class LogFile2DbProperties
{

	private int id;
	private int interval;
	private String logFileDir;
	private DataSource dataSource;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public int getInterval()
	{
		return interval;
	}

	public void setInterval(int interval)
	{
		this.interval = interval;
	}

	public String getLogFileDir()
	{
		return logFileDir;
	}

	public void setLogFileDir(String logFileDir)
	{
		this.logFileDir = logFileDir;
	}
}
