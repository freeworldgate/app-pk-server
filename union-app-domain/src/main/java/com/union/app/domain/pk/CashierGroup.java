package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CashierGroup implements Serializable{


    String groupId;

    String groupUrl;

    KeyNameValue statu;

    long members;

    String time;

}
