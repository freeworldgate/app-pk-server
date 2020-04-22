package com.union.app.domain.pk.integral;


import com.union.app.domain.pk.Post;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIntegral {

    User user;

    boolean isCreator;

    boolean sort;

    int index;

    int follow;

    int share;

    int select;

    int approved;

    int approving;

}
