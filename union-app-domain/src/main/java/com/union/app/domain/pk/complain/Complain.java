package com.union.app.domain.pk.complain;

import com.union.app.domain.pk.UserCode;
import com.union.app.domain.pk.apply.ApplyOrder;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.entity.pk.complain.ComplainType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Complain {

    String id;

    UserCode userCode;

    ApplyOrder applyOrder;

    ComplainType complainType;




}
