package com.union.app.entity.pk;

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
@Table(name="T_Post")
public class PostEntity {
    @Id
    private String postId;

    private long sortId;

    private String pkId;

    private String pkName;

    private String userId;

    private String topic;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private String videoUrl;

    private int width;
    private int height;

    private String backUrl;

    private String backColor;

    private String fontColor;

    private int imgNum;

    private int postTimes;

    private long time;


    @Enumerated(EnumType.STRING)
    private PostStatu statu;


    //投诉   点赞  评论
    private long complains;
    private long likes;
    private long dislikes;
    private long comments;








}
