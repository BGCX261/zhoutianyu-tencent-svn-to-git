package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import com.cyou.wg.sns.ctsvr.app.dbSyn2.work.DbCommandCache.WriteDbCommand2FileThread;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import java.io.IOException;

public class ShutDownThread extends Thread {

	private WriteDbCommand2FileThread wt;

	public ShutDownThread(WriteDbCommand2FileThread wt) {
		this.wt = wt;
	}

	public void run() {
		try {
			wt.resetFile();
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("程序关闭结束写文件流成功");
		} catch (IOException e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("程序关闭结束写文件流失败" + getName(), e);
		}
	}
}
