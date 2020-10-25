package com.union.app.api.pk.zone;

import com.union.app.domain.pk.cashier.PkCashier;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportPost {


    private String topic;

    private List<PkCashier> tips;

    private String imgBack;

    public ImportPost(String topic, List<PkCashier> tips, String imgBack) {
        this.topic = topic;
        this.tips = tips;
        this.imgBack = imgBack;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<PkCashier> getTips() {
        return tips;
    }

    public void setTips(List<PkCashier> tips) {
        this.tips = tips;
    }

    public String getImgBack() {
        return imgBack;
    }

    public void setImgBack(String imgBack) {
        this.imgBack = imgBack;
    }
}
