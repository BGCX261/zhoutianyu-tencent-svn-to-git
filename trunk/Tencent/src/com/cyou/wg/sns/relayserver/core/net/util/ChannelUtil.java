package com.cyou.wg.sns.relayserver.core.net.util;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import com.cyou.wg.sns.relayserver.core.util.ByteUtil;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

public class ChannelUtil
{

	public ChannelUtil()
	{
	}

	public static void writeMsg(Channel channel, BaseResponseProtocol msg) throws Exception
	{
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		buff.writeByte(1);
		buff.writeBytes(msg.encode());
		channel.write(ByteUtil.buff2Array(buff));
	}

	public static void writeMsg(List channelList, BaseResponseProtocol msg) throws Exception
	{
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		buff.writeByte(1);
		buff.writeBytes(msg.encode());
		byte src[] = ByteUtil.buff2Array(buff);
		Iterator channelI = channelList.iterator();
		while(channelI.hasNext()){
			Channel channel = (Channel)channelI.next();
			channel.write(src);
		}
	}

	public static void writeMsg(Channel channel, List msgList) throws Exception
	{
		if (msgList != null && msgList.size() > 0)
		{
			ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
			buff.writeByte(msgList.size());
			BaseResponseProtocol msg;
			Iterator msgI = msgList.iterator();
			while(msgI.hasNext()){
				msg = (BaseResponseProtocol)msgI.next();
				buff.writeBytes(msg.encode());
			}
			channel.write(ByteUtil.buff2Array(buff));
		}
	}

	public static String getRemoteIpFromChannel(Channel channel)
	{
		return getRemoteAddrssFromChannel(channel).getAddress().getHostAddress();
	}

	public static int getRemotePortFromChannel(Channel channel)
	{
		return getRemoteAddrssFromChannel(channel).getPort();
	}

	public static InetSocketAddress getRemoteAddrssFromChannel(Channel channel)
	{
		return (InetSocketAddress)channel.getRemoteAddress();
	}
}
