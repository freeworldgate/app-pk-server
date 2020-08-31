package com.union.app.domain.pk.cashier;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.entity.pk.CashierStatu;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
    private String linkImg;

    private String time;

}
