package com.ezu.user.application;

import com.ezu.common.page.PageQuery;
import com.ezu.common.page.PageVo;
import com.ezu.user.domain.req.AddressSaveReq;
import com.ezu.user.domain.vo.AddressVo;
import com.ezu.user.domainService.UserAddressDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户收货地址应用服务
 */
@Service
@RequiredArgsConstructor
public class UserAddressApplicationService {

    private final UserAddressDomainService userAddressDomainService;

    /**
     * 分页查询本人地址
     */
    public PageVo<AddressVo> list(Long userId, PageQuery pageQuery) {
        return userAddressDomainService.queryPage(userId, pageQuery);
    }

    /**
     * 新增/编辑地址（设默认时清其他默认，同事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(Long userId, AddressSaveReq req) {
        return userAddressDomainService.save(userId, req);
    }

    /**
     * 删除地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        userAddressDomainService.delete(userId, id);
    }
}
