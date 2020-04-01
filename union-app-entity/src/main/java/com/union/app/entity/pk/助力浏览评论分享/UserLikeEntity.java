package com.union.app.entity.pk.助力浏览评论分享;

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
@Table(name="T_User_Like_Post")
public class UserLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int likeId;

    private String pkId;

    private String postId;

    private String userId;

    private String time;

    private int value;












}
