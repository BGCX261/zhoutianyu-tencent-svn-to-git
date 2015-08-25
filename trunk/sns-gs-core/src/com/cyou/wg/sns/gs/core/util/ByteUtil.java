package com.cyou.wg.sns.gs.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterOutputStream;

import org.apache.mina.core.buffer.IoBuffer;

import com.cyou.wg.sns.gs.core.exception.CyouAppException;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;

public class ByteUtil {
	private static Charset cs = Charset.forName("utf-8");
	public static int MAX_LENGTH = 0xffff;
	/**
	 * 把字符串转换为utf-8编码
	 * 前两个字节为字符串长度
	 * 之后为内容
	 * @param s
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] string2Byte(String s) throws UnsupportedEncodingException {
		if(s == null) {
			return null;
		}
		byte[] s1 = s.getBytes("utf-8");
		if(s1.length > MAX_LENGTH) {
			throw new CyouSysException("字符串长度过长。");
		}
		byte[] b = new byte[s1.length + 2];
		b[0] = (byte)((s1.length) >>> 8);
		b[1] = (byte)((s1.length));
		System.arraycopy(s1, 0, b, 2, s1.length);
		return b;
	}
	/**
	 * 把字符串放入buff里
	 * @param buff
	 * @param s
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static void putString2Buff(IoBuffer buff, String s) throws UnsupportedEncodingException {
		if(s == null || s.length() <= 0) {
			buff.putShort((short) 0);
			return;
		}
		byte[] s1 = s.getBytes("utf-8");
		if(s1.length > MAX_LENGTH) {
			throw new CyouSysException("字符串长度过长。");
		}
		buff.putShort((short)(s1.length));
		buff.put(s1);
	}
	/**
	 * 从buff中得到String
	 * @param buff
	 * @return
	 * @throws IOException
	 */
	public static String getStringFromBuff(IoBuffer buff) throws IOException {
		int s = covertShort2Integer(buff.getShort());
		if(s <= 0) {
			return null;
		}
		return buff.getString(s, cs.newDecoder());
	}
	
	public static String coverByteArray2String(byte[] s) {
		return new String(s, cs);
	}
	
	/**
	 * 把buff里的数据去掉没有用的部分，只返回有数据的部分
	 * @param buff
	 * @return
	 */
	public static byte[] buff2Array(IoBuffer buff) {
		byte[] res = new byte[buff.position()];
		System.arraycopy(buff.array(), 0, res, 0, res.length);
		return res;
	}
	/**
	 * 把short型数据转换为无符号数int
	 * @param s
	 * @return
	 */
	public static int covertShort2Integer(short s) {
		return 0x0000ffff & s;
	}
	/**
	 * 把byte型数据转换为无符号数int
	 * @param b
	 * @return
	 */
	public static int coverByte2Integer(byte b) {
		return 0x000000ff & b;
	}
	/**
	 * 把字节数组放入buff
	 * @param buff
	 * @param arr
	 */
	public static void putByteArray2Buff(IoBuffer buff, byte[] arr) {
		if(arr == null || arr.length == 0) {
			buff.putShort((short) 0);
			return;
		}
		if(arr.length > MAX_LENGTH) {
			throw new CyouSysException("字节数组长度过长。");
		}
		buff.putShort((short)(arr.length));
		buff.put(arr);
	}
	/**
	 * 从缓冲区中取得byte数组
	 * @param buff
	 * @return
	 */
	public static byte[] getByteArrayFromBuff(IoBuffer buff) {
		short length = buff.getShort();
		if(length <= 0) {
			return null;
		}
		byte[] res = new byte[length];
		buff.get(res);
		return res;
	}
	
	/**
	 * 把字节数组放入buff
	 * @param buff
	 * @param arr
	 */
	public static void putShortArray2Buff(IoBuffer buff, short[] arr) {
		if(arr == null || arr.length == 0) {
			buff.putShort((short) 0);
			return;
		}
		if(arr.length > MAX_LENGTH) {
			throw new CyouSysException("字节数组长度过长。");
		}
		buff.putShort((short)(arr.length));
		for(int i = 0; i < arr.length; i++) {
			buff.putShort(arr[i]);
		}
	}
	/**
	 * 从缓冲区中取得byte数组
	 * @param buff
	 * @return
	 */
	public static short[] getShortArrayFromBuff(IoBuffer buff) {
		short length = buff.getShort();
		if(length <= 0) {
			return null;
		}
		short[] res = new short[length];
		for(int i = 0; i < length; i++) {
			res[i] = buff.getShort();
		}
		return res;
	}
	
