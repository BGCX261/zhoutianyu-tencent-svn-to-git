package com.cyou.wg.sns.gs.core.snsInterface;
/**
 * 向sns平台发送消息的接口
 * @author Administrator
 *
 */
public interface MessageInterface {
	/**
	 * 分享
	 * @param gamePara
	 */
	public void share(GamePara gamePara);
	/**
	 * 邀请
	 * @param gamePara
	 */
	public void invite(GamePara gamePara) ;
	/**
	 * 发送礼物
	 * @param gamePara
	 */
	public void sendGift(GamePara gamePara);
	/**
	 * 所要礼物
	 * @param gamePara
	 */
	public void call4Gift(GamePara gamePara);
	
}
