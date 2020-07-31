package com.union.app.domain.pk;

import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.审核.ApproveMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ActivePk {

    private PkDetail pk;

    private ApproveMessage approveMessage;

    private PkCashier pkCashier;

    private CashierGroup cashierGroup;

    private CashierFeeCode cashierFeeCode;

    private String cashierCommentUrl;
}