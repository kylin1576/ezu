package com.ezu.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 业务单号生成器：前缀 + yyyyMMdd + 4位流水（Redis 自增，key 当日过期）
 * 前缀：R订单 G商品 S SKU Z设备 B账单 P支付 D押金
 */
@Component
@RequiredArgsConstructor
public class NoGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final StringRedisTemplate stringRedisTemplate;

    public String next(String prefix) {
        String date = LocalDate.now().format(FMT);
        String key = "ezu:no:" + prefix + ":" + date;
        Long seq = stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, Duration.ofHours(25));
        return prefix + date + String.format("%04d", seq);
    }
}
