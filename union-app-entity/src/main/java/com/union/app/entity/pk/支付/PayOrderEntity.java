package com.union.app.entity.pk.支付;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_PAY_ORDER")
public class PayOrderEntity {


    @Id
    private String orderId;

    private String appId;

    private String userId;

    private String title;

    private int type;

    private int payValue;

    private int value;

    private long time;

    private String timeStamp;
    private String nonceStr;
    private String packageStr;
    private String signType;
    private String paySign;


}
