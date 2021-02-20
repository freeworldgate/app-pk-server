package com.union.app.entity.pk.支付;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_PAY_ITEM")
public class PayEntity {


    @Id
    private String payId;

    private int payType;

    private String tag;

    private int value;

    private int pay;

    private String selectImg;

    private String img;

    private long time;





}
