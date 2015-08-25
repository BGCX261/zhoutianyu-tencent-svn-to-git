package com.cyou.wg.sns.gs.core.domain;

public class UserHash {
	private int highStart;
	private int highEnd;
	private int lowStart;
	private int lowEnd;
	
	public int getHighStart() {
		return highStart;
	}

	public void setHighStart(int highStart) {
		this.highStart = highStart;
	}

	public int getHighEnd() {
		return highEnd;
	}

	public void setHighEnd(int highEnd) {
		this.highEnd = highEnd;
	}

	public int getLowStart() {
		return lowStart;
	}

	public void setLowStart(int lowStart) {
		this.lowStart = lowStart;
	}

	public int getLowEnd() {
		return lowEnd;
	}

	public void setLowEnd(int lowEnd) {
		this.lowEnd = lowEnd;
	}

	public boolean isHash(int userId) {
		int h = userId % 10000;
		int l = h % 100;
		h = (h - l) / 100;
		if(h <= highEnd && h >= highStart && l <= lowEnd && l >= lowStart) {
			return true;
		}
		return false;
	}
}
