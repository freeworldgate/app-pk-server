package com.union.app.domain.wechat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author root
 */
@ToString
@Getter
@Setter
public class WeChatUser implements Serializable
{

    private String session_key;

    private String openid;

}
