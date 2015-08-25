package com.cyou.wg.sns.gs.core.event;


public abstract class BaseEvent{
	protected BaseEventContent e;
	protected boolean isNeedUnSyn;//是否需要异步执行
	
	public BaseEvent(BaseEventContent e) {
		this.e = e;
		this.isNeedUnSyn = false;
	}
	
	public BaseEvent(BaseEventContent e, boolean isNeedUnSyn) {
		this.e = e;
		this.isNeedUnSyn = isNeedUnSyn;
	}
	public abstract void synExecute();
	
	public abstract void unsynExecute();
	public boolean isNeedUnSyn() {
		return isNeedUnSyn;
	}
	public Exception getException() {
		return e.getException();
	}
	
	public void setException() {
		e.setException(new RuntimeException());
	}
}
