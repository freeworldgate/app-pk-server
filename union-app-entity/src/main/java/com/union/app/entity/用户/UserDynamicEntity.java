package com.union.app.entity.用户;


import com.union.app.entity.用户.support.UserType;
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
@Table(name="T_User_KEY_VALUE")
public class UserDynamicEntity {

    @Id
    private String userId;

    private long findTimeLength;

    private int pk;

    private String userCard;

    //打卡总次数
    private int postTimes;

    //已创建Pk
    private int pkTimes;

    //收藏个数
    private int collectTimes;









//
//    private int postTimes;
//    private int pkTimes;
//    private int publishPkTimes;
//    private int inviteTimes;
//    private int unlockTimes;











}
