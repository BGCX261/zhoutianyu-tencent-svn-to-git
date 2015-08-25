package com.cyou.wg.sns.relayserver.core.util;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;

public class ByteUtil
{

	private static Charset cs = Charset.forName("utf-8");
	public static int MAX_LENGTH = 65535;

	public static byte[] string2Byte(String s)
		throws UnsupportedEncodingException
	{
		if (s == null)
			return null;
		byte s1[] = s.getBytes("utf-8");
		if (s1.length > MAX_LENGTH)
		{
			throw new RuntimeException("字符串长度过长。");
		} else
		{
			byte b[] = new byte[s1.length + 2];
			b[0] = (byte)(s1.length >>> 8);
			b[1] = (byte)s1.length;
			System.arraycopy(s1, 0, b, 2, s1.length);
			return b;
		}
	}

	public static void putString2Buff(ChannelBuffer buff, String s)
		throws UnsupportedEncodingException
	{
		if (s == null || s.length() <= 0)
		{
			buff.writeShort(0);
			return;
		}
		byte s1[] = s.getBytes("utf-8");
		if (s1.length > MAX_LENGTH)
		{
			throw new RuntimeException("字符串长度过长。");
		} else
		{
			buff.writeShort((short)s1.length);
			buff.writeBytes(s1);
			return;
		}
	}

	public static void putString2Buff(ChannelBuffer buff, String s, int length)
		throws UnsupportedEncodingException
	{
		if (s == null || length <= 0)
		{
			buff.writeShort(0);
			return;
		}
		byte s1[] = s.getBytes("utf-8");
		if (s1.length > MAX_LENGTH)
			throw new RuntimeException("字符串长度过长。");
		byte res[] = new byte[length];
		for (int i = 0; i < s1.length; i++)
			res[i] = s1[i];

		buff.writeBytes(res);
	}

	public static String getStringFromBuff(ChannelBuffer buff)
		throws IOException
	{
		int s = covertShort2Integer(buff.readShort());
		if (s <= 0)
		{
			return null;
		} else
		{
			byte res[] = new byte[s];
			buff.readBytes(res);
			return new String(res, cs);
		}
	}

	public static String getStringFromBuff(ChannelBuffer buff, int length)
		throws IOException
	{
		if (length <= 0)
			return null;
		byte res[] = new byte[length];
		buff.readBytes(res);
		for (int i = 0; i < res.length; i++)
		{
			System.out.print(res[i]);
			System.out.print(" ");
		}

		System.out.println("");
		return (new String(res, cs)).trim();
	}

	public static String coverByteArray2String(byte s[])
	{
		return new String(s, cs);
	}

	public static byte[] buff2Array(ChannelBuffer buff)
	{
		byte res[] = new byte[buff.readableBytes()];
		System.arraycopy(buff.array(), 0, res, 0, res.length);
		return res;
	}

	public static int covertShort2Integer(short s)
	{
		return 0xffff & s;
	}

	public static int coverByte2Integer(byte b)
	{
		return 0xff & b;
	}

	public static void putByteArray2Buff(ChannelBuffer buff, byte arr[])
	{
		if (arr == null || arr.length == 0)
		{
			buff.writeShort(0);
			return;
		}
		if (arr.length > MAX_LENGTH)
		{
			throw new RuntimeException("字节数组长度过长。");
		} else
		{
			buff.writeShort((short)arr.length);
			buff.writeBytes(arr);
			return;
		}
	}

	public static byte[] getByteArrayFromBuff(ChannelBuffer buff)
	{
		short length = buff.readShort();
		if (length <= 0)
		{
			return null;
		} else
		{
			byte res[] = new byte[length];
			buff.readBytes(res);
			return res;
		}
	}

	public static void putShortArray2Buff(ChannelBuffer buff, short arr[])
	{
		if (arr == null || arr.length == 0)
		{
			buff.writeShort(0);
			return;
		}
		if (arr.length > MAX_LENGTH)
			throw new RuntimeException("字节数组长度过长。");
		buff.writeShort((short)arr.length);
		for (int i = 0; i < arr.length; i++)
			buff.writeShort(arr[i]);

	}

	public static short[] getShortArrayFromBuff(ChannelBuffer buff)
	{
		short length = buff.readShort();
		if (length <= 0)
			return null;
		short res[] = new short[length];
		for (int i = 0; i < length; i++)
			res[i] = buff.readShort();

		return res;
	}

	public static byte[] coverShortArray2ByteArray(short s[])
	{
		if (s == null || s.length <= 0)
			return null;
		byte res[] = new byte[s.length * 2];
		for (int i = 0; i < s.length; i++)
		{
			res[i * 2] = (byte)(s[i] >> 8);
			res[i * 2 + 1] = (byte)s[i];
		}

		return res;
	}

