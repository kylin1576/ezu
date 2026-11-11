package com.ezu.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 全局插件配置：MybatisPlusInterceptor（含分页插件）为全局唯一 bean，
 * 统一在 common 注册，各业务模块（ezu-admin/ezu-product/...）禁止重复注册，否则启动期 bean 冲突
 */
@Configuration
public class MybatisPlusInterceptorConfig {

    /**
     * 分页插件：未注册时 Page 参数不生效（查全量且 total=0）
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
