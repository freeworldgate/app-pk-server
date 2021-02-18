package com.union.app.plateform.exception.handler;


import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.exception.ApiException;
import com.union.app.plateform.log4j2.AppLoggerFactory;
import com.union.app.plateform.response.ApiResponse;
import com.union.app.plateform.data.resultcode.ResultCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = AppLoggerFactory.getInterfaceLogger();

    @ExceptionHandler(value = { ApiException.class })
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse constraintViolationException(ApiException exception)
    {
        return ApiResponse.buildResponse(exception.getResultCode(),exception.getResultMsg(),exception);
    }


    @ExceptionHandler(value = { AppException.class })
    @ResponseStatus(HttpStatus.OK)
    public AppResponse constraintViolationException(AppException exception)
    {
        return exception.getAppResponse();
    }


    /**
     * 所有异常
     * @param exception
     * @return
     */
    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse constraintViolationException(Exception exception) {

        logger.error("--------------------------------------------------------------------------");
        logger.error(exception.getMessage());
        logger.error("--------------------------------------------------------------------------");

        return ApiResponse.buildResponse(ResultCode.E99999999,null);
    }




}