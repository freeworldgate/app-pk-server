package com.union.app.entity.pk;

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
@Table(name="T_PK_CASHIER")
public class PkCashierEntity {


    @Id
    private String cashierId;

    private String realName;

    private String cashierName;

    private String cashierImg;

    private int feeNum;

    private String imgUrl;

    private String text;

    private String mediaId;

    @Enumerated(EnumType.STRING)
    private CashierStatu statu;

    private int selectTimes;

    private int confirmTimes;

    private String time;



}
