/**
 * This file created at 2011-11-2.
 *
 * Copyright (c) 2002-2011 Bingosoft, Inc. All rights reserved.
 */
package madrid.apiFactory.core.util.spring;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;


/**
 * <code>{@link DelegatingServletProxy}</code>
 *
 * 代理执行Spring容器中配置的Servlet
 *
 * @author zhongtao
 */
@SuppressWarnings("serial")
public class DelegatingServletProxy extends HttpServlet {
	private Servlet proxy;

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		proxy.destroy();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		return proxy.getServletConfig();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		return proxy.getServletInfo();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		String beanName = config.getInitParameter("targetBeanName");
		this.proxy = ApplicationFactory.getBean(beanName, Servlet.class);
//			ApplicationFactory.getBeanForName(Servlet.class, beanName);
		this.proxy.init(config);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		proxy.service(req, res);
	}
}