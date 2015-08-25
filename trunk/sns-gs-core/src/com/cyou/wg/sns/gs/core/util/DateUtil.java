package com.cyou.wg.sns.gs.core.util;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtil {
	private static long baseTime = 0l;
	
	static {
		baseTime = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
	}
	
	/**
	 * 得到当前时间
	 * 相对于 2011-01-01 00:00:00 的秒数
	 * @return
	 */
	public static int currentSecond() {
		return (int)((System.currentTimeMillis() - baseTime)/1000);
	}
	/**
	 * 得到相对于当前second秒的时间
	 * @param second
	 * @return
	 */
	public static int offsetSecondTime(int second) {
		return currentSecond() + second;
	}
	/**
	 * 计算两点间的时间间隔，单位秒
	 * @param big
	 * @param small
	 * @return
	 */
	public static int secondDistance(Date big, Date small) {
		return (int)((big.getTime() - small.getTime())/1000);
	}
	/**
	 * 根据字符串时间，得到系统内使用的秒数
	 * 字符串的格式 必须符合 yyyy-mm-dd hh:mm:ss[.mmm]
	 * @param s
	 * @return
	 */
	public static int changeStr2Second(String s) {
		return (int)((Timestamp.valueOf(s).getTime() - baseTime)/1000);
	}
	/**
	 * 根据传入的日期得到当前系统的秒数
	 * @param d
	 * @return
	 */
	public static int changeDate2Second(Date d) {
		return (int)((d.getTime() - baseTime)/1000);
	}
	/**
	 * 根据系统日期得到真实日期
	 * 精确到秒
	 * @param sysSecond
	 * @return
	 */
	public static Date changeSysSecond2Date(int sysSecond) {
		return new Date(changeSysSecond2MillTime(sysSecond));
	}
	/**
	 * 根据系统日期得到真实毫秒数
	 * 精确到秒
	 * @param sysSecond
	 * @return
	 */
	public static long changeSysSecond2MillTime(int sysSecond) {
		return ((long)sysSecond) * 1000 + baseTime;
	}
	/**
	 * 此时间是否是当前天
	 * @param second
	 * @return
	 */
	public static boolean isToday(int second) {
		if(currentSecond() / 86400 - second / 86400 > 0) {
			return false;
		}
		return true;
	}
	/**
	 * 得到当天的第一秒
	 * @param second
	 * @return
	 */
	public static int getTodayFirstSecond(int second) {
		second = second - second % 86400 + 1;
		return second;
	}
	/**
	 * 此时间距离它下一天的时间
	 * @param second
	 * @return
	 */
	public static int time2NextDay(int second) {
		int nextDay = (second / 86400  + 1)* 86400;
		return nextDay - currentSecond();
	}
	
}
