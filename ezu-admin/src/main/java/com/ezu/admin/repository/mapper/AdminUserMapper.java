package com.ezu.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.admin.domain.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台账号 Mapper
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
