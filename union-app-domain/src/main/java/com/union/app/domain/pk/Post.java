package com.union.app.domain.pk;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class Post {


    private int current  = 0;

    private String postId;

    private String pkId;

    private String pkTopic;


    private User creator;

    private String topic;

    private String backColor;

    private String fontColor;

    private float opacity;;

    private String backUrl;

    private int postTimes;

    private List<PostImage> postImages;

    private String time;

    private PostTime ptime;

    private boolean flag = false;

    private int leftTime;

}
