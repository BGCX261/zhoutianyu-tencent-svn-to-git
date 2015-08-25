package com.cyou.wg.sns.gs.core.exception;

public class CyouAppException extends RuntimeException{
	
	private static final long serialVersionUID = 3935791518547163398L;
	
	String message = null;
	
	public CyouAppException(String message) {
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
	
	public CyouAppException(int exceptionId, Object... objs) {
		String s = ExceptionId2Text.getExceptionStr(exceptionId);
		if(s == null) {
			throw new CyouAppException("不存在异常数据：" + exceptionId);
		}
		if(objs != null && objs.length > 0) {
			for(int i = 0; i < objs.length; i++) {
				s = s.replaceFirst("\\$" + (i + 1), String.valueOf(objs[i]));
			}
		}
		this.message = s;
	}
	
	public CyouAppException(String exceptionModel, Object... objs) {
		if(objs != null && objs.length > 0) {
			for(int i = 0; i < objs.length; i++) {
				exceptionModel = exceptionModel.replaceFirst("\\$" + i, String.valueOf(objs[i]));
			}
		}
		this.message = exceptionModel;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}
