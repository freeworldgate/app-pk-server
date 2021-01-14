package com.union.app.entity.pk.社交;

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
@Table(name="T_PK_GROUP")
public class PkGroup {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int groupId;

    private String groupName;

    private String groupDesc;

    private String pkId;

    private String userId;

    private String url;

    /**
     * 需要基于Redis并发锁老修改其值
     */
    private int members;

    private long time;



}
