package com.cyou.wg.sns.gs.core.domain;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 所有业务基本对象的基类
 * @author Administrator
 *
 */
public abstract class BaseObject{
	/**
	 * 存储在本gs和memcached中的对象
	 * 查询时先访问本地内存，之后再访问memcached
	 * 修改时两个地方同时修改
	 */
	public static final byte STORAGE_TYPE_DOUBLE = 1;
	/**
	 * 只存储在memcached的对象
	 */
	public static final byte STORAGE_TYPE_REMOTE = 0;
	/**
	 * 只存储在本gs的对象
	 */
	public static final byte STORAGE_TYPE_ONLY_LOCAL = 2;
	
	public static final byte BASE_OBJ_OPT_NO = 0;//不执行任何操作
	public static final byte BASE_OBJ_OPT_UPDATE = 1;//数据操作：更新
	public static final byte BASE_OBJ_OPT_INSERT = 2;//数据操作：增加
	public static final byte BASE_OBJ_OPT_DEL = 3;//数据操作：删除
	
	
	protected byte baseObjectOpt = 0;
	

	public byte getBaseObjectOpt() {
		return baseObjectOpt;
	}

	public void setBaseObjectOpt(byte baseObjectOpt) {
		this.baseObjectOpt = baseObjectOpt;
	}
	
	public abstract String getBaseObjectKey();//得到标识此对象的key
	public abstract short getBaseObjectType();//得到此对象的类型数据，从memcached反序列化时用到
	public abstract byte getStorageType();//得到此对象的存储类型
	
	
	
	public abstract BaseObject decode(IoBuffer src) throws Exception;//对此对象进行解码
	public abstract byte[] encode() throws Exception;//进行编码
}
