package com.union.app.domain.pk.daka;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
@ToString
public class CreateLocation {

    String userId;

    double latitude;

    double longitude;

    String name;

    String address;

    String sign;

    String type;

    String backUrl;

//    String label;


}
