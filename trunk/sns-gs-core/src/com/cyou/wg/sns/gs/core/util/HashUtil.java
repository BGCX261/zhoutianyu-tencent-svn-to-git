package com.cyou.wg.sns.gs.core.util;

public class HashUtil {
	/**
	 * 把两个long换算为一个long
	 * 取自google的cityHash包，原来为c++算法，修改为java代码
	 * @param high
	 * @param low
	 * @return
	 */
	public static long hash128To64(long high, long low) {
		  long kMul = 0x9ddfea08eb382d69l;
		  long a = (low ^ high) * kMul;
		  a ^= ((a >> 47) & 0x00000000ffffffffl);
		  long b = (high ^ a) * kMul;
		  b ^= ((b >> 47) & 0x00000000ffffffffl);
		  b *= kMul;
		  return b;
	}
}
