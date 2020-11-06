package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PkLocation {

    private String pkId;

    private String name;

    private String desc;

    private String cityCode;

    private String city;

    private long latitude;

    private long longitude;



}
