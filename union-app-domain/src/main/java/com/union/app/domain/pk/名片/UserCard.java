package com.union.app.domain.pk.名片;

import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCard {



    private int groupId;

    private String pkId;

    private int pkRange;

    private double latitude;

    private double longitude;

    private String groupCard;

    private String groupName;

    private String groupDesc;

    private User user;

    private KeyValuePair statu;

    private int members;

    private User member1;

    private User member2;

    private String time;
    private String lastUpdateTime;


}
