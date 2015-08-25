package com.cyou.wg.sns.ctsvr.app.dbSyn.messageHandler;

import com.cyou.wg.sns.gs.core.domain.BaseObject;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.util.ByteUtil;
import com.cyou.wg.sns.gs.core.util.DateUtil;
import java.util.*;
import org.apache.mina.core.buffer.IoBuffer;

public class DbCommandList extends BaseObject {

	private List<DbCommand> list = new ArrayList<DbCommand>();
	private long sendTime;

	public List<DbCommand> getList() {
		return list;
	}

	public DbCommandList() {
		sendTime = DateUtil.currentTimeMillis();
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public void setList(List<DbCommand> list) {
		this.list = list;
	}

	public String getBaseObjectKey() {
		return null;
	}

	public short getBaseObjectType() {
		return 0;
	}

	public byte getStorageType() {
		return 0;
	}

	public BaseObject decode(IoBuffer src) throws Exception {
		int length = ByteUtil.covertShort2Integer(src.getShort());
		for (int i = 0; i < length; i++) {
			list.add((DbCommand)(new DbCommand()).decode(src));
		}
		sendTime = src.getLong();
		return this;
	}

	public byte[] encode() throws Exception {
		if (list.size() > ByteUtil.MAX_LENGTH) {
			throw new CyouSysException("需要更新的db命令过长");
		}
		IoBuffer buff = IoBuffer.allocate(256);
		buff.setAutoExpand(true);
		buff.putShort((short)list.size());
		for(DbCommand c : list){
			buff.put(c.encode());
		}
		buff.putLong(sendTime);
		return ByteUtil.buff2Array(buff);
	}

	public void print() {
		for(DbCommand d : list) {
			LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info("发送数据库更新命令：" + d.toString());
		}
	}
}
