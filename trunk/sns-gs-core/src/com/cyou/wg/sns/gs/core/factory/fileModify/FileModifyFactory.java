package com.cyou.wg.sns.gs.core.factory.fileModify;

public class FileModifyFactory {
	
	private static ModifyExecutor executor;
	
	
	
	public static void setExecutor(ModifyExecutor executor) {
		FileModifyFactory.executor = executor;
	}



	public static ModifyExecutor createExecutor() {
		return executor;
	}
}
