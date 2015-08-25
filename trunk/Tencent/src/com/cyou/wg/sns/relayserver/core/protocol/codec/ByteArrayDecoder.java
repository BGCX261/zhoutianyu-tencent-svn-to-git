package com.cyou.wg.sns.relayserver.core.protocol.codec;

import com.cyou.wg.sns.relayserver.core.protocol.base.BaseRequestProtocol;
import com.cyou.wg.sns.relayserver.core.util.ByteUtil;
import com.cyou.wg.sns.relayserver.socketSvr.conf.ProtocolConfig;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class ByteArrayDecoder extends FrameDecoder
{

	private static final int headLength = 15;
	private int maxArrayLength;

	public ByteArrayDecoder()
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

	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf)
		throws Exception
	{
		if (buf.readableBytes() < headLength)
			return null;
		buf.markReaderIndex();
		short m_nBegin = ByteUtil.readShort(buf);
		byte compressSign = buf.readByte();
		short msgID = ByteUtil.readShort(buf);
		short msgLength = ByteUtil.readShort(buf);
		for (int i = 0; i < 8; i++)
			buf.readByte();

		if (buf.readableBytes() < msgLength)
		{
			buf.resetReaderIndex();
			return null;
		} else
		{
			BaseRequestProtocol rp = ProtocolConfig.getInstance().getProtocolByName(Short.valueOf(msgID));
			rp.decode(buf);
			return rp;
		}
	}
}
