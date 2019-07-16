package madrid.apiFactory.core.exception;

import bingocloud.insight.sdk.core.DataResponse;
import bingocloud.insight.sdk.core.ICommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class SysExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(SysExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ICommandResponse jsonErrorHandler(HttpServletRequest request, Exception exception) {
        log.error(exception.getMessage(), exception);
        return new DataResponse.Builder<String>().setException(exception.getMessage()).build();
    }
}
