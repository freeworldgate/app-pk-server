package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.ActiveStatu;
import com.union.app.entity.pk.PkType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
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
