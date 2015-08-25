package com.cyou.wg.sns.relayserver.core.protocol.codec;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseResponseProtocol;
import com.cyou.wg.sns.relayserver.core.util.ByteUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class ByteArrayEncoder extends OneToOneEncoder
{

	private int maxArrayLength;
	private static final int headLength = 15;

	public ByteArrayEncoder()
	{
		maxArrayLength = 0x100000;
	}

	public int getMaxArrayLength()
	{
		return maxArrayLength;
	}

	public void setMaxArrayLength(int maxArrayLength)
	{
		if (maxArrayLength <= 0)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("maxArrayLength: ").append(maxArrayLength).toString());
		} else
		{
			this.maxArrayLength = maxArrayLength;
			return;
		}
	}

	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg)
		throws Exception
	{
		if (!(msg instanceof BaseResponseProtocol))
			throw new ClassCastException((new StringBuilder()).append("message is :").append(msg.getClass().getName()).append(" expect : BaseResponseProtocol").toString());
		BaseResponseProtocol res = (BaseResponseProtocol)msg;
		byte obj[] = res.encode();
		if (obj.length > maxArrayLength)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("The encoded object is too big: ").append(obj.length).append(" (> ").append(maxArrayLength).append(')').toString());
		} else
		{
			ChannelBuffer buf = ChannelBuffers.buffer(obj.length + headLength);
			buf.writeShort(ByteUtil.shortL2H((short)-21931));
			buf.writeByte(0);
			buf.writeShort(ByteUtil.shortL2H(res.getProtocolId()));
			buf.writeShort(ByteUtil.shortL2H((short)obj.length));
			buf.writeLong(0x21e88eL);
			buf.writeBytes(obj);
			return buf;
		}
	}
}
