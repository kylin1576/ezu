package com.ezu.product.domain.enums;

/**
 * 设备状态（状态机见 03_设备资产域.sql：在库→锁定→在租→归还验收中→在库；在租→维修→在库；在租/维修→报废）
 */
public interface DeviceStatus {

    /** 在库（可租） */
    int IN_STOCK = 1;

    /** 锁定（下单预占） */
    int LOCKED = 2;

    /** 在租 */
    int RENTED = 3;

    /** 维修 */
    int REPAIRING = 4;

    /** 报废（终态） */
    int SCRAPPED = 5;

    /** 归还验收中 */
    int RETURN_CHECKING = 6;
}
