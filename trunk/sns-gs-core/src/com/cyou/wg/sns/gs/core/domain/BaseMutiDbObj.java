package com.cyou.wg.sns.gs.core.domain;

	
public abstract class BaseMutiDbObj extends BaseDbObject {
	protected short objIndex;

	public short getObjIndex() {
		return objIndex;
	}

	public void setObjIndex(short objIndex) {
		this.objIndex = objIndex;
	}
}
