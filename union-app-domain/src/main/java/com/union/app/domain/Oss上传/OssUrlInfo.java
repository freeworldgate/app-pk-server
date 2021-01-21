package com.union.app.domain.Oss上传;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class OssUrlInfo
{

    private String taskId;

    private String directory;

    /*文件后缀*/
    private String suffix;

    /*文件前缀*/
    private String prefix;

    private String policyBase64 = "eyJleHBpcmF0aW9uIjoiMjAyOS0wMS0yMFQwNTowMjowMi45NDFaIiwiY29uZGl0aW9ucyI6W1siY29udGVudC1sZW5ndGgtcmFuZ2UiLDAsMjA5NzE1MjBdXX0=";

    private String signature = "4Jp/QohA85G/4pJ7cNlpSKeEjVI=";

    private String oSSAccessKeyId = "";

    private String aliyunServerURL = "https://oss.211shopper.com";







}
