package com.union.app.domain.pk.名片;

import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCard {


    private User user;

    private String userCard;

    //想认识我的人
    private int likeMe;
    //我想认识的人
    private int meLike;
    //已解锁用户
    private int unLock;

    private User member1;
    private User member2;
    private User member3;

}
