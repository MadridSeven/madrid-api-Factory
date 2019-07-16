package madrid.apiFactory.core.filter.route;


import madrid.apiFactory.core.filter.EatuulFilter;
import madrid.apiFactory.core.http.RequestContext;
import madrid.apiFactory.core.http.common.ApiMappingJackson2HttpMessageConverter;
import madrid.apiFactory.core.interceptor.HeadersInterceptor;
import madrid.apiFactory.core.util.core.DataResponse;
import madrid.apiFactory.core.util.spring.Configuration;
import madrid.apiFactory.core.util.utils.SendMsgUtil;
import madrid.apiFactory.core.util.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class RoutingFilter implements EatuulFilter {

    private final Logger logger = LoggerFactory.getLogger(RoutingFilter.class);

    private static int CON_TIME_OUT = 5 * 1000;
    private static int READ_TIME_OUT = 5 * 1000;

    static {
        String connectTimeout = Configuration.getProperty("CON_TIME_OUT");
        String readTimeout = Configuration.getProperty("READ_TIME_OUT");
        if(StringUtils.isNotEmpty(connectTimeout)) {
            CON_TIME_OUT = Integer.parseInt(connectTimeout);
        }
        if(StringUtils.isNotEmpty(readTimeout)) {
            READ_TIME_OUT = Integer.parseInt(readTimeout);
        }
    }

    @Override
    public void run() throws IOException {
        RequestContext ctx = RequestContext.getCurrentContext();
        RequestEntity requestEntity = ctx.getRequestEntity();
        if(isUploadFile(requestEntity)) {
        	//上传文件
        	sendUploadFile(ctx, requestEntity);
        } else {
        	//普通请求
        	send(ctx, requestEntity);
        }
    }
    
    /**
     * 判断是不是文件上传的请求
     * @param requestEntity
     * @return
     */
    private boolean isUploadFile(RequestEntity requestEntity) {
		if(!"post".equals(requestEntity.getMethod().name().toLowerCase())) {
			return false;
		}
		if(requestEntity.getHeaders().getContentType() != null 
				&& requestEntity.getHeaders().getContentType().isCompatibleWith(MediaType.MULTIPART_FORM_DATA)) {
			return true;
		}else {
			return false;
		}
    }
    
    //非上传文件请求
    private void send(RequestContext ctx, RequestEntity requestEntity) throws IOException {
    	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientRestfulHttpRequestFactory();
        requestFactory.setConnectTimeout(CON_TIME_OUT);
        requestFactory.setReadTimeout(READ_TIME_OUT);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new ApiMappingJackson2HttpMessageConverter());
        try{
            ResponseEntity<Resource> responseEntity = restTemplate.exchange(requestEntity, Resource.class);
            ctx.setResponseEntity(responseEntity);
            logger.info(requestEntity.getUrl().getPath() + "请求发送成功");
        }catch (ResourceAccessException e){
            logger.error("请求超时");
            SendMsgUtil.sendJsonMessage(RequestContext.getCurrentContext().getResponse(),
                    new DataResponse.Builder<Integer>().setException(e.getMessage())
                            .setResultValue(0).build());
        }
    }
    
    //上传文件请求
    private void sendUploadFile(RequestContext ctx, RequestEntity requestEntity) throws IOException {
    	logger.info("文件上传");
    	//文件上传
    	try {
        	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setConnectTimeout(CON_TIME_OUT);
            requestFactory.setReadTimeout(READ_TIME_OUT);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            restTemplate.getInterceptors().add(new HeadersInterceptor());
    		HttpServletRequest request = ctx.getRequest();
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
    		//获取上传的MultipartFile文件
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			for (String key : fileMap.keySet()) {
				MultipartFile multipartFile = fileMap.get(key);
				//将multipartFile文件写入file中
				FileUtils.writeByteArrayToFile(new File(multipartFile.getOriginalFilename()),multipartFile.getBytes() );
				FileSystemResource resource = new FileSystemResource(new File(multipartFile.getOriginalFilename()));
				param.add(key, resource);
		    }
			//发送请求
    		ResponseEntity responseEntity = restTemplate.postForEntity(requestEntity.getUrl(), param, Resource.class);
    		ctx.setResponseEntity(responseEntity);
            logger.info("文件上传成功");
    	}catch(Exception e) {
    		logger.error("文件上传失败");
    		e.printStackTrace();
            SendMsgUtil.sendJsonMessage(RequestContext.getCurrentContext().getResponse(),
                    new DataResponse.Builder<Integer>().setException(e.getMessage())
                            .setResultValue(0).build());
    	}
    }

    private static final class HttpComponentsClientRestfulHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
        @Override
        protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
            if (httpMethod == HttpMethod.GET) {
                return new HttpGetRequestWithEntity(uri);
            }
            return super.createHttpUriRequest(httpMethod, uri);
        }
    }

    private static final class HttpGetRequestWithEntity extends HttpEntityEnclosingRequestBase {
        public HttpGetRequestWithEntity(final URI uri) {
            super.setURI(uri);
        }

        @Override
        public String getMethod() {
            return HttpMethod.GET.name();
        }
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public String filterType() {
        return "route";
    }
}
