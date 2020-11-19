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
    private String pkBackUrl;

//    private PkDetail pkDetail;


    private long scan;

    private String wxCode;

    private boolean isQueryerCollect;

    private User creator;

    private String topic;

    private List<PostImage> postImages;

    private PostDynamic dynamic;

    private KeyNameValue statu;

    private UserIntegral userIntegral;


    private ApproveComment approveComment;


    private String time;

    private String selfComment;
    private String selfCommentTime;

    private int maxRejectTimes;
    private int rejectTimes;
    private String rejectText;


    private int style = RandomUtil.getRandomNumber()%6 + 1;
    private String backUrl ;

    private boolean flag = false;

    private String imgBack;
}
