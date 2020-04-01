package com.union.app.entity.用户;

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
@Table(name="T_User_Balance")
public class UserBalanceEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int accountId;

    private String userId;

    private int balance;

}
