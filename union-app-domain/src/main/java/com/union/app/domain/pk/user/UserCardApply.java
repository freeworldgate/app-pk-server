package com.union.app.domain.pk.user;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserCardApply implements Serializable{


    private int applyId;

    private User target;

    private User applyer;

    private String text;

    private boolean lock;

    private String time;

}
