package com.union.app.util.env;

import org.springframework.util.StringUtils;

import java.io.File;

public class EnvUtil {

    private static final String APP_HOME = "APP_HOME";
    private static final String CONF_DIR_FLOADER = "conf";
    private static final String DATA_JAR_DIR_FLOADER = "data/jar";
    private static final String CONFIG_CORE_FILE = "application.properties";

    public static File appWorkHome()
    {

        String  appHome = !StringUtils.isEmpty(System.getenv(APP_HOME)) ? System.getenv(APP_HOME):System.getProperty(APP_HOME);
        if(StringUtils.isEmpty(appHome))
        {
            System.out.println("no set app_home");
            System.exit(0);
        }
        File workHome = new File(appHome);
        if(!workHome.exists() || !workHome.isDirectory())
        {
            System.out.println("app_home path is not exist or is not dir");
            System.exit(0);
        }
        return workHome;
    }


    public static File appConfDir()
    {
        File confDir = new File(appWorkHome(),CONF_DIR_FLOADER);
        if(!confDir.exists() || !confDir.isDirectory())
        {
            System.out.println("there is no conf dir");
            System.exit(0);
        }
        return confDir;
    }


    public static File appCoreConfigFile()
    {
        File coreFile = new File(appConfDir(),CONFIG_CORE_FILE);
        if(!coreFile.exists() || coreFile.isDirectory())
        {
            System.out.println("there is no application.properties file in conf dir");
            System.exit(0);
        }
        return coreFile;
    }


    public static File apiJarWorkHome() {

        File jarDir = new File(appWorkHome(),DATA_JAR_DIR_FLOADER);
        if(!jarDir.exists() || !jarDir.isDirectory())
        {
            jarDir.mkdirs();
        }
        return jarDir;
    }
}
