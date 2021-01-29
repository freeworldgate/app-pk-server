package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.ApproveComment;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.PkLocationEntity;
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
    private PkLocationEntity location;

    private User creator;

    private String topic;

    private String backColor;

    private String fontColor;

    private float opacity;;

    private String backUrl;

    private int postTimes;

    private List<PostImage> postImages;

    private String time;

    private boolean flag = false;


}
