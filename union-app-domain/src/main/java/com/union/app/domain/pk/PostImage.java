package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PostImage implements Serializable {

    private String imageId;

    private String pkId;

    private String postId;

    private String imgUrl;

    private String time;






}
