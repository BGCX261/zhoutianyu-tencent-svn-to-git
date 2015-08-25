package com.cyou.wg.sns.gs.core.event;

public class BaseEventContent {
	private Exception exception;
	
	private Object[] obj;
	
	public BaseEventContent(Object...objects) {
		obj = objects;
	}
	
	

	public Object[] getObj() {
		return obj;
	}



	public void setObj(Object[] obj) {
		this.obj = obj;
	}



	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	

}
