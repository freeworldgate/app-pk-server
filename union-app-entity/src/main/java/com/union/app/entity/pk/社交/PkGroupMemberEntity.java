package com.union.app.entity.pk.社交;

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
@Table(name="T_PK_GROUP_MEMBERS")
public class PkGroupMemberEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int memberId;

    private String groupId;

    private String pkId;

    private String userId;

    private long time;





}
