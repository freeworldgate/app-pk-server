package com.union.app.entity.pk;

import com.union.app.entity.ImgStatu;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_PK_IMAGE")
public class PkImageEntity {
    @Id
    private String imgId;

    private String pkId;

    private String imgUrl;

    private String userId;

    @Enumerated(EnumType.STRING)
    private ImgStatu imgStatu;

    private long time;


}
