package com.cyou.wg.sns.relayserver.core.factory.log.LogFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 
 * @author 周天宇
 */
public class LogFactory
{

	public static final String SYS_DEBUG_LOG = "sys_debug";
	public static final String SYS_INFO_LOG = "sys_info";
	public static final String SYS_WARN_LOG = "sys_warn";
	public static final String SYS_ERROR_LOG = "sys_error";
	public static final String APP_ERROR_LOG = "app_error";

	public LogFactory()
	{
	}

	public static Logger getLogger(String name)
	{
		return LoggerFactory.getLogger(name);
	}
}
