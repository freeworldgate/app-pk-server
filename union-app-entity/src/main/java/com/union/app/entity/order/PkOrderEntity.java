package com.union.app.entity.order;

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
public class PkOrderEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    String orderId;

    String payUser;

    String cashier;

    PkOrderStatu statu;

    String orderUrl;

    String complainUrl;

    String time;






















}
