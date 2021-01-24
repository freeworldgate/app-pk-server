package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkActive implements Serializable{

    String activeCode;

    String pkId;

    KeyNameValue statu;

    String tip;

    int rejectTimes;

    int  maxModifyTimes ;
}
