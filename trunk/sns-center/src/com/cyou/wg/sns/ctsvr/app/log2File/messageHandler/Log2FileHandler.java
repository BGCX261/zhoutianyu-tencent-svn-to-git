package com.cyou.wg.sns.ctsvr.app.log2File.messageHandler;

import com.cyou.wg.sns.ctsvr.app.log2File.work.MessageCache;
import com.cyou.wg.sns.ctsvr.core.protocol.handler.NetServerBaseHandler;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import org.apache.mina.core.session.IoSession;

public class Log2FileHandler extends NetServerBaseHandler {


	public void execute(IoSession session, Object message) {
		try {
			MessageCache.addMessage((byte[])message);
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("�������", e);
		}
	}
	
}
