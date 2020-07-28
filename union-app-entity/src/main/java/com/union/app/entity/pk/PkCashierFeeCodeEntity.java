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
@Table(name="T_PK_CASHIER_FEECODE_TABLE")
public class PkCashierFeeCodeEntity {


    @Id
    private String feeCodeId;

    String cashierId;

    String feeCodeUrl;

    String feeCodeMediaId;

    long lastUpdateTime;

    long createTime;

    @Enumerated(EnumType.STRING)
    FeeNumber feeNumber;

    int confirmTimes;
}
