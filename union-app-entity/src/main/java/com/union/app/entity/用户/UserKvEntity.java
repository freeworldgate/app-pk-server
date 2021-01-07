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
public class UserKvEntity {

    @Id
    private String userId;

    private long findTimeLength;

    private int pk;

    private String userCard;














    private int postTimes;
    private int pkTimes;
    private int publishPkTimes;
    private int inviteTimes;
    private int unlockTimes;













    //用户总的激活PK次数  （发布第一贴时+1）
    private int activePks;
    //可激活主题次数    购买次数，支付成功后增加次数
    private int feeTimes;







}
