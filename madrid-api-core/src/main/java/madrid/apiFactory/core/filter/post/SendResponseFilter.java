package madrid.apiFactory.core.filter.post;


import madrid.apiFactory.core.filter.EatuulFilter;
import madrid.apiFactory.core.http.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


public class SendResponseFilter implements EatuulFilter {
    private final Logger logger = LoggerFactory.getLogger(SendResponseFilter.class);
    @Override
    public void run()  {
        addResponseHeaders();
        try {
            writeResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public String filterType() {
        return "post";
    }

    private void addResponseHeaders() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        ResponseEntity responseEntity = ctx.getResponseEntity();
        if(responseEntity != null) {
        	HttpHeaders httpHeaders = responseEntity.getHeaders();
            for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
                String headerName = entry.getKey();
                List<String> headValues = entry.getValue();
                for (String headValue : headValues) {
                    logger.debug(headerName, headValue);
                    response.addHeader(headerName, headValue);
                }
            }
        }
    }

    private void writeResponse() throws Exception {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        ResponseEntity responseEntity = ctx.getResponseEntity();
        if (responseEntity != null && responseEntity.hasBody()) {
        	OutputStream os = new BufferedOutputStream(response.getOutputStream());
        	try {
            	Resource resource = (Resource) responseEntity.getBody();
            	InputStream is = resource.getInputStream();
            	byte[] buffer = new byte[1024];
    			int len;
    			while ((len =is.read(buffer)) > -1) {
    				os.write(buffer,0,len);
    				os.flush();
    			}
            } finally {
                os.close();
            }
        }
    }
}
