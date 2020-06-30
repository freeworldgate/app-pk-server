package com.union.app.entity.pk;

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
@Table(name="T_PK_ACTIVE")
public class PkActiveEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int id;

    private String pkId;

    private String cashierId;

    private String commentImgUrl;

    private String mediaId;

    private String commentText;



    private String time;











}
