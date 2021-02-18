package com.union.app.plateform.data.resultcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ToString
@AllArgsConstructor
public enum ResultCode
{

    E03000001("0x03000000","Success!"),

    E99999999("0x99999999","Internal Error!"),




    ;


    private String resultCode;

    private String resultMsg;


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
