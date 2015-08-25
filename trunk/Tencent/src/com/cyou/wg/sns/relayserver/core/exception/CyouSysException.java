package com.cyou.wg.sns.relayserver.core.exception;

/**
 * @Description 
 * @author 周天宇
 *
 */
public class CyouSysException extends RuntimeException
{

	private static final long serialVersionUID = 0xae854d5919d8d7caL;

	public CyouSysException(String message)
	{
		super(message);
	}

	public CyouSysException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public String toString()
	{
		return super.getMessage();
	}
}
