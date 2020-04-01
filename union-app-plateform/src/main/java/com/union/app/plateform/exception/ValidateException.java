package com.union.app.plateform.exception;

import com.union.app.plateform.data.resultcode.ResultCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API请求异常
 */
@Getter
@Setter
@ToString
public class ValidateException extends Exception
{

    private String resultCode;

    private String resultMsg;

    private Throwable throwable;


    private ValidateException(String resultCode, String resultMsg, Throwable throwable) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.throwable = throwable;
    }

    public static ValidateException buildException(ResultCode resultCode, Throwable throwable)
    {
        return new ValidateException(resultCode.getResultCode(),resultCode.getResultMsg(),throwable);
    }


}
