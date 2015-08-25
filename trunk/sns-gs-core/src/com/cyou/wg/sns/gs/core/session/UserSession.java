package com.cyou.wg.sns.gs.core.session;

public class UserSession {
	
	public final static String USER_SESSION_KEY = "UserSession";
	private int userId;
	
	private String accId;
	
	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	

}
