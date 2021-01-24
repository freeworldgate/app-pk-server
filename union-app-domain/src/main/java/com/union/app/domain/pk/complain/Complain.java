package com.union.app.domain.pk.complain;

import com.union.app.domain.user.User;
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
