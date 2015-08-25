package com.cyou.wg.sns.ctsvr.startboot.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFactory {
	
	/**
	 * Gs log
	 */
	public static final String SYS_DEBUG_LOG = "com.cyou.wg.sns.core.log.sys.debug";
	public static final String SYS_INFO_LOG = "com.cyou.wg.sns.core.log.sys.info";
	public static final String SYS_WARN_LOG = "com.cyou.spriteTales.core.log.sys.warn";
	public static final String SYS_ERROR_LOG = "com.cyou.spriteTales.core.log.sys.error";
	
	public static final String APP_CONSOLE_LOG = "com.cyou.wg.sns.core.log.app.console";
	/**
	 * 业务的逻辑日志
	 * @param name
	 * @return
	 */
	
	public static Logger getLogger(String name) {
		return LoggerFactory.getLogger(name);
	}
}
