package com.cyou.wg.sns.gs.core.snsInterface;
/**
 * 处理sns平台请求的接口
 * @author Administrator
 *
 */
public interface InvokeMessage {
	/**
	 * 处理请求
	 * @param para
	 */
	public void invoke(GamePara para);
}
