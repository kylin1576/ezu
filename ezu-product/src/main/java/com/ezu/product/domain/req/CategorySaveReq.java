package com.ezu.product.domain.req;

import com.ezu.product.domain.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 品类保存请求（id为空=新增，非空=编辑）
 */
@Data
public class CategorySaveReq {

    /** 品类ID（编辑必传） */
    private Long id;

    /** 父级品类ID（0=顶级；二级品类传顶级品类ID） */
    @NotNull(message = "父级品类不能为空")
    private Long parentId;

    /** 品类名称 */
    @NotBlank(message = "品类名称不能为空")
    private String name;

    /** 品类图标URL */
    private String icon;

    /** 排序（越小越靠前），不传默认0 */
    private Integer sort = 0;

    /** 状态（1=启用，2=禁用），不传默认启用 */
    private Integer status = CategoryStatus.ENABLED;
}
