package com.cyou.wg.sns.gs.core.event;

public class ExecuteEvent implements Runnable{
	private BaseEvent baseEvent;
	public ExecuteEvent(BaseEvent baseEvent) {
		this.baseEvent = baseEvent;
	}

	@Override
	public void run() {
		try {
			baseEvent.unsynExecute();
		}catch (Exception e) {
			e.printStackTrace();
			baseEvent.getException().printStackTrace();
		}
		
	}
}
