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
@Table(name="T_PK_CASHIER_TABLE")
public class PkCashierEntity {


    @Id
    String cashierId;

    String realName;

    @Enumerated(EnumType.STRING)
    CashierStatu statu;

    int selectTimes;





}
