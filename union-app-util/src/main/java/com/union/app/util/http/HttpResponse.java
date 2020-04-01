package com.union.app.util.http;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class HttpResponse {

    private int statusCode;

    private String responseEntity;


    public <T> T praseEntity(Class<T> tClass)
    {

        return null;
    }





}
