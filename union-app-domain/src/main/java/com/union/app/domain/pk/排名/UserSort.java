package com.union.app.domain.pk.排名;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserSort implements Serializable{

    private String pkId;

    private User user;

    private int postTimes;

    private String lastPublishPostTime;



}
