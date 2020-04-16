package com.union.app;


//import com.union.app.util.env.EnvUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@EnableAutoConfiguration
@ServletComponentScan
@EnableTransactionManagement
@EnableScheduling
public class AppApplication {




    public static void main(String[] args) throws IOException
    {

        System.out.println("APP_HOME = " + System.getenv("APP_HOME"));

//
//        File applicationFile = EnvUtil.appCoreConfigFile();
//        Properties properties = new Properties();
//        FileInputStream fileInputStream = FileUtils.openInputStream(applicationFile);
//        properties.load(fileInputStream);
//        fileInputStream.close();
        SpringApplication springApplication = new SpringApplication(AppApplication.class);
//        springApplication.setDefaultProperties(properties);
        springApplication.run(args);
    }




}
