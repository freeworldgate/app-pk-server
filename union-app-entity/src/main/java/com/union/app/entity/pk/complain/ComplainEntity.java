package com.union.app.entity.pk.complain;

import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.PkType;
import com.union.app.entity.pk.PostStatu;
import com.union.app.entity.pk.apply.OrderType;
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
@Table(name="T_Pk_Complain_Table")
public class ComplainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    int id;

    String pkId;

    @Enumerated(EnumType.STRING)
    PkType pkType;

    @Enumerated(EnumType.STRING)
    PostStatu postStatu;

    String userId;

    @Enumerated(EnumType.STRING)
    ComplainStatu complainStatu;

    String text;

    long updateTime;



}
