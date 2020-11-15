package com.union.app.domain.pk.complain;

import com.union.app.domain.pk.UserCode;
import com.union.app.domain.pk.apply.ApplyOrder;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.entity.pk.complain.ComplainType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Complain {


    private int complainId;
    private User user;

    private String pkId;

    private String postId;

    private String statu;

    private String text;

    private String url;

    private String time;




}
