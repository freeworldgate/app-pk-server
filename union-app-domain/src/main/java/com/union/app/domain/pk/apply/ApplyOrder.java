package com.union.app.domain.pk.apply;

import com.union.app.domain.pk.ShareDynamicMessage;
import com.union.app.domain.pk.share.CreatorInfo;
import com.union.app.domain.pk.share.PosterInfo;
import com.union.app.domain.pk.share.ShareInfo;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.OrderStatu;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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




}




