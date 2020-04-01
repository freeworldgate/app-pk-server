package com.union.app.plateform.exception;

import com.union.app.plateform.data.resultcode.ResultCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * App运行期抛出异常
 * @author root
 */
@Getter
@Setter
@ToString
public class AppException extends Exception
{

    private String resultCode;

    private String resultMsg;

    private Throwable throwable;


    private AppException(String resultCode, String resultMsg, Throwable throwable) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.throwable = throwable;
    }

    public static AppException buildAppException(ResultCode resultCode, Throwable throwable)
    {
        return new AppException(resultCode.getResultCode(),resultCode.getResultMsg(),throwable);
    }

}
