package com.ezu.product.util;

import com.ezu.product.domain.enums.RentalUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 租金试算工具（C端展示与下单试算共用）
 */
public final class RentalCalculator {

    private RentalCalculator() {
    }

    /**
     * 总租金 = 单周期价格 × 周期数（周期数 = ceil(租期天数 / 单位天数)，不足一个周期按一个周期计），保留2位 HALF_UP
     *
     * @param unit     计费单位（1=日，2=周，3=月，4=季）
     * @param price    单周期租金（元）
     * @param rentDays 租期天数
     */
    public static BigDecimal calcTotalRent(int unit, BigDecimal price, int rentDays) {
        int unitDays = unitDays(unit);
        int periods = (rentDays + unitDays - 1) / unitDays;
        return price.multiply(BigDecimal.valueOf(periods)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 单位 → 天数（日=1，周=7，月=30，季=90）
     */
    private static int unitDays(int unit) {
        switch (unit) {
            case RentalUnit.DAY:
                return 1;
            case RentalUnit.WEEK:
                return 7;
            case RentalUnit.MONTH:
                return 30;
            case RentalUnit.QUARTER:
                return 90;
            default:
                throw new IllegalArgumentException("未知计费单位: " + unit);
        }
    }
}
