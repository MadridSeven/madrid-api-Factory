package madrid.apiFactory.core.filter.pre;


import madrid.apiFactory.core.filter.EatuulFilter;
import madrid.apiFactory.core.http.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class RequestWrapperFilter implements EatuulFilter {

    private final Logger logger = LoggerFactory.getLogger(RequestWrapperFilter.class);

    @Override
    public void run() throws IOException {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String restfulUrl = request.getRequestURI();
        String apiUrlArr[] = restfulUrl.split("/api")[1].split("/");
        String apiKey = apiUrlArr[1];
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/config/apiFactory.properties");
        Properties p = new Properties();
        p.load(inputStream);
        String rootURL = p.getProperty(apiKey);
        String apiUrl = "";
        for (int i = 2; i < apiUrlArr.length; i++) {
            apiUrl += "/" + apiUrlArr[i];
        }
        String targetURL = rootURL + apiUrl;
        Map<String, String[]> params = request.getParameterMap();
        if (params.size()>0){
            targetURL += "?";
        }
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String key =param.getKey();
            String value =  StringUtils.join(param.getValue());
            targetURL += key+"="+ URLEncoder.encode(value, "UTF-8") + "&";
        }
        if (params.size()>0){
            targetURL = targetURL.substring(0,targetURL.length()-1);
        }
        if (targetURL.equals("null") || null == rootURL) {
            logger.error("当前请求的目标地址不符合规则");
            return;
        }else {
            logger.info("当前请求的目标地址为：" + targetURL);
        }
        RequestEntity<byte[]> requestEntity = null;
        try {
            requestEntity = createRequestEntity(request, targetURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将请求体requestEntity放置全局对象当中
        ctx.setRequestEntity(requestEntity);
    }

    private RequestEntity createRequestEntity(HttpServletRequest request, String url) throws IOException, URISyntaxException {
        String method = request.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(method);
        //1.封装请求头
        MultiValueMap<String, String> headers = createRequestHeaders(request);
        //2.封装请求体
        byte[] body = createRequestBody(request);
        //3.构造出RestTemplate能识别的RequestEntity
        RequestEntity requestEntity = new RequestEntity<byte[]>(body, headers, httpMethod, new URI(url));
        return requestEntity;
    }


    public MultiValueMap<String, String> createRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headreValue : headerValues) {
                headers.add(headerName, headreValue);
            }
        }
        return headers;
    }

    public byte[] createRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public String filterType() {
        return "pre";
    }
}
