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
@Table(name="T_FIND_USER")
public class UserCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int findId;

    private String pkId;

    private String pkName;

    private String userId;

    private String text;

    private String img1;

    private String img2;

    private String img3;

    private int findLength;

    private long startTime;

    private long endTime;

    @Enumerated(EnumType.STRING)
    private FindStatu findStatu;

    private String approverId;



    private long createTime;




}
