package com.union.app.entity.应用信息表;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;


@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_APP")
public class ApplicationEntity {

    @Id
    private String appId;

    private String appName;

    private String title ;







}
