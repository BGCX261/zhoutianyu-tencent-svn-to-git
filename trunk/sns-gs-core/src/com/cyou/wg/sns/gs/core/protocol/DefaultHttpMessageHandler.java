package com.cyou.wg.sns.gs.core.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.buffer.IoBuffer;

import com.cyou.wg.sns.gs.core.cache.ThreadLocalCache;
import com.cyou.wg.sns.gs.core.dataSource.SqlMapClientMgr;
import com.cyou.wg.sns.gs.core.domain.UserMessageCache;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.protocol.base.response.FailResponse;
import com.cyou.wg.sns.gs.core.session.AppContext;
import com.cyou.wg.sns.gs.core.session.UserSession;
import com.cyou.wg.sns.gs.core.util.ByteUtil;

public class DefaultHttpMessageHandler implements HttpMessageHandler{
	
	protected FailResponse failResponse;
	
	protected BaseProtocolConfig protocolConfig;
	
	public void setFailResponse(FailResponse failResponse) {
		this.failResponse = failResponse;
	}
	
	

	public void setProtocolConfig(BaseProtocolConfig protocolConfig) {
		this.protocolConfig = protocolConfig;
	}

	protected int getMaxProtocolResponseLength() {
		return 3072;
	}

	public  static int MAX_PROTOCOL_IN_NUM = 4096;
	/**
	 * 处理请求头的协议
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected RequestProtocol[] decode(InputStream in) throws IOException, InstantiationException, IllegalAccessException {
		IoBuffer buffer = IoBuffer.wrap(readByte(in));
		int num = buffer.get();
		RequestProtocol[] res = new RequestProtocol[num];
		for(int i = 0; i < num; i++) {
			RequestProtocol bp = protocolConfig.getProtocolByName(buffer.getShort());
			bp.decode(buffer);
			res[i] = bp;
		}
		return res;
	}
	/**
	 * 读取输入流中的所有信息
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected byte[] readByte(InputStream in) throws IOException {
		byte[] buff = new byte[MAX_PROTOCOL_IN_NUM];
		int readLength = 0;
		int count = 0;
		while(readLength < 2) {
			readLength = readLength + in.read(buff, readLength, MAX_PROTOCOL_IN_NUM);
			if(count++ > 10) {
				throw new CyouSysException("错误的输入流，没有输入参数");
			}
		}
		while(in.available() > 0) {
			readLength = readLength + in.read(buff, readLength, MAX_PROTOCOL_IN_NUM - readLength);
			if(readLength > MAX_PROTOCOL_IN_NUM) {
				throw new CyouSysException("协议包传入过长。");
			}
		}
		return buff;
	}
	
	
	/**
	 * 处理返回协议
	 * @return
	 * @throws Exception 
	 */
	protected byte[] encodeResponse() throws Exception {
		List<ResponseProtocol> list = ThreadLocalCache.responseList.get();
		UserSession us = ((UserSession)AppContext.getAppContext().getHttpSession().getAttribute(UserSession.USER_SESSION_KEY));
		if(us != null) {
			List<ResponseProtocol> list2 = UserMessageCache.getInstance().getMessage(us.getUserId());
			if(list2 != null && list2.size() > 0) {
				list.addAll(list2);
			}
		}
		IoBuffer res = IoBuffer.allocate(128);
		res.setAutoExpand(true);
		res.put((byte)list.size());
		byte[] bb = null;
		for(int i = 0; i < list.size(); i++) {
			bb = list.get(i).encode();
			res.putShort((short)bb.length);
			res.put(bb);
		}
		return ByteUtil.buff2Array(res);
	}
	/**
	 * 处理请求
	 * @param request
	 * @return
	 */
	protected ResponseProtocol execute(RequestProtocol request) {
		return request.execute();
	}
	/**
	 * 处理请求
	 */
	@Override
	public Object handler(HttpServletRequest request) {
		try {
			RequestProtocol[] rp = decode(request.getInputStream());//解码
			ResponseProtocol res = null;
			boolean isbreak = false;//是否继续执行
			for(int i = 0; i < rp.length; i++) {
				ThreadLocalCache.currReq.set(rp[i]);
				try {
					if(LogFactory.getLogger(LogFactory.SYS_DEBUG_LOG).isDebugEnabled()) {
						LogFactory.getLogger(LogFactory.SYS_DEBUG_LOG).debug("start execute request:" + rp[i].getProtocolId());
					}
					res = this.execute(rp[i]);//执行
					if(LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).isDebugEnabled()) {
						LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).debug("end execute request:" + rp[i].getProtocolId());
					}
				}catch (Exception e) {
					LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("handler", e);
					if(LogFactory.getLogger(LogFactory.SYS_DEBUG_LOG).isDebugEnabled()) {
						LogFactory.getLogger(LogFactory.SYS_DEBUG_LOG).debug("execute request error protocolId:" + rp[i].getProtocolId());
					}
					res = failResponse.createProtocol(e.getMessage(),rp[i].getProtocolId());
					isbreak = true;
				}
				if(res != null) {
					ThreadLocalCache.responseList.get().add(res);//把结果放入缓存
				}
				ThreadLocalCache.currReq.remove();
				if(isbreak) {
					break;
				}
			}
		} catch (Exception e) {
			LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("handler", e);
			ResponseProtocol res = failResponse.createProtocol(e.getMessage(), BaseProtocol.DEFAULT_SYS_ERROR_ID);
			ThreadLocalCache.responseList.get().add(res);//把结果放入缓存
		}finally {
			ThreadLocalCache.currReq.remove();
			ThreadLocalCache.data.remove();
		}
		return null;
	}
	/**
	 * 处理请求后
	 */
	@Override
	public Object afterHandler(HttpServletResponse response, Object obj) {
		try {
			byte[] tt = encodeResponse();
			if(tt.length > getMaxProtocolResponseLength()) {
				int currIndex = 0;
				while(currIndex < tt.length) {
					response.getOutputStream().write(tt, currIndex, Math.min(tt.length - currIndex, getMaxProtocolResponseLength()));
					currIndex = currIndex + Math.min(tt.length - currIndex, getMaxProtocolResponseLength());
				}
			}else {
				response.getOutputStream().write(tt);
			}
			response.getOutputStream().flush();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		finally {
			ThreadLocalCache.responseList.remove();
			try {
				SqlMapClientMgr.closeCurr();//关闭打开的数据库连接
				if(response.getOutputStream() != null) {
					response.getOutputStream().close();//关闭输出流
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Object beforeHandler(HttpServletRequest request,
			HttpServletResponse response) {
		AppContext.init(request, response);
		ThreadLocalCache.responseList.set(new ArrayList<ResponseProtocol>());//初始化线程返回值缓存
		return null;
	}

}
