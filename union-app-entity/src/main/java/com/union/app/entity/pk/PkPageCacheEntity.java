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
@Table(name="T_PK_Page_Cache")
public class PkPageCacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int cacheId;

    private String pkId;

    private String page;

    private String postId;

    private int priority;

    private String lastModifyTime;


}
