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
@Table(name="T_Home_Page_PK")
public class HomePagePk {


    @Id
    private String pkId;

    private PkType pkType;

    private long  priority;

    private int approved;
    private int approving;
    private int groupStatu;









}
