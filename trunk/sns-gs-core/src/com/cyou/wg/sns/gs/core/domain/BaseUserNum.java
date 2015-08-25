package com.cyou.wg.sns.gs.core.domain;

public abstract class BaseUserNum  extends BaseInstanceObject{
	protected short[] objIndex;
	
	
	
	public short[] getObjIndex() {
		return objIndex;
	}

	public void setObjIndex(short[] objIndex) {
		this.objIndex = objIndex;
	}

	public boolean addIndex(short num) {
		if(objIndex == null) {
			objIndex = new short[1];
			objIndex[0] = num;
			return true;
		}
		for(short s : objIndex) {
			if(s == num) {
				return false;
			}
		}
		short[] t = new short[objIndex.length + 1];
		System.arraycopy(objIndex, 0, t, 0, objIndex.length);
		t[objIndex.length] = num;
		objIndex = t;
		return true;
	}
	
	public boolean removeIndex(short num) {
		if(objIndex == null) {
			return false;
		}
		int s = -1;
		for(int i = 0; i < objIndex.length; i++) {
			if(objIndex[i] == num) {
				s = i;
				break;
			}
		}
		if(s < 0) {
			return false;
		}
		if(objIndex.length == 1) {
			objIndex = null;
		}else {
			short[] t = new short[objIndex.length - 1];
			if(s == 0) {
				System.arraycopy(objIndex, 1, t, 0, t.length);
			}else if(s == objIndex.length - 1) {
				System.arraycopy(objIndex, 0, t, 0, t.length);
			}else {
				System.arraycopy(objIndex, 0, t, 0, s);
				System.arraycopy(objIndex, s + 1, t, s, t.length - s);
			}
			objIndex = t;
		}
		return true;
	}
}
