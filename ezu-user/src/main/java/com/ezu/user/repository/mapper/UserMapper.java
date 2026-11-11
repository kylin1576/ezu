package com.ezu.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.user.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * C端用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
