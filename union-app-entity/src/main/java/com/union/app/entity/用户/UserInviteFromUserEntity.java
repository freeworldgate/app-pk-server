package com.union.app.entity.用户;

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
@Table(name="T_User_Invite_FromUser")
public class UserInviteFromUserEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    private String userId;

    private String inviteId;

    private String fromUser;


}
