package com.cyou.wg.sns.gs.core.domain;

public abstract class BaseUserNum4Int extends BaseInstanceObject{
	
	protected int[] objIndex;
	
	
	public boolean addIndex(int num) {
		if(objIndex == null) {
			objIndex = new int[1];
			objIndex[0] = num;
			return true;
		}
		for(int s : objIndex) {
			if(s == num) {
				return false;
			}
		}
		int[] t = new int[objIndex.length + 1];
		System.arraycopy(objIndex, 0, t, 0, objIndex.length);
		t[objIndex.length] = num;
		objIndex = t;
		return true;
	}
	
	public boolean removeIndex(int num) {
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
			int[] t = new int[objIndex.length - 1];
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
