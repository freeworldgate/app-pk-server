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
@Table(name="T_PK_ACTIVE_CODE_TABLE")
public class PkActiveEntity {



    @Id
    private String pkId;

    private String activeCode;


    @Enumerated(EnumType.STRING)
    ActiveStatu statu;

    public String tipId;
    //驳回次数
    public int rejectTime;


}
