package com.cyou.wg.sns.ctsvr.core.net.client;

import com.cyou.wg.sns.ctsvr.core.exception.CyouSysException;
import com.cyou.wg.sns.ctsvr.core.log.LogFactory;
import com.cyou.wg.sns.ctsvr.core.protocol.codec.ByteArrayProtocolFactory;
import com.cyou.wg.sns.ctsvr.core.protocol.handler.NetClientHandler;
import java.net.InetSocketAddress;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class NetClient {

	private static final long DEFAULT_TIME_OUT = 10000L;
	private int id;
	private String clientName;
	private String svrIp;
	private int port;
	private int threadNum;
	private boolean heardSingal;
	private int timeOut;
	private int timeOutCheckInterval;
	private IoSession ioSession;
	private NioSocketConnector connector;
	private NetClientHandler ioHandler;
	private ConnectHelper helpler;

	public int getTimeOutCheckInterval() {
		return timeOutCheckInterval;
	}

	public void setTimeOutCheckInterval(int timeOutCheckInterval) {
		this.timeOutCheckInterval = timeOutCheckInterval;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getId() {
		return this.id;
	}

	public NetClient(int id, String svrIp, int port, int threadNum, String handler) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		heardSingal = true;
		this.id = id;
		this.svrIp = svrIp;
		this.port = port;
		this.threadNum = threadNum;
		ioHandler = (NetClientHandler) Class.forName(handler).newInstance();
	}

	public void init() throws InstantiationException, IllegalAccessException {
		connector = new NioSocketConnector(threadNum);
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ByteArrayProtocolFactory()));
		connector.setDefaultRemoteAddress(new InetSocketAddress(svrIp, port));
		ioHandler.setClient(this);
		ioHandler.setTimeOut(timeOut);
		ioHandler.setTimeOutCheckInterval(timeOutCheckInterval);
		connector.setHandler(ioHandler);
	}

	public void connect() {
		if (helpler == null) {
			helpler = new ConnectHelper();
			helpler.start();
		} else {
			synchronized (helpler) {
				helpler.notify();
			}
		}
	}

	public void write(Object obj) {
		if (obj == null)
			throw new CyouSysException("writing object could not be null");
		if (!(obj instanceof byte[])) {
			throw new CyouSysException("writing object could only be byte[]");
		} else {
			ioSession.write(obj);
			return;
		}
	}

	public void close() {
		ioHandler.stop();
		CloseFuture cf = ioSession.close(true);
		try {
			boolean res = cf.await(2000L);
			if (!res)
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error((new StringBuilder())
								.append("faile to close the connection with remote server ")
								.append(ioSession.getRemoteAddress().toString()).toString());
		} catch (InterruptedException e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error((new StringBuilder()).append("faile to close the connection with remote server ")
							.append(ioSession.getRemoteAddress().toString()).toString());
		}
		connector.dispose();
	}

	class ConnectHelper extends Thread {

		public void run() {
			while (heardSingal) {
				LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
						(new StringBuilder()).append(clientName)
								.append("begin to connect server").toString());
				ConnectFuture cf = connector.connect();
				boolean s = cf.awaitUninterruptibly(DEFAULT_TIME_OUT);
				if (s)
					try {
						ioSession = cf.getSession();
						LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
								(new StringBuilder()).append(clientName)
										.append("connect server successfully")
										.toString());
						ioHandler.start();
						synchronized (helpler) {
							helpler.wait();
						}
					} catch (Exception e) {
						waitTime(e);
					}
				else
					waitTime(null);
			}
		}

		private void waitTime(Exception e) {
			if (e != null)
				LogFactory
						.getLogger(LogFactory.SYS_WARN_LOG)
						.warn((new StringBuilder())
								.append(clientName)
								.append("fail to connect server, wait 3 second")
								.toString(), e);
			else
				LogFactory
						.getLogger(LogFactory.SYS_WARN_LOG)
						.warn((new StringBuilder())
								.append(clientName)
								.append("fail to connect server, wait 3 second")
								.toString());
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e1) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error(
						(new StringBuilder()).append("ConnectHelper-")
								.append(clientName).toString(), e1);
			}
		}

		public ConnectHelper() {
			this.setDaemon(true);
			this.setName((new StringBuilder()).append("ConnectHelper-")
					.append(clientName).toString());
		}
	}

}
