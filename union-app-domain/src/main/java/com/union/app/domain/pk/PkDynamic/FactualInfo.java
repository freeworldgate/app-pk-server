package com.union.app.domain.pk.PkDynamic;


import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FactualInfo implements Serializable {

    private String factualId;

    private User user;

    private KeyNameValue operType;

    private String time;









}
