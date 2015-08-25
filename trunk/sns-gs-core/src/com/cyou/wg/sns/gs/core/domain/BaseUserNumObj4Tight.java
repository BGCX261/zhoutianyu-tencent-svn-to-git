package com.cyou.wg.sns.gs.core.domain;


/**
 * 玩家有多个对象时的数据索引,索引紧密排列
 * @author Administrator
 *
 */
public abstract class BaseUserNumObj4Tight extends BaseInstanceObject{
	protected short[] objIndex;
	
	/**
	 * 在索引中增加新的对象
	 * @return 返回新对象的索引
	 */
	public short addObj2Index() {
		if(objIndex == null) {
			objIndex = new short[1];
			objIndex[0] = 1;
			return 1;
		}
		for(short i = 0; i < objIndex.length; i++) {
			if(objIndex[i] <= 0) {
				objIndex[i] =  (short) (i + 1);
				return objIndex[i];
			}
		}
		short[] t = new short[objIndex.length + 1];
		System.arraycopy(objIndex, 0, t, 0, objIndex.length);
		t[objIndex.length] = (short) t.length;
		objIndex = t;
		return t[objIndex.length - 1];
	}
	
	public void desObjNum(int index) {
		objIndex[index - 1] = 0;
		//整理索引，把数组尾部的清除
		int count = objIndex.length - 1;
		for(count = objIndex.length - 1; count >= 0; count--) {
			if(objIndex[count] > 0) {
				break;
			}
		}
		if(count < objIndex.length - 1) {
			if(count < 0) {
				objIndex = null;
			}else {
				short[] t = new short[count + 1];
				System.arraycopy(objIndex, 0, t, 0, count + 1);
				objIndex = t;
			}
		}
	}
	public short[] getObjIndex() {
		return objIndex;
	}
	public void setObjIndex(short[] objIndex) {
		this.objIndex = objIndex;
	}
	
	public short getObjNum() {
		short num = 0;
		if(objIndex != null) {
			for(short s : objIndex) {
				if(s > 0) {
					num++;
				}
			}
		}
		return num;
	}


}
