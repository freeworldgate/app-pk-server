package com.union.app.domain.pk.交友;

import com.union.app.domain.pk.Callout;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.社交.PkGroupMemberEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PkGroup {



    private int groupId;

    private String pkId;

    private String pkName;

    private int pkRange;

    private double latitude;

    private double longitude;

    private String groupCard;

    private String groupName;

    private String groupDesc;

    private User user;

    private KeyValuePair statu;

    private long members;

    private User member1;

    private User member2;

    private String time;
    private String lastUpdateTime;



    private PkGroupMemberEntity queryerMemberEntity;
    private String tip;


}
