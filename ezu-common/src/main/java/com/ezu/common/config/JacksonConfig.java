package com.ezu.common.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 雪花ID（19位）超过 JS Number.MAX_SAFE_INTEGER（2^53），前端解析会精度丢失；
 * 全局将 Long 序列化为 String，前端按字符串处理。反序列化 Jackson 默认可接受数字字符串，无需额外配置。
 * <p>
 * Spring Boot 4.x 默认 Jackson 3（tools.jackson），定制点为 JsonMapperBuilderCustomizer
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> builder.addModule(new SimpleModule()
                .addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(Long.TYPE, ToStringSerializer.instance));
    }
}
