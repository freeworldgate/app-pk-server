package com.union.app.entity.校验表;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="T_Validate")
@Getter
@Setter
@ToString
public class ValidateEntity
{
    @javax.persistence.Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int Id;

    private String validateClass;

    private String validteUniqued;

    @Enumerated(EnumType.STRING)
    private ValidateClassType 校验数据的类型;

    private boolean 校验总开关;

    private boolean 校验失败是否抛出异常;

    private int 返回的异常码;

    private String 返回的异常描述信息;

    private boolean 空值校验开关;

    private boolean 是否可以为空;

    private boolean 长度校验开关;

    private int 最大长度;

    private int 最小长度;

    private boolean 正则表达式校验开关;

    private String 正则表达式;

    private boolean 枚举表达式校验开关;

    /**
     * 支持逗号分隔，仅支持基本类型和字符串
     */
    private String 枚举值;

}
