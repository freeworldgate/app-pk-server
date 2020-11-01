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

    private int postTimes;
    private int pkTimes;
    private int activePkTimes;
    private int inviteTimes;
    private int unlockTimes;
    private int feeTimes;
    private int usedTimes;







}
