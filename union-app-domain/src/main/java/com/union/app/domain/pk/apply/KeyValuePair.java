package com.union.app.domain.pk.apply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class KeyValuePair implements Serializable {

    private int key;
    private String name;


}
