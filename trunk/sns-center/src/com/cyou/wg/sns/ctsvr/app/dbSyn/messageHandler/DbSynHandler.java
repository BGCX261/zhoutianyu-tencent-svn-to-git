// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DbSynHandler.java

package com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler;

import com.cyou.wg.sns.ctsvr.app.dbSyn.work.DbCommandCache;
import com.cyou.wg.sns.ctsvr.core.protocol.handler.NetServerBaseHandler;
import org.apache.mina.core.session.IoSession;

public class DbSynHandler extends NetServerBaseHandler
{

	public DbSynHandler()
	{
	}

	public void execute(IoSession session, Object message)
	{
		DbCommandCache.addCommand((byte[])(byte[])message);
	}
}
