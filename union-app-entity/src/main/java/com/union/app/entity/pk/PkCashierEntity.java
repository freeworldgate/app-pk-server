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
@Table(name="T_PK_CASHIER_LINK_URL_TABLE")
public class PkCashierEntity {


    @Id
    String cashierId;

    String realName;

    @Enumerated(EnumType.STRING)
    CashierStatu statu;

    int selectTimes;

    String linkUrl;
    String mediaId;
    long lastUpdateTime;

    @Enumerated(EnumType.STRING)
    LinkType linkType;

    long time;

    //总数
    int activeCodes;

    //已使用
    int noActiveCode;

    //已激活
    int usedActiveCode;


}
