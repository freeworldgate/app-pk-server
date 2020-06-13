package com.union.app.common.spring.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Controller请求和响应的日志
 * @author root
 */

@Configuration
public class ControllerConfigurerAdapter implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /*日志拦截器拦截所有请求*/
        registry.addInterceptor(new ControllerRequestLoggerInterceptor())
                .addPathPatterns("/**");





/*        // super.addInterceptors(registry);
        // 注册自定义的拦截器passwordStateInterceptor
        registry.addInterceptor(controllerRequestLoggerInterceptor)
                .addPathPatterns("/api/*") //匹配要过滤的路径
                .excludePathPatterns("/api/changePasswordByUser/*") //匹配不过滤的路径。密码还要修改呢，所以这个路径不能拦截
                .excludePathPatterns("/api/passwordStateValid") //密码状态验证也不能拦截
                .excludePathPatterns("/api/getManagerVersion");//版本信息同样不能拦截*/
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //和页面有关的静态目录都放在项目的static目录下
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //上传的图片在D盘下的OTA目录下，访问路径如：http://localhost:8081/OTA/d3cf0281-bb7f-40e0-ab77-406db95ccf2c.jpg
        //其中OTA表示访问的前缀。"file:D:/OTA/"是文件真实的存储路径
        registry.addResourceHandler("/static/**").addResourceLocations("file:C:/Users/root/Desktop/千图素材/背景图/mg");
    }


}
