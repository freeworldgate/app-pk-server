package com.union.app.dao.jpa;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(
        // basePackages 支持多包扫描，用文本数组的形式就可以
        // 比如这样 {"com.simply.zuozuo.repo","com.simply.zuozuo.mapper"}
        basePackages = {
                "com.union.app"
        },
        value = {},
        // 指定里面的存储库类
        basePackageClasses = {}
)
public class JPAConfig {


}
