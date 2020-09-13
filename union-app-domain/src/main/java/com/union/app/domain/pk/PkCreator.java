package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.InviteType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkCreator implements Serializable{


    private PkDetail pk;

    private boolean switchBit;


}
