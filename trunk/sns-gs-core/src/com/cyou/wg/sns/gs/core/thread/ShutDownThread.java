package com.cyou.wg.sns.gs.core.thread;

import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;

public class ShutDownThread extends Thread{
	
	public void run() {
		try {
			SqlMapClientMgr.closeAll();
			
		}catch (Exception e) {
			e.printStackTrace();
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("开始强制关闭jvm");
			Runtime.getRuntime().halt(1);
		}
	}

}
