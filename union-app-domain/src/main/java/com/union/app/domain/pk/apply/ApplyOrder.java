package com.union.app.domain.pk.apply;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplyOrder {

    String orderId;

    KeyNameValue type;

    String pkId;

    private User cashier;

    private User payer;

    private int feeNum;

    private String feeCodeUrl;

    private String orderCut;

    private KeyNameValue statu;

    private boolean complain;

    private int rewardTimes;

    private int leftTimes;
}




