package com.union.app.entity.pk.文字背景;

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
@Table(name="T_TEXT_BACK")
public class TextBackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private String backId;

    private String backColr;

    private String backUrl;

    private String fontColor;




}
