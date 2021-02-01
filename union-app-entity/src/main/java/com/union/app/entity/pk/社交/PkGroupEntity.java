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
@Table(name="T_PK_GROUP")
public class PkGroupEntity {


    @Id
    private String groupId;

    private String groupName;

    private String groupDesc;

    @Enumerated(EnumType.STRING)
    private GroupStatu groupStatu;

    private String pkId;

    private String pkName;

    private String userId;

    private String groupCode;

    private String updateGroupCode;
    /**
     * 需要基于Redis并发锁老修改其值
     */
    private int members;

    private String user1;

    private String user2;

    private long time;

    private long lastUpdateTime;





}
