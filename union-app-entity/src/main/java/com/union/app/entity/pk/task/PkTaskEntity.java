package com.union.app.entity.pk.task;

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
@Table(name="T_Pk_Task")
public class PkTaskEntity {
    @Id
    String taskId;

    String pkId;

    String feeUser;

    @Enumerated(EnumType.STRING)
    TaskStatu statu;

}
