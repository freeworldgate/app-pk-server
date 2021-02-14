package com.union.app.domain.pk.cashier;

import com.union.app.domain.pk.apply.KeyNameValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PkCashier
{
    private String cashierId;

    private String cashierName;


    private int selectTimes;

    private KeyNameValue statu;
    private KeyNameValue type;

    private String img;
    private String backPng;


    private String time;



}
