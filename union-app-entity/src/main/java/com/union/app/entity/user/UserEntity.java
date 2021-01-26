package com.union.app.entity.user;


import com.union.app.entity.user.support.UserType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;


@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_User")
public class UserEntity implements Serializable {

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










}
