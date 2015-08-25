package com.cyou.wg.sns.relayserver.core.net.server;

import com.cyou.wg.sns.relayserver.core.factory.log.LogFactory.LogFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.ExternalResourceReleasable;

public class NioServer
{

	public static final ChannelGroup allChannels = new DefaultChannelGroup("server");
	private int id;
	private int port;
	private int threadNum;
	private String serverType;
	private BaseNioServerHandler nioServerHandler;
	private ServerBootstrap bootstrap;

	public NioServer()
	{
	}

	public NioServer(int id, int port)
	{
		this(id, port, 0, new BaseNioServerHandler());
	}

	public NioServer(int id, int port, int threadNum, BaseNioServerHandler handler)
	{
		this.id = id;
		this.port = port;
		this.threadNum = threadNum;
		nioServerHandler = handler;
	}

	public NioServer(int id, int port, int threadNum, String handlerName)
		throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		this.id = id;
		this.port = port;
		this.threadNum = threadNum;
		nioServerHandler = (BaseNioServerHandler)Class.forName(handlerName).newInstance();
	}

	public void init() throws Exception
	{
		if (nioServerHandler == null)
		{
			throw new RuntimeException("Nio server handler can't be null");
		} else
		{
			BaseServerChannelPipelineFactory pipelineFactory = new BaseServerChannelPipelineFactory(nioServerHandler);
			init(pipelineFactory);
			return;
		}
	}

	public void init(ChannelPipelineFactory pipelineFactory) throws Exception
	{
		if (pipelineFactory == null)
			throw new RuntimeException("Nio server pipelineFactory can't be null");
		org.jboss.netty.channel.ChannelFactory factory = null;
		if (threadNum > 0)
			factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), threadNum);
		else
			factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("child.tcpNoDelay", Boolean.valueOf(true));
		bootstrap.setOption("child.keepAlive", Boolean.valueOf(true));
		bootstrap.setOption("reuseAddress", Boolean.valueOf(true));
	}

	public void startSvr()
	{
		bootstrap.bind(new InetSocketAddress(port));
		Runtime.getRuntime().addShutdownHook(new ServerShutdown(this));
		LogFactory.getLogger("sys_info").info((new StringBuilder()).append(" Server start successfully ").append(serverType).append(" : ").append(id).append(" ").append(port).toString());
	}

	public void stop()
	{
		ChannelGroupFuture future = allChannels.close();
		future.awaitUninterruptibly();
		if (bootstrap.getPipelineFactory() instanceof ExternalResourceReleasable)
			((ExternalResourceReleasable)bootstrap.getPipelineFactory()).releaseExternalResources();
		bootstrap.releaseExternalResources();
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getServerType()
	{
		return serverType;
	}

	public void setServerType(String serverType)
	{
		this.serverType = serverType;
	}

	public BaseNioServerHandler getNioServerHandler()
	{
		return nioServerHandler;
	}

	public void setNioServerHandler(BaseNioServerHandler nioServerHandler)
	{
		this.nioServerHandler = nioServerHandler;
	}

	public int getThreadNum()
	{
		return threadNum;
	}

	public void setThreadNum(int threadNum)
	{
		this.threadNum = threadNum;
	}

	public static void main(String args[])
		throws Exception
	{
		NioServer nioServer = new NioServer(1, 18888);
		nioServer.init();
		nioServer.startSvr();
	}

}
