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
@Table(name="T_PK_GREATE")
public class GreatePkEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int greateId;

    private String userId;

    private String pkId;


    private long createTime;











}
