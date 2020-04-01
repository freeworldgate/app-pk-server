package com.union.app.common.api.registry.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceUrlBean
{

    private String serviceUrl;
    private String serviceDesc;
    private String apiUrl;
    private String apiDesc;


    public String contactUrl() {

        return "";
    }
}
