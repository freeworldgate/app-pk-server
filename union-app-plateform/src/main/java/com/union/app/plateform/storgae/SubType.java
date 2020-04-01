package com.union.app.plateform.storgae;

import org.springframework.data.redis.listener.PatternTopic;

public enum SubType {



    分享记录("PostShareHistory",1),
    助力记录("PostFollowHistory",2),
    浏览记录("PostViewHistory",3),
    评论记录("PostCommentHistory",4),


    ;

    private String topic;

    private int type;

    SubType(String topic, int type) {
        this.topic = topic;
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static SubType valueOf(int type){
        if(type==1){return SubType.分享记录;}
        if(type==2){return SubType.助力记录;}
        if(type==3){return SubType.浏览记录;}
        if(type==4){return SubType.评论记录;}

        return null;
    }


}
