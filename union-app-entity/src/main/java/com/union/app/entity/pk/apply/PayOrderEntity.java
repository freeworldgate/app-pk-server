package com.union.app.entity.pk.apply;

import com.union.app.entity.pk.OrderStatu;
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
@Table(name="T_Pk_Pay_Order")
public class PayOrderEntity {

    @Id
    String orderId;

    private String pkId;
    //收款方
    private String cashierId;
    // 支付方
    private String payerId;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderStatu orderStatu;

    private String orderCut;

    private boolean complain;









}
