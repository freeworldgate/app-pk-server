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
@Table(name="T_PK_Order")
public class OrderEntity {



    @Id
    private String orderId;

    private String userId;

    private String pkId;

    private String cashierId;

    @Enumerated(EnumType.STRING)
    private OrderStatu orderStatu;


    /**
     * 用户支付后上传的支付凭证
     */
    private String proveImg;









}
