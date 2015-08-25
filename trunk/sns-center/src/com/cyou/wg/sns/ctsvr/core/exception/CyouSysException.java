package com.cyou.wg.sns.ctsvr.core.exception;

public class CyouSysException extends RuntimeException{
	
	private static final long serialVersionUID = -5871201494126503990L;
	
	public CyouSysException(String message) {
		super(message);
	}
	public CyouSysException(String message, Throwable cause) {
		super(message, cause);
	}
	public String toString() {
		return super.getMessage();
	}

}
