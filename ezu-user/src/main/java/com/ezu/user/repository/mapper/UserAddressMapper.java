package com.ezu.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.user.domain.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收货地址 Mapper
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
