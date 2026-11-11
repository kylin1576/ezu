package com.ezu.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * ezu-order 模块 mapper 扫描配置
 * <p>
 * MapperScan 必须显式声明：启动类在 com.ezu.startup，MP/MyBatis 的自动扫描只覆盖启动类所在包的 @Mapper 接口，
 * 本模块 mapper 在 com.ezu.order.repository.mapper 下，不扫则 Repository 注入 baseMapper 失败。
 * 分页插件 MybatisPlusInterceptor 为全局唯一 bean，已在 ezu-common（MybatisPlusInterceptorConfig）注册，
 * 本模块禁止重复注册
 */
@Configuration
@MapperScan("com.ezu.order.repository.mapper")
public class MybatisPlusConfig {
}
