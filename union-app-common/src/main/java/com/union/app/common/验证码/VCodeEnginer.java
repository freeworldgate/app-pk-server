package com.union.app.common.验证码;

public class VCodeEnginer {
    public static boolean validate(String userTel,String vCode) {

        return "8888".equalsIgnoreCase(vCode);

    }
}
