package com.union.app.domain.pk.wechat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccessToken {
    private String access_token;

    private int expires_in;
}
