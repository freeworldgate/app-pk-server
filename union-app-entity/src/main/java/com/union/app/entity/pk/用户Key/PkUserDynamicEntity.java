package com.union.app.entity.pk.用户Key;

import com.union.app.entity.pk.消息.MessageType;
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
@Table(name="T_PK_USER_DYNNAMIC")
public class PkUserDynamicEntity
{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int dynamicId;

    private String pkId;

    private String userId;

    private int postTimes;

    //卡点已解锁群组数量  递加
    private int unLockGroups;

    //卡点最新打卡时间
    private long lastPublishPostTime;




}
