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
@Table(name="T_Pk_User_Dynamic")
public class UserDynamicEntity {
    @Id
    String dynamicId;

    String pkId;

    String userId;

    String feePayUrl;

    String phone;

    @Enumerated(EnumType.STRING)
    ImgStatu feeImgStatu;












}
