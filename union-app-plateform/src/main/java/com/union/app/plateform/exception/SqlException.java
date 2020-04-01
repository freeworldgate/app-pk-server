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
public class SqlException extends Exception
{

    private String resultCode;

    private String resultMsg;

    private Throwable throwable;


    private SqlException(String resultCode, String resultMsg, Throwable throwable) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.throwable = throwable;
    }

    public static SqlException buildException(ResultCode resultCode, Throwable throwable)
    {
        return new SqlException(resultCode.getResultCode(),resultCode.getResultMsg(),throwable);
    }


}
