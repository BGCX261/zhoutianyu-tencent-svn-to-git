package com.cyou.wg.sns.relayserver.core.net.client;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import com.cyou.wg.sns.relayserver.core.net.util.ChannelUtil;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.ExternalResourceReleasable;

public class NioClient {
	
	private static final long DEFAULT_TIME_OUT = 10000L;
	private static final long DEFAULT_CHECK_DISCONNECT_TIME = 1000L;
	public static byte HeartBeat_MSG = 1;
	static final int DEFAULT_IO_THREADS = 1;
	private int id;
	private String host;
	private int port;
	private int threadNum;
	private String clientName;
	private int timeOut;
	private int timeOutCheckInterval;
	private boolean isConnected;
	private ClientBootstrap bootstrap;
	private Channel channel;
	private BaseNioClientHandler nioHandler;

	public NioClient(int id, String host, int port) {
		this(id, host, port, -1, new BaseNioClientHandler());
	}

	public NioClient(int id, String host, int port, int threadNum,
			String handler) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		this(id, host, port, threadNum, (BaseNioClientHandler) Class.forName(
				handler).newInstance());
	}

	public NioClient(int id, String host, int port, int threadNum,
			BaseNioClientHandler handler) {
		isConnected = false;
		if (handler == null)
			throw new RuntimeException("Nio client handler can't be null");
		if (threadNum <= 0)
			threadNum = DEFAULT_IO_THREADS;
		this.id = id;
		this.host = host;
		this.port = port;
		this.threadNum = threadNum;
		nioHandler = handler;
	}

	public void init() throws Exception {
		BaseClientHeartBeatHandler heartBeatHandler = new BaseClientHeartBeatHandler();
		heartBeatHandler.setClient(this);
		nioHandler.setClient(this);
		BaseClientChannelPipelineFactory pipelineFactory = new BaseClientChannelPipelineFactory(
				nioHandler, heartBeatHandler);
		init(((ChannelPipelineFactory) (pipelineFactory)));
	}

	public void init(ChannelPipelineFactory pipelineFactory) throws Exception {
		ChannelFactory factory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors
						.newCachedThreadPool(), threadNum);
		InetSocketAddress address = new InetSocketAddress(host, port);
		bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("remoteAddress", address);
		bootstrap.setOption("connectTimeoutMillis", DEFAULT_TIME_OUT);
	}

	public void connect() {
		ClientConnecter checkHeardSignal = new ClientConnecter();
		checkHeardSignal.setDaemon(true);
		checkHeardSignal.setName((new StringBuilder()).append(getClientName())
				.append("-ClientConnecter").toString());
		checkHeardSignal.start();
	}

	public boolean connect4Await() {
		ChannelFuture future = bootstrap.connect();
		future.awaitUninterruptibly();
		if (future.isSuccess()) {
			channel = future.getChannel();
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("Connect channel to the server: "+ChannelUtil.getRemoteAddrssFromChannel(channel).toString());
			isConnected = true;
			return true;
		} else {
			return false;
		}
	}

	public void write(Object obj) {
		if (obj == null)
			throw new RuntimeException("The object is null");
		if (!(obj instanceof byte[])) {
			throw new RuntimeException("The object should only be byte[]");
		} else {
			channel.write(obj);
			return;
		}
	}

	public void close() {
		ChannelFuture cf = channel.close();
		try {
			boolean res = cf.await(2000L);
			if (!res)
				LogFactory.getLogger(LogFactory.SYS_WARN_LOG).warn("Disconnect channel to the server: "+channel.getRemoteAddress().toString()+"Failed!");
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_WARN_LOG).warn("Disconnect channel to the server:"+channel.getRemoteAddress()+"Failed!");
		}
		if (bootstrap.getPipelineFactory() instanceof ExternalResourceReleasable) {
			((ExternalResourceReleasable) bootstrap.getPipelineFactory()).releaseExternalResources();
		}
		bootstrap.releaseExternalResources();
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

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public BaseNioClientHandler getNioHandler() {
		return nioHandler;
	}

	public void setNioHandler(BaseNioClientHandler nioHandler) {
		this.nioHandler = nioHandler;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	private class ClientConnecter extends Thread {

		@Override
		public void run() {
			while (true) {
				if (!isConnected) {
					if (!connect()) {
						continue;
					}
				}
				try {
					Thread.sleep(DEFAULT_CHECK_DISCONNECT_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private boolean connect() {
			try {
				ChannelFuture future = bootstrap.connect();
				future.awaitUninterruptibly();
				if (!future.isSuccess()) {
					channel = future.getChannel();
					LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("Connect channel to the server: "+ChannelUtil.getRemoteAddrssFromChannel(channel).toString());
					isConnected = true;
					return true;
				} else {
					waitTime(null);
				}
			} catch (Exception e) {
				waitTime(e);
			}
			return false;
		}

		private void waitTime(Exception e) {
			if (e != null)
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error(getClientName()+"Fail to connect to the server, please 3 seconds.", e);
			else
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error(getClientName()+"Fail to connect to the server, please 3 seconds.", e);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException el) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error(getClientName()+" Thread Sleep error", el);
			}
		}

	}

}
