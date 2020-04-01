package com.union.app.entity.标签;


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
@Table(name="T_LABEL")
public class LabelEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int labelId;

    /**
     * 添加人员ID
     */
    private String userId;

    private int statu;

    private String text;

    private int color1;

    private int color2;

    private int color3;

    private int opacity;
}
