package com.cyou.wg.sns.gs.core.tools;

import java.io.IOException;

public class Tool {
	/**
	 * 初始化配置
	 */
	public static void initConf() {
		try {
			ConvertId2Sql.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
