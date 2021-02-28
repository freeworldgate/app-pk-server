package com.union.app.entity.pk.post;

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
@Table(name="T_Id_Greate")
public class IdGreateEntity {

    @Id
    private String id;

    private String userId;

    private String targetId;

    private int statu;

    private long time;


}
