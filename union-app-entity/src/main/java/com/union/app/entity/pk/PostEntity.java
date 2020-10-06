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

    @Enumerated(EnumType.STRING)
    private PkType pkType;

    private String userId;

    private String topic;

    private String selfComment;

    private long selfCommentTime;

    private int imgNum;

    private String createTime;

    private String lastModifyTime;

    @Enumerated(EnumType.STRING)
    private PostStatu statu;

    private String imgUrls;

    private String wxCode;

    private long shareTime;

    @Enumerated(EnumType.STRING)
    private ApproveStatu approveStatu;


    private int rejectTimes;
    private String rejectTextBytes;
}
