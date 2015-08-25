package com.cyou.wg.sns.ctsvr.core.protocol.handler;

import com.cyou.wg.sns.ctsvr.core.exception.CyouSysException;
import com.cyou.wg.sns.ctsvr.core.log.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class NetServerBaseHandler extends IoHandlerAdapter {

	public void messageReceived(IoSession session, Object message) {
		byte src[] = (byte[]) (byte[]) message;
		if (src.length == 1 && src[0] == 1) {
			session.write(new byte[] { 1 });
			return;
		}
		execute(session, message);
	}

	public void execute(IoSession session, Object message) {
		throw new CyouSysException("Must override this method");
	}

	public void exceptionCaught(IoSession session, Throwable cause) {
		LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error(
				session.getRemoteAddress().toString(), cause);
	}

	public void sessionClosed(IoSession session) {
		if (session != null)
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
					(new StringBuilder()).append("Close the remote connection")
							.append(session.getRemoteAddress().toString())
							.toString());
		else
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
					"One connection closed");
	}

	public void sessionCreated(IoSession session) {
		if (session != null)
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
					(new StringBuilder()).append("Create connection with")
							.append(session.getRemoteAddress().toString())
							.toString());
		else
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
					"One connection created");
	}

	public void sessionOpened(IoSession session) {
		if (session != null)
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
					(new StringBuilder()).append("Open connection with")
							.append(session.getRemoteAddress().toString())
							.toString());
		else
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
					"One connection opened");
		session.write(new byte[] { 1 });
	}
}
