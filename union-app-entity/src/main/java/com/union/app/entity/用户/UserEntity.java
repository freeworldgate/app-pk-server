package com.union.app.entity.用户;


import com.union.app.entity.用户.support.UserPostStatu;
import com.union.app.entity.用户.support.UserType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_User")
public class UserEntity {

    @Id
    private String userId;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String openId;

    private String appName;

    private String sessionId;

    private String nickName;

    private String avatarUrl;


    private String fromUser;

    private long postLastUpdateTime;









}
