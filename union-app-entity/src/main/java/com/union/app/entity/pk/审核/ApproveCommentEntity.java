package com.union.app.entity.pk.审核;

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
@Table(name="T_APPROVE_COMMENT")
public class ApproveCommentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int commentId;
    private String pkId;
    private String postId;
    private String approverId;
    private byte[] text;
    private String imgUrl;
    private long time;



}
