package com.union.app.entity.pk;

import com.union.app.entity.pk.社交.GroupStatu;
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
@Table(name="T_PK_CASHIER_GROUP_TABLE")
public class PkCashierGroupEntity {


    @Id
    private String groupId;

    String cashierId;

    String groupUrl;

    String groupMediaId;

    long lastUpdateTime;

    long createTime;

    @Enumerated(EnumType.STRING)
    GroupStatu statu;


}
