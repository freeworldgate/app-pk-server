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
@Table(name="T_APPROVE_MESSAGE")
public class ApproveMessageEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int messageId;


    private String msgId;


    private String pkId;

    private String approverUserId;

    private byte[] text;

    private String imgUrl;

    private String mediaId;

    private long time;




}
