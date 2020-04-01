package com.union.app.plateform.response;

import com.union.app.plateform.data.resultcode.ResultCode;
import lombok.*;

@Getter
@Setter
@ToString
public class ApiResponse {

    private String code;

    private String msg;

    private Object data;

    private int type;





    private ApiResponse(ResultCode resultCode, Object data) {
        this.code = resultCode.getResultCode();
        this.msg = resultCode.getResultMsg();
        this.data = data;
    }

    public ApiResponse(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResponse buildResponse(ResultCode resultCode, Object data)
    {
        return new ApiResponse(resultCode,data);

    }
    public static ApiResponse buildResponse(String code,String msg, Object data)
    {
        return new ApiResponse(code,msg,data);

    }
    public static ApiResponse buildSuccessResponse(Object data)
    {
        return new ApiResponse(ResultCode.E03000001,data);
    }



}
