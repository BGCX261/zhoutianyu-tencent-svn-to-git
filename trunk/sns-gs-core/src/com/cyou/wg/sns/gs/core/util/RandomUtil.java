package com.cyou.wg.sns.gs.core.util;

import java.util.Random;

public class RandomUtil {
	
	public static final int SEED = 1000000;
	
	public static final int PERCENT_MUTI = 10000;
	
	private static final Random rdm = new Random();
	
	public static int getNext(int n) {
		return rdm.nextInt(n);
	}
	
	public static double getNext() {
		return rdm.nextDouble();
	}

}
