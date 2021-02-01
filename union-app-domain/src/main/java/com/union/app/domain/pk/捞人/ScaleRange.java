package com.union.app.domain.pk.捞人;

import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ScaleRange implements Serializable{

    private int range;

    private int scale;

    private double offset;


}
