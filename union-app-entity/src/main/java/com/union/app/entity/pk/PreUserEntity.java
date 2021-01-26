package com.union.app.entity.pk;

import com.union.app.entity.user.support.UserType;
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
@Table(name="T_Pre_User")
public class PreUserEntity {


    @Id
    private String userId;

    private String imgUrl;

    private String  userName;

    @Enumerated(EnumType.STRING)
    private UserType userType;











}
