package com.cyou.wg.sns.ctsvr.core.protocol.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ByteArrayEncoder extends ProtocolEncoderAdapter {

	private int maxArrayLength = 1048576; // 1M

	public int getMaxArrayLength() {
		return maxArrayLength;
	}

	public void setMaxArrayLength(int maxArrayLength) {
		if (maxArrayLength <= 0) {
			throw new IllegalArgumentException((new StringBuilder())
					.append("maxArrayLength: ").append(maxArrayLength)
					.toString());
		}
		this.maxArrayLength = maxArrayLength;
		return;
	}

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		if (!(message instanceof byte[]))
			throw new ClassCastException((new StringBuilder())
					.append("message is :")
					.append(message.getClass().getName())
					.append(" expect : byte[]").toString());
		byte obj[] = (byte[]) message;
		if (obj.length > maxArrayLength) {
			throw new IllegalArgumentException((new StringBuilder())
					.append("The encoded object is too big: ")
					.append(obj.length).append(" (> ").append(maxArrayLength)
					.append(')').toString());
		} 
		IoBuffer buf = IoBuffer.allocate(obj.length + 4);
		buf.putInt(obj.length);
		buf.put(obj);
		buf.position(0);
		out.write(buf);
	}
}
