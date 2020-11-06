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

    //创建时间
    private long createTime;


    @Enumerated(EnumType.STRING)
    private PkType pkType;






    //--------------------可变数据----------------------

    private String topPostUserId;

    private int complainTimes;

    @Enumerated(EnumType.STRING)
    private PkStatu albumStatu;

    private long updateTime;

    private String backUrl;


}
