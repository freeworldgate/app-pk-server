package com.union.app.common.config;

import com.union.app.common.dao.KeyService;
import com.union.app.common.spring.context.SpringContextUtil;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.storgae.KeyType;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AppConfigService
{
    private static AppDaoService appDao = SpringContextUtil.getBean(AppDaoService.class);
    private static KeyService keyService = SpringContextUtil.getBean(KeyService.class);

    public static String getConfigAsString(String configName,String defaultValue) {
        /*从缓存获取*/
        String cacheValue = keyService.queryStringValue(configName, KeyType.配置缓存);

        if(StringUtils.isNotBlank(cacheValue)){return cacheValue;}
        try {
            EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(ConfigEntity.class).compareFilter("configName", CompareTag.Equal, configName);
            ConfigEntity configEntity = appDao.querySingleEntity(ConfigEntity.class, entityFilterChain);
            if (configEntity == null) {
                return defaultValue;
            }
            if (configEntity.getColumSwitch() == ColumSwitch.OFF)
            {
                return defaultValue;
            }
            String value = configEntity.getConfigValue();
            if (StringUtils.isBlank(value)) {
                String configDefaultValue = configEntity.getDefaultValue();
                if(StringUtils.isBlank(configDefaultValue))
                {
                    return defaultValue;
                }
                else
                {
                    return configDefaultValue;
                }
            }
            /*缓存配置项*/
            keyService.保存配置缓存(configName, value,KeyType.配置缓存);
            return value;
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static String getConfigAsString(ConfigItem configItem) {
        return getConfigAsString(configItem.getName(),configItem.getDefaultValue());
    }
    public static int getConfigAsInteger(ConfigItem configItem) {
        return getConfigAsInteger(configItem.getName(),Integer.valueOf(configItem.getDefaultValue()));
    }
    public static long getConfigAsLong(ConfigItem configItem) {
        return getConfigAsLong(configItem.getName(),Long.valueOf(configItem.getDefaultValue()));
    }
    public static boolean getConfigAsBoolean(ConfigItem configItem) {
        return getConfigAsBoolean(configItem.getName(),Boolean.valueOf(configItem.getDefaultValue()));
    }

    public static int getConfigAsInteger(String configName,int configValue){
        String value = getConfigAsString(configName,String.valueOf(configValue));
        try{
            int intValue = Integer.valueOf(value);
            return intValue;
        }
        catch (Exception e)
        {
            return configValue;
        }
    }

    public static long getConfigAsLong(String configName,long configValue){
        String value = getConfigAsString(configName,String.valueOf(configValue));
        try{
            long intValue = Long.valueOf(value);
            return intValue;
        }
        catch (Exception e)
        {
            return configValue;
        }
    }

    public static boolean getConfigAsBoolean(String configName,boolean configValue) {

        String value = getConfigAsString(configName, String.valueOf(configValue));
        try {
            boolean intValue = Boolean.valueOf(value);
            return intValue;
        } catch (Exception e) {
            return configValue;
        }
    }


    public static void refreshConfig(String configName)
    {
        keyService.刷新配置缓存(configName,KeyType.配置缓存);
    }



}