	public static byte[] coverIntegerArray2ByteArray(int s[])
	{
		if (s == null || s.length <= 0)
			return null;
		byte res[] = new byte[s.length * 4];
		for (int i = 0; i < s.length; i++)
		{
			res[i * 4] = (byte)(s[i] >> 24);
			res[i * 4 + 1] = (byte)(s[i] >> 16);
			res[i * 4 + 2] = (byte)(s[i] >> 8);
			res[i * 4 + 3] = (byte)s[i];
		}

		return res;
	}

	public static int[] coverByteArray2IntegerArray(byte s[])
	{
		if (s == null || s.length <= 0)
			return null;
		int r[] = new int[s.length / 4];
		for (int i = 0; i < r.length; i++)
			r[i] = toInt(s[i * 4], s[i * 4 + 1], s[i * 4 + 2], s[i * 4 + 3]);

		return r;
	}

	public static short[] coverByteArray2ShortArray(byte s[])
	{
		if (s == null || s.length <= 0)
			return null;
		short r[] = new short[s.length / 2];
		for (int i = 0; i < r.length; i++)
			r[i] = (short)toInt(s[i * 2], s[i * 2 + 1]);

		return r;
	}

	public static int toInt(byte high, byte low)
	{
		return high << 8 & 0xff00 | low & 0xff;
	}

	public static int toInt(byte a1, byte a2, byte a3, byte a4)
	{
		return a1 << 24 | a2 << 16 & 0xff0000 | a3 << 8 & 0xff00 | a4 & 0xff;
	}

	public static int setBitValue(int src, int startPos, int value)
	{
		if (value < 0 || startPos < 0 || startPos > 31 && (value > 1 || value < 0))
			throw new RuntimeException("para not legal");
		else
			return ~(~src & ~(value << startPos));
	}

	public static int setBitValue(int src, int startPos, int value, int overWriteLength)
	{
		if (value < 0 || startPos < 0 || startPos > 31)
			throw new RuntimeException("para not legal");
		else
			return ~(~(-1 << overWriteLength) << startPos) & src | value << startPos;
	}

	public static int getBitValue(int src, int startPos, int length)
	{
		return (~(-1 << length) << startPos & src) >> startPos;
	}

	public static byte[] zip(byte src[])
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DeflaterOutputStream o2 = new DeflaterOutputStream(out);
		try
		{
			o2.write(src);
			o2.finish();
		}
		catch (IOException e)
		{
			LogFactory.getLogger("sys_error").error("zip fail", e);
			throw new RuntimeException("zip fail");
		}
		return out.toByteArray();
	}

	public static byte[] unzip(byte src[])
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InflaterOutputStream o2 = new InflaterOutputStream(out);
		try
		{
			o2.write(src);
			o2.finish();
		}
		catch (IOException e)
		{
			LogFactory.getLogger("sys_error").error("zip fail", e);
			throw new RuntimeException("zip fail");
		}
		return out.toByteArray();
	}

	public static char[] changeByte2HpyChar(byte src[])
	{
		char res[] = new char[src.length * 2];
		for (int i = 0; i < src.length; i++)
		{
			res[i * 2] = changeHyp2Char(src[i] >> 4 & 0xf);
			res[i * 2 + 1] = changeHyp2Char(src[i] & 0xf);
		}

		return res;
	}

	public static byte[] changeChar2Hpy(char src[])
	{
		byte res[] = new byte[src.length / 2];
		for (int i = 0; i < res.length; i++)
			res[i] = (byte)(changeHpyChar2Byte(src[i * 2]) << 4 | changeHpyChar2Byte(src[i * 2 + 1]));

		return res;
	}

	public static byte changeHpyChar2Byte(char c)
	{
		if (c > '9')
			return (byte)(c - 87);
		else
			return (byte)(c - 48);
	}

	public static char changeHyp2Char(int value)
	{
		if (value < 0 || value > 15)
			throw new RuntimeException("para not legal");
		if (value < 10)
			return (char)(48 + value);
		else
			return (char)(87 + value);
	}

	public static byte[] low2High(byte lowBytes[])
	{
		ByteBuffer bb = ByteBuffer.wrap(lowBytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.array();
	}

	public static short shortL2H(short low)
	{
		byte b[] = new byte[2];
		b[0] = (byte)(low & 0xff);
		b[1] = (byte)(low >> 8 & 0xff);
		return (short)(b[0] << 8 & 0xff00 | b[1] & 0xff);
	}

	public static int intL2H(int low)
	{
		byte b[] = new byte[4];
		b[0] = (byte)(low & 0xff);
		b[1] = (byte)(low >> 8 & 0xff);
		b[2] = (byte)(low >> 16 & 0xff);
		b[3] = (byte)(low >> 24 & 0xff);
		return b[0] << 24 & 0xff000000 | b[1] << 16 & 0xff0000 | b[1] << 8 & 0xff00 | b[0] & 0xff;
	}

	public static short readShort(ChannelBuffer cb)
	{
		return shortL2H(cb.readShort());
	}

	public static int readInt(ChannelBuffer cb)
	{
		return intL2H(cb.readInt());
	}

}
