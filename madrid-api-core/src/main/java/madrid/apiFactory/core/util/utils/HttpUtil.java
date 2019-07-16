package madrid.apiFactory.core.util.utils;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {
	private static final String AJAX_REQUEST_HEADER = "x-requested-with"; //$NON-NLS-1$
	private static final String AJAX_REQUEST_VALUE = "XMLHttpRequest"; //$NON-NLS-1$
	
	public static String getRequestPath(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());

		if ("".equals(path)) {
			return "/";
		}

		return path;
	}

	public static String appendQueryParam(String url, String param, String value) {
		StringBuilder sb = new StringBuilder(url);
		if (url.contains("?")) {
			sb.append("&");
		} else {
			sb.append("?");
		}
		sb.append(param).append("=").append(value);
		return sb.toString();
	}
	
	public static boolean isAjax(HttpServletRequest request) {
		return AJAX_REQUEST_VALUE.equals(request.getHeader(AJAX_REQUEST_HEADER));
	}
	
}
