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
@Table(name="T_User_Balance_History")
public class UserBalanceHistoryEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int historyId;

    private String userId;

    private int payNum;

    @Enumerated(EnumType.STRING)
    private PayScene payScene;

    private String inviteId;











}
