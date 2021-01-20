package com.union.app.entity.pk.名片;

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
@Table(name="T_USER_CARD_MEMBER")
public class UserCardMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int memberId;

    private String userId;

    private String memberUserId;

    private long time;


}
