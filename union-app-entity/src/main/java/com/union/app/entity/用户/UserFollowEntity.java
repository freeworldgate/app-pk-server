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
@Table(name="T_User_Follow")
public class UserFollowEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    private String userId;

    private String ownerId;




}
