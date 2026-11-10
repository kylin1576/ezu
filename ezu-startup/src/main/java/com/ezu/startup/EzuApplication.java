package com.ezu.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * scanBasePackages 必须显式指定 com.ezu：
 * 启动类在 com.ezu.startup 包，默认组件扫描只覆盖 startup 子包，
 * ezu-common 的 GlobalExceptionHandler / MyMetaObjectHandler / JacksonConfig 会静默不注册
 */
@SpringBootApplication(scanBasePackages = "com.ezu")
public class EzuApplication {

    public static void main(String[] args) {
        SpringApplication.run(EzuApplication.class, args);
    }
}
