package com.cyou.wg.sns.ctsvr.app.log2File.messageHandler;

import com.cyou.wg.sns.ctsvr.app.log2File.work.InitSchemaMapping;
import com.cyou.wg.sns.gs.core.aop.interceptor.TransInterceptor;
import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.util.ByteUtil;
import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.text.SimpleDateFormat;
import org.apache.mina.core.buffer.IoBuffer;

public class LogicalBaseLog extends BaseObject {

	private static String NULL = "{}";
	private static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static String split = "|";
	private String accId;
	private int userId;
	private int appId;
	private short svrId;
	private short zoneId;
	private short publishId;
	private long eventId;
	private String logName;
	private long timeMills;
	private int reason;
	private String username = "name";
	private String params;

	public short getSvrId() {
		return svrId;
	}

	public void setSvrId(short svrId) {
		this.svrId = svrId;
	}

	public int getAppId()
	{
		return appId;
	}

	public void setAppId(int appId)
	{
		this.appId = appId;
	}

	public short getZoneId()
	{
		return zoneId;
	}

	public void setZoneId(short zoneId)
	{
		this.zoneId = zoneId;
	}

	public short getPublishId()
	{
		return publishId;
	}

	public void setPublishId(short publishId)
	{
		this.publishId = publishId;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public long getEventId()
	{
		return eventId;
	}

	public void setEventId(long eventId)
	{
		this.eventId = eventId;
	}

	public String getLogName()
	{
		return logName;
	}

	public void setLogName(String logName)
	{
		this.logName = logName;
	}

	public LogicalBaseLog()
	{
		username = "name";
	}

	public LogicalBaseLog(String accId, int userId, String logName, int reason, String username, Object... objects) {
		this.accId = accId;
		this.userId = userId;
		svrId = 0;
		this.logName = logName;
		eventId = TransInterceptor.getTransStartNano();
		this.reason = reason;
		this.username = username;
		if (eventId <= 0)
			throw new CyouSysException("没有开启事务，不能使用此方法。");
		this.logName = logName;
		timeMills = DateUtil.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			sb.append(objects[i].toString());
			if (i < objects.length - 1) {
				sb.append(split);
			}
		}

		params = sb.toString();
	}

	public LogicalBaseLog(String accId, int userId, String logName, int reason, int eventId, String username, Object... objects) {
		this.accId = accId;
		this.userId = userId;
		this.logName = logName;
		this.eventId = System.nanoTime();
		this.logName = logName;
		timeMills = DateUtil.currentTimeMillis();
		this.reason = reason;
		this.username = username;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			sb.append(objects[i].toString());
			if (i < objects.length - 1)
				sb.append(split);
		}

		params = sb.toString();
	}

	public LogicalBaseLog(String accId, int userId, short svrId, String logName, int reason, int eventId, String username, Object... objects) {
		this.accId = accId;
		this.userId = userId;
		this.logName = logName;
		this.eventId = System.nanoTime();
		this.logName = logName;
		timeMills = DateUtil.currentTimeMillis();
		this.reason = reason;
		this.username = username;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++)
		{
			sb.append(objects[i].toString());
			if (i < objects.length - 1)
				sb.append(split);
		}

		params = sb.toString();
	}

	public String getBaseObjectKey()
	{
		return null;
	}

	public short getBaseObjectType()
	{
		return 0;
	}

	public byte getStorageType()
	{
		return 0;
	}

	public BaseObject decode(IoBuffer buff) throws Exception {
		accId = ByteUtil.getStringFromBuff(buff);
		userId = buff.getInt();
		appId = buff.getInt();
		svrId = buff.getShort();
		zoneId = buff.getShort();
		publishId = buff.getShort();
		eventId = buff.getLong();
		reason = buff.getInt();
		logName = ByteUtil.getStringFromBuff(buff);
		timeMills = buff.getLong();
		username = ByteUtil.getStringFromBuff(buff);
		params = ByteUtil.getStringFromBuff(buff);
		return this;
	}

	public byte[] encode() throws Exception {
		IoBuffer buff = IoBuffer.allocate(128);
		buff.setAutoExpand(true);
		ByteUtil.putString2Buff(buff, accId);
		buff.putInt(userId);
		buff.putInt(appId);
		buff.putShort(svrId);
		buff.putShort(zoneId);
		buff.putShort(publishId);
		buff.putLong(eventId);
		buff.putInt(reason);
		ByteUtil.putString2Buff(buff, logName);
		buff.putLong(timeMills);
		ByteUtil.putString2Buff(buff, username);
		ByteUtil.putString2Buff(buff, params);
		return ByteUtil.buff2Array(buff);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(logName);
		sb.append(split);
		sb.append(date.format(Long.valueOf(timeMills)));
		sb.append(split);
		sb.append(appId);
		sb.append(split);
		sb.append(svrId);
		sb.append(split);
		sb.append(zoneId);
		sb.append(split);
		sb.append(publishId);
		sb.append(split);
		sb.append(eventId);
		sb.append(split);
		sb.append(reason);
		sb.append(split);
		sb.append(accId);
		sb.append(split);
		sb.append(userId);
		sb.append(split);
		sb.append(username);
		sb.append(split);
		sb.append(params);
		return sb.toString();
	}

	public boolean getJsonParams(String logname) {
		String keys[] = InitSchemaMapping.getKeys(logname);
		String objects[] = params.split("\\" + split);
		if (keys == null) {
			System.out.println("没有对应的logname");
			return false;
		}
		if (keys.length < objects.length) {
			System.out.println("key跟Value个数不匹配");
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < keys.length; i++) {
			if (i > objects.length - 1)
				sb.append((new StringBuilder()).append("\"").append(keys[i]).append("\"").append(":").append("\"").append("\"").toString());
			else
				sb.append((new StringBuilder()).append("\"").append(keys[i]).append("\"").append(":").append("\"").append(objects[i].toString()).append("\"").toString());
			if (i != keys.length - 1)
				sb.append(",");
		}

		sb.append("}");
		params = sb.toString();
		return true;
	}

	public long getTimeMills()
	{
		return timeMills;
	}

	public void setTimeMills(long timeMills)
	{
		this.timeMills = timeMills;
	}

}
