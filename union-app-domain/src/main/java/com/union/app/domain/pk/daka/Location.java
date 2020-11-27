package com.union.app.domain.pk.daka;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Location {

    private String locationId;

    private String name;

    private String address;

    private long latitude;

    private long longitude;



}
