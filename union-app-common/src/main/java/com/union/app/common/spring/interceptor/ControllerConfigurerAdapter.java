package com.union.app.common.spring.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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



}
