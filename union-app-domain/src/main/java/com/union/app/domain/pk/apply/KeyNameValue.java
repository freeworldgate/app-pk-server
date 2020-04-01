package com.union.app.domain.pk.apply;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KeyNameValue implements Serializable {

    private int key;
    private String name;


}
