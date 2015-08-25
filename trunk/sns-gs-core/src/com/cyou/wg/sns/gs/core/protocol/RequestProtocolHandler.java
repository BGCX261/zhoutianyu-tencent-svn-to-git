package com.cyou.wg.sns.gs.core.protocol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cyou.wg.sns.gs.core.session.AppContext;
import com.cyou.wg.sns.gs.core.session.UserSession;

/**
 * 执行协议的统一接口
 * @author Administrator
 *
 */
public abstract class RequestProtocolHandler {
	protected AppContext getAppContext() {
		return AppContext.getAppContext();
	}
	
	protected HttpServletRequest getHttpServletRequest() {
		return getAppContext().getHttpServletRequest();
	}
	
	protected HttpSession getHttpSession() {
		return getAppContext().getHttpSession();
	}
	
	protected UserSession getUserSession() {
		return (UserSession)getHttpSession().getAttribute(UserSession.USER_SESSION_KEY);
	}
	
	protected int getUserIdFromSession() {
		return this.getUserSession().getUserId();
	}
	public abstract ResponseProtocol execute(RequestProtocol req);
}
