package com.union.app.entity.配置表;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="T_CONFIG")
public class ConfigEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    private String configName;

    private String configDesc;

    private String configValue;

    private String defaultValue;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


    @Enumerated(EnumType.STRING)
    private ColumSwitch columSwitch = ColumSwitch.ON;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigDesc() {
        return configDesc;
    }

    public void setConfigDesc(String configDesc) {
        this.configDesc = configDesc;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public ColumSwitch getColumSwitch() {
        return columSwitch;
    }

    public void setColumSwitch(ColumSwitch columSwitch) {
        this.columSwitch = columSwitch;
    }
}
