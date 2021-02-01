package com.union.app.entity.user;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_USER_KEY_VALUE_DYNAMIC")
public class UserDynamicEntity {

    @Id
    private String userId;

    private String userBack;

    //可打捞时间
    private long findTimeLength;

    //可创建卡点数
    private int pk;

    //可创建群数量
    private int mygroups;

    //打卡总次数
    private int postTimes;

    //用户解锁群组总数
    private int unLockTimes;

    //已创建Pk
    private int pkTimes;

    //收藏个数
    private int collectTimes;


    //用户关注
    private int follow;
    //用户粉丝
    private int fans;


    //已打捞次数
    private int findTimes;
    //已打捞时间
    private long findLength;







//
//    private int postTimes;
//    private int pkTimes;
//    private int publishPkTimes;
//    private int inviteTimes;
//    private int unlockTimes;











}
