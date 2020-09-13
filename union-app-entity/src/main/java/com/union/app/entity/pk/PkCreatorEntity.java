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
@Table(name="T_PK_CREATOR")
public class PkCreatorEntity {
    @Id
    private String pkId;

    private String userId;

    private boolean switchBit;






}
