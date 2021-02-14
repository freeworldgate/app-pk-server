package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkCreator implements Serializable{


    private PkDetail pk;

    private boolean switchBit;


}
