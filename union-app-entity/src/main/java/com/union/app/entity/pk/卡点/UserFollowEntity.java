package com.union.app.entity.pk.卡点;

import com.union.app.entity.ImgStatu;
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
@Table(name="T_USER_FOLLOW")
public class UserFollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int id;

    private String userId;

    private String followerId;

    private long time;


}
