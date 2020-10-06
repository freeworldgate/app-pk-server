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

    public ImportPost(String topic, List<PkCashier> cashiers) {
        this.topic = topic;
        this.tips = cashiers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<PkCashier> getCashiers() {
        return tips;
    }

    public void setCashiers(List<PkCashier> cashiers) {
        this.tips = cashiers;
    }
}
