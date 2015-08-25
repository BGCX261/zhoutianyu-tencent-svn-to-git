package com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler;

import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.util.ByteUtil;
import java.io.IOException;
import java.sql.Timestamp;
import org.apache.mina.core.buffer.IoBuffer;

public class DbCommand extends BaseObject
{

	private static final byte DATA_TYPE_NULL = 1;
	private static final byte DATA_TYPE_INT = 2;
	private static final byte DATA_TYPE_STR = 3;
	private static final byte DATA_TYPE_TIMESTAMP = 4;
	private static final byte DATA_TYPE_SHORT = 5;
	private static final byte DATA_TYPE_BYTE_ARRAY = 6;
	private static final byte DATA_TYPE_BYTE = 7;
	private static final byte DATA_TYPE_LONG = 8;
	private static int MAX_PARAMS = 255;
	private byte dbIndex;
	private short objectType;
	private String key;
	private String baseSql;
	private Object params[];
	private DbCommand nextCommand;

	public DbCommand getNextCommand()
	{
		return nextCommand;
	}

	public void setNextCommand(DbCommand nextCommand)
	{
		this.nextCommand = nextCommand;
	}

	public short getObjectType()
	{
		return objectType;
	}

	public void setObjectType(short objectType)
	{
		this.objectType = objectType;
	}

	public String getBaseObjectKey()
	{
		return key;
	}

	public short getBaseObjectType()
	{
		throw new CyouSysException("���ܵ��ô˷�����");
	}

	public BaseObject decode(IoBuffer buff)
		throws Exception
	{
		dbIndex = buff.get();
		baseObjectOpt = buff.get();
		objectType = buff.getShort();
		key = ByteUtil.getStringFromBuff(buff);
		baseSql = ByteUtil.getStringFromBuff(buff);
		params = new Object[ByteUtil.coverByte2Integer(buff.get())];
		getParamsFromBuff(buff, params);
		return this;
	}

	public byte[] encode()
		throws Exception
	{
		IoBuffer buff = IoBuffer.allocate(64);
		buff.setAutoExpand(true);
		buff.put(dbIndex);
		buff.put(baseObjectOpt);
		buff.putShort(objectType);
		ByteUtil.putString2Buff(buff, key);
		ByteUtil.putString2Buff(buff, baseSql);
		if (params.length > MAX_PARAMS)
		{
			throw new CyouSysException((new StringBuilder()).append("�����������key��").append(key).append(", dbIndex :").append(dbIndex).append(", num :").append(params.length).toString());
		} else
		{
			buff.put((byte)params.length);
			putParams(buff, params);
			return ByteUtil.buff2Array(buff);
		}
	}

	public byte getStorageType()
	{
		throw new CyouSysException("���ܵ��ô˷�����");
	}

	public byte getDbIndex()
	{
		return dbIndex;
	}

	public void setDbIndex(byte dbIndex)
	{
		this.dbIndex = dbIndex;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getBaseSql()
	{
		return baseSql;
	}

	public void setBaseSql(String baseSql)
	{
		this.baseSql = baseSql;
	}

	public Object[] getParams()
	{
		return params;
	}

	public void setParams(Object params[])
	{
		this.params = params;
	}

	public void putParams(IoBuffer buff, Object para[])
		throws Exception
	{
		for (int i = 0; i < para.length; i++)
		{
			Object o = para[i];
			if (o == null)
			{
				buff.put((byte)1);
				continue;
			}
			if (o instanceof Long)
			{
				buff.put((byte)8);
				buff.putLong(((Long)o).longValue());
				continue;
			}
			if (o instanceof Integer)
			{
				buff.put((byte)2);
				buff.putInt(((Integer)o).intValue());
				continue;
			}
			if (o instanceof String)
			{
				buff.put((byte)3);
				ByteUtil.putString2Buff(buff, (String)o);
				continue;
			}
			if (o instanceof Timestamp)
			{
				buff.put((byte)4);
				ByteUtil.putString2Buff(buff, ((Timestamp)o).toString());
				continue;
			}
			if (o instanceof byte[])
			{
				if (((byte[])(byte[])o).length > 65535)
					throw new CyouSysException("�ֽ����ĳ��ȹ�");
				buff.put((byte)6);
				ByteUtil.putByteArray2Buff(buff, (byte[])(byte[])o);
				continue;
			}
			if (o instanceof Short)
			{
				buff.put((byte)5);
				buff.putShort(((Short)o).shortValue());
				continue;
			}
			if (o instanceof Byte)
			{
				buff.put((byte)7);
				buff.put(((Byte)o).byteValue());
			} else
			{
				throw new CyouSysException((new StringBuilder()).append("��֧�ֵ�������� ��").append(o.getClass().getName()).toString());
			}
		}

	}

	public void getParamsFromBuff(IoBuffer buff, Object para[])
		throws IOException
	{
		for (int i = 0; i < para.length; i++)
		{
			byte type = buff.get();
			if (type == 1)
			{
				para[i] = null;
				continue;
			}
			if (type == 8)
			{
				para[i] = Long.valueOf(buff.getLong());
				continue;
			}
			if (type == 2)
			{
				para[i] = Integer.valueOf(buff.getInt());
				continue;
			}
			if (type == 3)
			{
				para[i] = ByteUtil.getStringFromBuff(buff);
				continue;
			}
			if (type == 6)
			{
				para[i] = ByteUtil.getByteArrayFromBuff(buff);
				continue;
			}
			if (type == 4)
			{
				para[i] = Timestamp.valueOf(ByteUtil.getStringFromBuff(buff));
				continue;
			}
			if (type == 5)
			{
				para[i] = Short.valueOf(buff.getShort());
				continue;
			}
			if (type == 7)
				para[i] = Byte.valueOf(buff.get());
			else
				throw new CyouSysException((new StringBuilder()).append("��֧�ֵ�������� ��").append(buff.get()).toString());
		}

	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("::");
		sb.append(getBaseObjectOpt());
		sb.append("::");
		sb.append(baseSql);
		sb.append("::");
		for (int i = 0; i < params.length; i++)
		{
			sb.append(params[i]);
			sb.append(",");
		}

		return sb.toString();
	}

}
