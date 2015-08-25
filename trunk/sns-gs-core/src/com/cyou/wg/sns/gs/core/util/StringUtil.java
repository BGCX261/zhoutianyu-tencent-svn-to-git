package com.cyou.wg.sns.gs.core.util;

public class StringUtil {
	public static final String UNDER_LINE = "_";//下划线
	public static final String COMMA = ",";//逗号
	public static final String SEMIOCLON = ";";//分号
	public static final String VERTICAL_LINE = "\\|";//竖线
	public static final String COLON = ":";//冒号
	
	
	/**
	 * 是否为空
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if(s == null || s.trim().equals("")) {
			return true;
		}
		return false;
	}
}
