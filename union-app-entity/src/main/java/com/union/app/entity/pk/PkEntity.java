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
@Table(name="T_PK")
public class PkEntity {
    @Id
    private String pkId;

    private String userId;

    private String topic;

    private String watchWord;

    //时长
    private String time;

    //创建时间
    private String createTime;

    private String appName;

    //刷新
    private String pageTime;

    private String nodeUUID;

    private PkStatu pkStatu;

    /**
     * 打榜任务每天重置一次，重置时写入重置时间
     */
    private long resetTime;








}
