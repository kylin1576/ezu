package com.ezu.user.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.common.page.PageQuery;
import com.ezu.user.domain.entity.UserAddress;
import com.ezu.user.repository.mapper.UserAddressMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户收货地址数据访问
 */
@Repository
public class UserAddressRepository extends ServiceImpl<UserAddressMapper, UserAddress> {

    /**
     * 分页查询本人地址（pageSize=-1 查全量，默认地址在前）
     */
    public IPage<UserAddress> queryPage(Long userId, PageQuery pageQuery) {
        Page<UserAddress> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        return this.lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime)
                .orderByDesc(UserAddress::getId)
                .page(page);
    }

    /**
     * 清除本人全部默认地址标记
     */
    public void clearDefault(Long userId) {
        this.lambdaUpdate()
                .set(UserAddress::getIsDefault, 0)
                .eq(UserAddress::getUserId, userId)
                .update();
    }
}
