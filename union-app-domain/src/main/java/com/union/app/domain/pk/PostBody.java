package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class PostBody {

    private String postId;

    private String pkId;

    private String userId;

    private String title;

    private List<PostImage> images = new ArrayList<>();


}
