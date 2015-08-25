package com.cyou.wg.sns.ctsvr.app.log2File.messageHandler;

import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.util.ByteUtil;
import java.util.*;
import org.apache.mina.core.buffer.IoBuffer;

public class LogicalLogList extends BaseObject {

	private List<LogicalBaseLog> list = new ArrayList<LogicalBaseLog>();
	private List<String> errorList = new ArrayList<String>();

	public void add(LogicalBaseLog log)
	{
		list.add(log);
	}

	public String getBaseObjectKey()
	{
		return null;
	}

	public short getBaseObjectType()
	{
		return 0;
	}

	public byte getStorageType()
	{
		return 0;
	}

	public BaseObject decode(IoBuffer src) throws Exception {
		int length = ByteUtil.covertShort2Integer(src.getShort());
		for (int i = 0; i < length; i++) {
			list.add((LogicalBaseLog)(new LogicalBaseLog()).decode(src));
		}
		return this;
	}

	public byte[] encode() throws Exception {
		if (list.size() > ByteUtil.MAX_LENGTH) {
			throw new CyouSysException("��Ҫ���µ�db�����");
		}
		IoBuffer buff = IoBuffer.allocate(256);
		buff.setAutoExpand(true);
		buff.putShort((short)list.size());
		for(LogicalBaseLog c : list) {
			buff.put(c.encode());
		}
		return ByteUtil.buff2Array(buff);
	}

	public List<String> getLogs() {
		List<String> res = new ArrayList<String>(list.size());
		for(LogicalBaseLog log : list) {
			if (log.getJsonParams(log.getLogName())) {
				res.add(log.toString().toLowerCase());
			} else {
				errorList.add(log.toString().toLowerCase());
			}
		}
		return res;
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
}
