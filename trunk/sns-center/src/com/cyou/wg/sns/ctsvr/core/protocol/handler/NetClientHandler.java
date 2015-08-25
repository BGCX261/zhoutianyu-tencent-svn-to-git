package com.cyou.wg.sns.ctsvr.core.protocol.handler;

import com.cyou.wg.sns.ctsvr.core.exception.CyouSysException;
import com.cyou.wg.sns.ctsvr.core.log.LogFactory;
import com.cyou.wg.sns.ctsvr.core.net.client.NetClient;
import com.cyou.wg.sns.gs.core.util.DateUtil;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class NetClientHandler extends IoHandlerAdapter {

	private NetClient client;
	private int timeOut;
	private int timeOutCheckInterval;
	private TimeOutThread timeOutThread;
	private long lastReveiceTimeMills;
	private boolean signal = false;

	public void start() {
		if (client == null || timeOut <= 0 || timeOutCheckInterval <= 0)
			throw new CyouSysException("Not complete initialization");
		lastReveiceTimeMills = System.currentTimeMillis();
		if (timeOutThread == null) {
			timeOutThread = new TimeOutThread();
			timeOutThread.setName("TimeOutThread_"+client.getClientName());
			timeOutThread.setDaemon(false);
			timeOutThread.start();
		} else {
			synchronized (timeOutThread) {
				timeOutThread.notify();
			}
		}
		signal = true;
	}

	public int getTimeOutCheckInterval() {
		return timeOutCheckInterval;
	}

	public void setTimeOutCheckInterval(int timeOutCheckInterval) {
		this.timeOutCheckInterval = timeOutCheckInterval;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public long getLastReveiceTimeMills() {
		return lastReveiceTimeMills;
	}

	public void setLastReveiceTimeMills(long lastReveiceTimeMills) {
		this.lastReveiceTimeMills = lastReveiceTimeMills;
	}

	public NetClient getClient() {
		return client;
	}

	public void setClient(NetClient client) {
		this.client = client;
	}

	public void messageReceived(IoSession session, Object message) {
		lastReveiceTimeMills = DateUtil.currentTimeMillis();
	}

	public void exceptionCaught(IoSession session, Throwable cause) {
		LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error(
				session.getRemoteAddress().toString(), cause);
	}

	public void sessionClosed(IoSession session) {
		lastReveiceTimeMills = 0L;
		if (session != null)
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("Close the connection with "+session.getRemoteAddress().toString());
		else
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("Close one connection");
	}

	public void sessionCreated(IoSession session) {
		if (session != null)
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("create the connection with "+session.getRemoteAddress().toString());
		else
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("create one connection");
	}

	public void sessionOpened(IoSession session) {
		signal = true;
		if (session != null)
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("open the connection with "+session.getRemoteAddress().toString());
		else
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("Open one connection");
	}

	public void stop() {
		signal = false;
	}

	class TimeOutThread extends Thread {

		public void run() {
			while (true) {
				if (!signal)
					break;
				if ((System.currentTimeMillis() - lastReveiceTimeMills) / 1000L > (long) timeOut) {
					LogFactory.getLogger(LogFactory.SYS_WARN_LOG).warn(client.getClientName()+"connect server overtime, begin to reconnect");
					try {
						client.connect();
						synchronized (timeOutThread) {
							timeOutThread.wait();
						}
					} catch (Exception e) {
						LogFactory.getLogger(LogFactory.SYS_WARN_LOG).warn(client.getClientName()+"fail to reconnect, please wait 3 second", e);
						try {
							Thread.sleep(3000L);
						} catch (InterruptedException e1) {
							LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("TimeOutThread", e1);
						}
					}
				} else {
					try {
						Thread.sleep(5000L);
						client.write(new byte[] { 1 });
					} catch (Exception e1) {
						try {
							LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("Overtime", e1);
						} catch (Exception e) {
							if (e != null)
								e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	
}
