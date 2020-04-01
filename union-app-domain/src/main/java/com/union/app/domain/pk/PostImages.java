package com.union.app.domain.pk;

import com.union.app.domain.pk.PostImage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PostImages {


    private String postId;

    private String userId;

    private List<PostImage> postImages;




}
