package com.ezu.startup.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 全模块 Mapper 扫描统一配置（唯一入口，模块内禁止再建 @MapperScan 配置类）
 * <p>
 * 启动类在 com.ezu.startup，MP/MyBatis 自动扫描只覆盖启动类所在包的 @Mapper 接口，
 * 各业务模块 mapper 在 com.ezu.{模块}.repository.mapper 下，必须显式声明；
 * 原各模块自带 MybatisPlusConfig 因类名相同在 Spring 7 组件扫描下 Bean 名冲突（@Configuration 自定义名不生效），已合并至此。
 * 分页插件 MybatisPlusInterceptor 仍由 ezu-common（MybatisPlusInterceptorConfig）全局唯一注册。
 * M4 落地 ezu-ai mapper 时在此追加 "com.ezu.ai.repository.mapper"。
 */
@Configuration
@MapperScan({
        "com.ezu.admin.repository.mapper",
        "com.ezu.user.repository.mapper",
        "com.ezu.product.repository.mapper",
        "com.ezu.order.repository.mapper",
        "com.ezu.payment.repository.mapper"
})
public class MapperScanConfig {
}
