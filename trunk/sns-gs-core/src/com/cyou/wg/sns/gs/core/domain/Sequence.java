package com.cyou.wg.sns.gs.core.domain;

import org.apache.mina.core.buffer.IoBuffer;

public class Sequence extends BaseObject{
	
	private String seqType;//序列器类型
	private int count;//序列器的值
	
	public Sequence() {
		
	}
	
	public Sequence(String seqType, int count) {
		this.seqType = seqType;
		this.count = count;
	}

	public String getSeqType() {
		return seqType;
	}


	public void setSeqType(String seqType) {
		this.seqType = seqType;
	}


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String getBaseObjectKey() {
		// TODO Auto-generated method stub
		return "Sequence_" + seqType;
	}

	@Override
	public short getBaseObjectType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getStorageType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BaseObject decode(IoBuffer src) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] encode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
