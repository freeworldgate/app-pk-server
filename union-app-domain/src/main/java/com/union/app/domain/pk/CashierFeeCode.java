package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CashierFeeCode implements Serializable{

    String cashierId;

    String feeCodeId;

    String feeCodeUrl;

    KeyNameValue statu;

    KeyNameValue feeNumber;


    long selectTime;

    long confirmTime;


}
