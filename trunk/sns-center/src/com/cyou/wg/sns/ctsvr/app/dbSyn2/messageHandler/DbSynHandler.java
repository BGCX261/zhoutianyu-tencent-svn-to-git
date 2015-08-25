package com.cyou.wg.sns.ctsvr.app.dbSyn2.messageHandler;

import com.cyou.wg.sns.ctsvr.app.dbSyn2.work.DbCommandCache;
import com.cyou.wg.sns.ctsvr.core.protocol.handler.NetServerBaseHandler;
import org.apache.mina.core.session.IoSession;

public class DbSynHandler extends NetServerBaseHandler {

	public void execute(IoSession session, Object message) {
		DbCommandCache.addCommand((byte[])message);
	}
	
}
