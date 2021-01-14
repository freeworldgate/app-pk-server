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
@Table(name="T_PK_GROUP_MEMBER")
public class PkGroupMember {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int groupId;

    private String userId;

    private long time;





}
