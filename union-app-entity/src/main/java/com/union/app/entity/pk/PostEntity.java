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

    private String userId;

    private byte[] topic;
    private byte[] selfComment;
    private long selfCommentTime;

    private int imgNum;

    private String createTime;

    private String lastModifyTime;

    @Enumerated(EnumType.STRING)
    private PostStatu statu;

    private String approveUserId;

    private String imgUrls;



}
