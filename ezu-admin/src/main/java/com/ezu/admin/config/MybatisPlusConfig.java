package com.ezu.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置（本模块内生效的 mapper 扫描 + 全局分页插件）
 * <p>
 * MapperScan 必须显式声明：启动类在 com.ezu.startup，MP/MyBatis 的自动扫描只覆盖启动类所在包的 @Mapper 接口，
 * 本模块 mapper 在 com.ezu.admin.repository.mapper 下，不扫则 Repository 注入 baseMapper 失败
 */
@Configuration
@MapperScan("com.ezu.admin.repository.mapper")
public class MybatisPlusConfig {

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
