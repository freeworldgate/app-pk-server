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
@Table(name="T_ACTIVE_CODE")
public class ActiveCodeEntity {


    @Id
    private String activeCode;

    private String cashierId;

    private String userId;

    private int activeTimes;

    @Enumerated(EnumType.STRING)
    private CodeStatu codeStatu;

    private long time;
}
