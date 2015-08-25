package com.cyou.wg.sns.ctsvr.core.net.server;

import com.cyou.wg.sns.ctsvr.core.log.LogFactory;
import com.cyou.wg.sns.ctsvr.core.protocol.codec.ByteArrayProtocolFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class NioServer {

	private int id;
	private String type;
	private NioSocketAcceptor acceptor;
	private int threadNum;
	private Class<?> clazz;
	private int port;

	public void setId(int id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public NioServer(int port, int threadNum, String handlerName)
			throws ClassNotFoundException {
		this.port = port;
		this.threadNum = threadNum > 0 ? threadNum : Runtime.getRuntime()
				.availableProcessors();
		clazz = Class.forName(handlerName);
	}

	public void startSvr() throws InstantiationException,
			IllegalAccessException, IOException {
		acceptor = new NioSocketAcceptor(threadNum);
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(
				new ByteArrayProtocolFactory()));
		acceptor.setHandler((IoHandler) clazz.newInstance());
		acceptor.setReuseAddress(true);
		acceptor.bind(new InetSocketAddress(port));
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(
				(new StringBuilder()).append("Successfully start server!")
						.append(type).append(":").append(id).toString());
		Runtime.getRuntime().addShutdownHook(new ServerShutdown(this));
	}

	public void stop() {
		acceptor.unbind();
		LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("unbind all port");
	}
}
