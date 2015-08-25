package com.cyou.wg.sns.ctsvr.core.protocol.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class ByteArrayDecoder extends CumulativeProtocolDecoder {

	private int maxArrayLength = 1048576; // 1M

	public int getMaxArrayLength() {
		return maxArrayLength;
	}

	public void setMaxArrayLength(int maxArrayLength) {
		if (maxArrayLength <= 0) {
			throw new IllegalArgumentException((new StringBuilder())
					.append("maxArrayLength: ").append(maxArrayLength)
					.toString());
		} else {
			this.maxArrayLength = maxArrayLength;
			return;
		}
	}

	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (!in.prefixedDataAvailable(4, maxArrayLength)) {
			return false;
		} else {
			int length = in.getInt();
			byte i[] = new byte[length];
			in.get(i);
			out.write(i);
			return true;
		}
	}
}
