package com.union.app.domain.pk.支付;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;

@Getter
@Setter
@ToString
public class PayOrder {


    private String orderId;

    private String title;

    private User user;

    private int type;

    private int payValue;

    private int value;

    private String time;

    private String timeStamp;
    private String nonceStr;
    private String packageStr;
    private String signType = "MD5";
    private String paySign;







}




