package com.cyou.wg.sns.gs.core.protocol;
/**
 * 返回值协议的基类
 * 返回值协议id区间 1-10000
 * @author Administrator
 *
 */
public abstract class ResponseProtocol extends BaseProtocol{
	public static final short SUCC = 1;//操作成功协议id
	public static final short FAIL = 2;//操作失败协议id
	public abstract byte[] encode() throws Exception;
}
