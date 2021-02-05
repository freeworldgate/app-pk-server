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
@Table(name="T_Tips")
public class PkTipEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int id;

    private int type;

    private String tip;







}
