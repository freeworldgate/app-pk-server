package com.union.app.domain.pk.zone;


import com.union.app.domain.pk.PkDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Zone {


    private String sessionId;

    private int index;

    private PkDetail pk;


}
