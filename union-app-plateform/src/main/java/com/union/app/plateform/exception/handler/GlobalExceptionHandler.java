package com.union.app.plateform.exception.handler;


import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.AppResponse;
import com.union.app.plateform.exception.ApiException;
import com.union.app.plateform.response.ApiResponse;
import com.union.app.plateform.data.resultcode.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(value = { ApiException.class })
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse constraintViolationException(ApiException exception) {
        return ApiResponse.buildResponse(exception.getResultCode(),exception.getResultMsg(),exception);
    }


    @ExceptionHandler(value = { AppException.class })
    @ResponseStatus(HttpStatus.OK)
    public AppResponse constraintViolationException(AppException exception) {
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
        return ApiResponse.buildResponse(ResultCode.E99999999,null);
    }




}