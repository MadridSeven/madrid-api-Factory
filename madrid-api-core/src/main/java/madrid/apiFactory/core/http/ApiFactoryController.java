package madrid.apiFactory.core.http;


import madrid.apiFactory.core.util.core.DataResponse;
import madrid.apiFactory.core.util.utils.SendMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ApiFactoryController {

    @Autowired
    private EatRunner eatRunner;

    private static final Logger logger = LoggerFactory.getLogger(ApiFactoryController.class);

    @RequestMapping(value = "/api/**", method = {RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.PUT, RequestMethod.TRACE}, produces = "application/json;charset=UTF-8")
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //将request,response放入上下文对象中
        eatRunner.init(req,resp);
        runApiFilter(eatRunner);
    }


    static void runApiFilter(EatRunner eatRunner) throws IOException {
        try{
            //执行前置过滤
            eatRunner.preRoute();
            //执行过滤
            eatRunner.route();
            //执行后置过滤
            eatRunner.postRoute();
        }catch (HttpMessageNotReadableException e){
            logger.error("请求发送失败，请求返回类型不是json对象类型");
            e.printStackTrace();
            SendMsgUtil.sendJsonMessage(RequestContext.getCurrentContext().getResponse(),
                    new DataResponse.Builder<Integer>().setException(e.getMessage())
                            .setResultValue(0).build());
        }catch (HttpClientErrorException e){
            logger.error("请求发送失败");
            SendMsgUtil.sendJsonMessage(RequestContext.getCurrentContext().getResponse(),
                    new DataResponse.Builder<Integer>().setException(e.getMessage())
                            .setResultValue(0).build());
        }catch (ResourceAccessException e){
            logger.error("请求超时");
            SendMsgUtil.sendJsonMessage(RequestContext.getCurrentContext().getResponse(),
                    new DataResponse.Builder<Integer>().setException(e.getMessage())
                            .setResultValue(0).build());
        }catch (Throwable e){
        	e.printStackTrace();
            SendMsgUtil.sendJsonMessage(RequestContext.getCurrentContext().getResponse(),
                    new DataResponse.Builder<Integer>().setException(e.getMessage())
                            .setResultValue(0).build());
        }finally {
            //清除变量
            RequestContext.getCurrentContext().unset();
        }
    }

}
