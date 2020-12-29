package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PkImage {

    private String imageId;
    private String pkId;
    private User user;
    private String imgUrl;
    private KeyValuePair statu;
    private String time;
}
