// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Main.java

package com.cyou.wg.sns.ctsvr.app.logFile2Db.boot;

import com.cyou.wg.sns.ctsvr.app.logFile2Db.work.WorkThread;
import com.cyou.wg.sns.ctsvr.core.boot.BaseMain;
import com.cyou.wg.sns.ctsvr.core.util.CenterServerConfig;
import com.cyou.wg.sns.gs.core.threadPool.CoreThreadPool;
import java.io.File;

public class Main extends BaseMain
{

	public Main()
	{
	}

	public void start()
		throws Exception
	{
		CenterServerConfig.initNetProperties(new File("conf/centerServer/centerServer.xml"), "logFile2Db");
		CoreThreadPool.init();
		WorkThread wt = new WorkThread(super.getServerId());
		wt.setDaemon(false);
		wt.setName("WorkThread");
		wt.start();
	}
}
