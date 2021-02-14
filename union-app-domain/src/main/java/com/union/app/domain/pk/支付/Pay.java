package com.union.app.domain.pk.支付;

import com.union.app.entity.pk.PkTipEntity;
import com.union.app.entity.pk.支付.PayEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class Pay implements Serializable{

    private String payId;

    private int payType;

    private String title;


    private PayEntity selectPay;

    private List<PayEntity> payItems;


    private List<PkTipEntity> tips;



}
