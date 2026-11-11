package com.ezu.product.domain.req;

import com.ezu.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备SN台账列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceQueryReq extends PageQuery {

    /** SKU ID */
    private Long skuId;

    /**
     * 设备状态（1=在库，2=锁定，3=在租，4=维修，5=报废，6=归还验收中）
     * <p>
     * {@link com.ezu.product.domain.enums.DeviceStatus}
     */
    private Integer status;

    /** 关键字（模糊匹配 SN/设备编号） */
    private String keyword;
}
