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

    private String pkId;

    private String pkName;

    private String userId;

    private String topic;

    private String backUrl;

    private String backColor;

    private String fontColor;

    private int imgNum;

    private int postTimes;
    private long time;


    @Enumerated(EnumType.STRING)
    private PostStatu statu;

}
