package com.union.app.entity.pk.名片;

import com.union.app.entity.pk.卡点.捞人.FindStatu;
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
@Table(name="T_USER_CARD")
public class UserCardEntity {

    @Id
    private String userId;

    private String userCard;

    private int unLock;

    private int likeMe;

    private int meLike;

    private String member1;

    private String member2;

    private String member3;

    private long time;


}
