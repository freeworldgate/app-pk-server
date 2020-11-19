package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Callout {
    String content;
    int fontSize = 10;
    int padding = 10;
    int borderRadius = 5;
    String display = "ALWAYS";



}
