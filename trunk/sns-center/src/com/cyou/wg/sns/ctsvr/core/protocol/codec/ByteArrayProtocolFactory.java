package com.cyou.wg.sns.ctsvr.core.protocol.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;

public class ByteArrayProtocolFactory implements ProtocolCodecFactory {

	private ByteArrayDecoder decoder = new ByteArrayDecoder();;
	private ByteArrayEncoder encoder = new ByteArrayEncoder();

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
}
