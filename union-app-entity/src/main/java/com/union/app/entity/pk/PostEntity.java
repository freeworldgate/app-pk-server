package com.union.app.entity.pk;

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
@Table(name="T_Post")
public class PostEntity {
    @Id
    private String postId;

    private String pkId;

    private String userId;

    private String topic;


    private int imgNum;

    private int postTimes;
    private long time;


    @Enumerated(EnumType.STRING)
    private PostStatu statu;

}
