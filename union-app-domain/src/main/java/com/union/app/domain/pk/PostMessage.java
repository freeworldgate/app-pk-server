package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostMessage {

    private int type;

    private String pkId;

    private String postId;

    private String userId;

    private String text;

    private String time;

}








