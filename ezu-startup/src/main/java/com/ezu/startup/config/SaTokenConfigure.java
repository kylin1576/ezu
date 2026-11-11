package com.ezu.startup.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import com.ezu.common.satoken.StpAdminUtil;
import com.ezu.common.satoken.StpUserUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截配置：C 端 /api/app/** 校验 user 账号，B 端 /api/admin/** 校验 admin 账号，白名单放行
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    SaRouter.match("/api/app/**")
                            .notMatch("/api/app/auth/**")
                            .check(r -> StpUserUtil.checkLogin());
                    SaRouter.match("/api/admin/**")
                            .notMatch("/api/admin/auth/**")
                            .check(r -> StpAdminUtil.checkLogin());
                }))
                .addPathPatterns("/api/**");
    }
}
