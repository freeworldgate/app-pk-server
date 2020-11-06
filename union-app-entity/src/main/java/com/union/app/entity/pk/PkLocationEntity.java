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
@Table(name="T_PK_LOCATION")
public class PkLocationEntity {
    @Id
    private String pkId;

    private String name;

    private String description;

    private String cityCode;

    private String city;

    private long latitude;

    private long longitude;



}
