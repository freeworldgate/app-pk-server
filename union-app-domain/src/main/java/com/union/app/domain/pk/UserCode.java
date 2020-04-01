package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCode {

    private String pkId;

    private User user;

    private int feeNum;

    private String url;

    private KeyNameValue statu;

    private String phone;

}
