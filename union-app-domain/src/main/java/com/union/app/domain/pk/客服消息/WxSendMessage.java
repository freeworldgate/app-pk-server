package com.union.app.domain.pk.客服消息;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WxSendMessage {

    String access_token;

    String touser;

    String msgtype;

    WxText text;

    WxImage image;

    WxLink link;








}
