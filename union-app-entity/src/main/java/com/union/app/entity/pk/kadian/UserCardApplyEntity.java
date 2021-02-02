package com.union.app.entity.pk.kadian;

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
@Table(name="T_USER_CARD_APPLY")
public class UserCardApplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int id;

    private String userId;

    private String applyerId;

    private String text;

    private boolean cardLock;

    private long time;


}
