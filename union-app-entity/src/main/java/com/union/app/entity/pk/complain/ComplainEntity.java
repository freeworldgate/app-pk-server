package com.union.app.entity.pk.complain;

import com.union.app.entity.pk.OrderStatu;
import com.union.app.entity.pk.apply.OrderType;
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
@Table(name="T_Pk_Complain")
public class ComplainEntity {

    @Id
    String id;


    ComplainType complainType;


    ComplainStatu statu;


    long time;



}
