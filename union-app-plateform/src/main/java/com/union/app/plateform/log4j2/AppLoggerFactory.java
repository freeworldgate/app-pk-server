package com.union.app.plateform.log4j2;

//import org.apache.log4j2.Logger;

//import org.apache.logging.slf4j.Log4jMarker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 日志
 * @author root
 */
public final class AppLoggerFactory {

    private static final String LOGGER_APPENDER_NAME_RUN = "run";

    private static final String LOGGER_APPENDER_NAME_INTERFACE = "interface";

    private static final String LOGGER_APPENDER_NAME_PLATEFORM = "plateform";

    private static final String LOGGER_APPENDER_NAME_JPQL = "run";

    /**
     * Log4j2配置文件命名
     */
    private static final String LOG_CONFIG_FILE_PREFIX = "log4j2-spring.xml";

    public AppLoggerFactory(){
    }

    private static Logger getLogger(String logName){
        return LogManager.getLogger(logName);
       /* if(logger == null)
        {
            Collection<File> files = FileUtils.listFiles(EnvUtil.appConfDir(),new String[]{"xml"},true);
            File config = null;
            for(File file:files)
            {
                if(LOG_CONFIG_FILE_PREFIX.equalsIgnoreCase(file.getName()))
                {
                    config = file;
                }
            }
            if(config == null)
            {   //没有配置文件
                return LogManager.getRootLogger();
            }
            BufferedInputStream in = null;
            ConfigurationSource source = null;
            try {
                in = new BufferedInputStream(new FileInputStream(config));
                source = new ConfigurationSource(in);
                Configurator.initialize(null, source);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger = LogManager.getRootLogger();
                logger.error("do not find log4j 配置 file in work home",e);
                return logger;

            } catch (IOException e) {
                logger = LogManager.getRootLogger();
                logger.error("do not find log4j 配置 file in work home",e);
                return logger;
            }
*/
//            logger = LogManager.getLogger(logName);
//        }
//
//        return logger;
    }

    public static Logger getRunLogger(){
        return getLogger(LOGGER_APPENDER_NAME_RUN);
    }

    public static Logger getInterfaceLogger(){
        return getLogger(LOGGER_APPENDER_NAME_INTERFACE);
    }

    /**
     * 监控平台性能和告警等事件
     * @return
     */
    public static Logger getPlateformLogger(){
        return getLogger(LOGGER_APPENDER_NAME_PLATEFORM);
    }

    /**
     * 监控JPA合成JPQL语句
     * @return
     */
    public static Logger getJPQLLogger(){
        return getLogger(LOGGER_APPENDER_NAME_JPQL);
    }


    /*
    *
    *
    * 加载Log4j2配置文件
    *
    File file = new File("D:/log4j2.xml");
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
    final ConfigurationSource source = new ConfigurationSource(in);
    Configurator.initialize(null, source);
    Logger logger = LogManager.getLogger("myLogger");
    *
    *
    *
    * */






}
