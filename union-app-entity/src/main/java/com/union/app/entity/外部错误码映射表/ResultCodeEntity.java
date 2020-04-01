package com.union.app.entity.外部错误码映射表;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

/**
 * 不考虑国际化
 */
@Entity
@Table(name="T_Result_Code")
@Getter
@Setter
@ToString
public class ResultCodeEntity
{
    @javax.persistence.Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int Id;

    private String resultCode;

    private String outCode;

    private String resultDesc;

}
