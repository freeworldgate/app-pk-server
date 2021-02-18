package com.union.app.entity.pk.城市;

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
@Table(name="T_CITY")
public class CityEntity {

    @Id
    private int cityCode;

    private String cityName;



}
