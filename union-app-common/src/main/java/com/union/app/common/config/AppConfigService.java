package com.union.app.common.config;

import com.union.app.common.spring.context.SpringContextUtil;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AppConfigService
{
    private static AppDaoService appDao = SpringContextUtil.getBean(AppDaoService.class);

    private static final Map<String,String> configCache = new ConcurrentHashMap<>();

    public static String getConfigAsString(String configName,String defaultValue) {
        /*从缓存获取*/
        String cacheValue = configCache.get(configName);
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
//            configCache.put(configName,value);
            return value;
        }
        catch (Exception e)
        {
            return defaultValue;
        }
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
        configCache.remove(configName);
    }


    public static void saveInviteTime(String inviteId, long time) {

        ConfigEntity entity = new ConfigEntity();
        entity.setDefaultValue(String.valueOf(time));
        entity.setCreateTime(Calendar.getInstance().getTime());
        entity.setConfigValue(String.valueOf(time));
        entity.setConfigName(inviteId);
        entity.setColumSwitch(ColumSwitch.ON);
        entity.setConfigDesc("联谊时间");
        appDao.insertEntity(entity);
    }
}
