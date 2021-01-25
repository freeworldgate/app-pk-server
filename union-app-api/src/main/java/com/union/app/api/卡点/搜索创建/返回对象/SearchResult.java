package com.union.app.api.卡点.搜索创建.返回对象;

import com.union.app.common.config.AppConfigService;
import com.union.app.domain.pk.Circle;
import com.union.app.domain.pk.Marker;
import com.union.app.domain.pk.PkDetail;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.DataSet;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

@Setter
@Getter
public class SearchResult {

    private int maxLength;
    private Marker[] markers;
    private Circle[] circles;
    private int scale;
    private double latitude;
    private double longitude;
    private PkDetail pk;


}
