package com.cyou.wg.sns.gs.core.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class IdList4Cache<T> extends BaseInstanceObject {
	
	protected List<T> list = new ArrayList<T>();
	
	public void add(T e) {
		list.add(e);
	}
	
	public void remove(T e) {
		list.remove(e);
	}

}
