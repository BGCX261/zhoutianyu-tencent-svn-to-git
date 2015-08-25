package test.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qq.open.OpenApiV3;

public class PlayGameAction implements Servlet {

	@Override
	public void destroy() {
		
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest)arg0;
		HttpServletResponse res = (HttpServletResponse)arg1;
		Enumeration<String> test = req.getParameterNames();
		while(test.hasMoreElements()){
			System.out.println(test.nextElement());
		}
		String openkey = req.getParameter("openkey");
		String pf = req.getParameter("pf");
		String openid = req.getParameter("openid");
		String pfkey = req.getParameter("pfkey");
		System.out.println(openkey);
		System.out.println(pf);
		System.out.println(openid);
		System.out.println(pfkey);
		
		OpenApiV3 sdk = new OpenApiV3("100683354", "d4b4c8da208418cdb450ba5ec6d7274b");
        sdk.setServerName("119.147.19.43");
		TestOpenApiV3.testGetUserInfo(sdk, openid, openkey, pf);
		/**
		 * 
		 * {
	"ret": 0,
    "is_lost": 0,
    "nickname": "周天宇",
    "gender": "男",
    "country": "中国",
    "province": "北京",
    "city": "昌平",
    "figureurl": "http:\/\/pyapp.qlogo.cn\/campus\/1be89d3ddf5e816f8e6f8bd80e19cf337f7d3559b2ce50464b4336d5960dd34812cfda75d8ad736a\/60",
    "is_yellow_vip": 0,
    "is_yellow_year_vip": 0,
    "yellow_vip_level": 0,
    "is_yellow_high_vip": 0
}
		 */
	}

}
