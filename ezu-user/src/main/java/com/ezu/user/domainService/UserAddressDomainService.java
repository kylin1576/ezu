package com.ezu.user.domainService;

import com.ezu.common.page.PageQuery;
import com.ezu.common.page.PageVo;
import com.ezu.user.domain.req.AddressSaveReq;
import com.ezu.user.domain.vo.AddressVo;

/**
 * 用户收货地址领域服务
 */
public interface UserAddressDomainService {

    /**
     * 分页查询本人地址
     */
    PageVo<AddressVo> queryPage(Long userId, PageQuery pageQuery);

    /**
     * 保存地址（id为空=新增，非空=编辑，均校验归属）
     */
    Long save(Long userId, AddressSaveReq req);

    /**
     * 删除地址（校验归属后逻辑删）
     */
    void delete(Long userId, Long id);
}