	/**
	 * 把short数组转换为byte数组
	 * @param s
	 * @return
	 */
	public static byte[] coverShortArray2ByteArray(short[] s) {
		if(s == null || s.length <= 0) {
			return null;
		}
		byte[] res = new byte[s.length * 2];
		for(int i = 0; i < s.length; i++) {
			res[i * 2] = (byte) (s[i] >> 8);
			res[i *2 + 1] = (byte) s[i];
		}
		return res;
	}
	/**
	 * 把int数组转换为byte数组
	 * @param s
	 * @return
	 */
	public static byte[] coverIntegerArray2ByteArray(int[] s) {
		if(s == null || s.length <= 0) {
			return null;
		}
		byte[] res = new byte[s.length * 4];
		for(int i = 0; i < s.length; i++) {
			res[i * 4] = (byte) (s[i] >> 24);
			res[i * 4 + 1] = (byte) (s[i] >> 16);
			res[i * 4 + 2] = (byte) (s[i] >> 8);
			res[i * 4 + 3] = (byte) s[i];
		}
		return res;
	}
	/**
	 * 把byte数组转换为int数组
	 * @param s
	 * @return
	 */
	public static int[] coverByteArray2IntegerArray(byte[] s) {
		if(s == null || s.length <= 0) {
			return null;
		}
		int[] r = new int[s.length/4];
		for(int i = 0; i < r.length; i++) {
			r[i] = toInt(s[i * 4],s[i * 4 + 1],s[i * 4 + 2],s[i * 4 + 3]);
		}
		return r;
	}
	
	/**
	 * 把byte数组转换为short数组
	 * @param s
	 * @return
	 */
	public static short[] coverByteArray2ShortArray(byte[] s) {
		if(s == null || s.length <= 0) {
			return null;
		}
		short[] r = new short[s.length/2];
		for(int i = 0; i < r.length; i++) {
			r[i] =  (short) toInt(s[i * 2],s[i * 2 + 1]);
		}
		return r;
	}
	
	public static int toInt(byte high, byte low) {
		return (high << 8) & 0x0000ff00 | low & 0x000000ff;
	}
	
	public static int toInt(byte a1, byte a2, byte a3, byte a4) {
		return a1 << 24 | (a2 << 16) & 0x00ff0000 | (a3 << 8) & 0x0000ff00 | a4 & 0x000000ff;
	}
	/**
	 * 把整数间的某几位置为定义值
	 * 右边第一位 索引为0 左边最后一位索引为31
	 * @param src
	 * @param startPos 开始位  从右到左
	 * @param value 值 大于0   低位在startPos, 左边为高位 右边为低位
	 * @return
	 */
	public static int setBitValue(int src, int startPos, int value) {
		if(value < 0 || startPos < 0 || startPos > 31) {
			throw new CyouSysException("para not legal");
		}
		return ~(~src & ~(value << startPos));
	}
	/**
	 * 得到一个整数间某几个位的值
	 * 右边第一位 索引为0 左边最后一位索引为31
	 * @param src
	 * @param startPos 开始位  从右到左
	 * @param length 从右到左的长度，左边为高位 右边为低位
	 * @return
	 */
	public static int getBitValue(int src, int startPos, int length) {
		return ((~(-1 << length) << startPos) & src) >> startPos;
	}
	
	public static byte[] zip(byte[] src) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DeflaterOutputStream o2 = new DeflaterOutputStream(out);
		try {
			o2.write(src);
			o2.finish();
		} catch (IOException e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("zip fail",e);
			throw new CyouSysException("zip fail");
		}
		return out.toByteArray();
	}
	
	public static byte[] unzip(byte[] src) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InflaterOutputStream o2 = new InflaterOutputStream(out);
		try {
			o2.write(src);
			o2.finish();
		} catch (IOException e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("zip fail",e);
			throw new CyouSysException("zip fail");
		}
		return out.toByteArray();
	}
	
	/**
	 * 把字节数组转换为16进制字符串
	 * @return
	 */
	public static char[] changeByte2HpyChar(byte[] src) {
		char[] res = new char[src.length * 2];
		for(int i = 0; i < src.length; i++) {
			res[i * 2] = changeHyp2Char((src[i] >> 4) & 0x0000000f);
			res[i * 2 + 1] = changeHyp2Char(src[i] & 0x0000000f);
		}
		return res;
	}
	
	public static byte[] changeChar2Hpy(char[] src) {
		byte[] res = new byte[src.length / 2];
		for(int i = 0; i < res.length; i++) {
			res[i] = (byte) (changeHpyChar2Byte(src[i * 2]) << 4 | changeHpyChar2Byte(src[i * 2 + 1]));
		}
		return res;
	}
	/**
	 * 把十六进制字符转换为字符串
	 * @param c
	 * @return
	 */
	public static byte changeHpyChar2Byte(char c) {
		if(c > 57) {
			return (byte) (c - 87);
		}else {
			return (byte) (c - 48);
		}
	}
	
	public static char changeHyp2Char(int value) {
		if(value < 0 || value > 15) {
			throw new CyouSysException("para not legal");
		}
		if(value < 10) {
			return (char) (48 + value);
		}else {
			return (char) (87 + value);
		}
	}
	
}
