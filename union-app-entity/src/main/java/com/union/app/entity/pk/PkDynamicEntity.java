package com.union.app.entity.pk;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_PK_Dynamic")
public class PkDynamicEntity {
    @Id
    private String pkId;

    //图片数量
    private int totalImages;

    //群组
    private int pkGroups;

    //捞人
    private int pkFinds;










}
