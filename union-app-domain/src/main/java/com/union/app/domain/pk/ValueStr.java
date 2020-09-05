package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValueStr {

    private String castV1;
    private String castV2;
    private String castV3;

    public ValueStr(String castV1, String castV2, String castV3) {
        this.castV1 = castV1;
        this.castV2 = castV2;
        this.castV3 = castV3;
    }
}
