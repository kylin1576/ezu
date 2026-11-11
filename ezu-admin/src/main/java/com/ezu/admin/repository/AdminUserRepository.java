package com.ezu.admin.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.req.AdminUserQueryReq;
import com.ezu.admin.repository.mapper.AdminUserMapper;
import org.springframework.stereotype.Repository;

/**
 * 后台账号数据访问
 */
@Repository
public class AdminUserRepository extends ServiceImpl<AdminUserMapper, AdminUser> {

    /**
     * 按登录账号查询
     */
    public AdminUser getByUsername(String username) {
        return this.lambdaQuery().eq(AdminUser::getUsername, username).one();
    }

    /**
     * 分页查询（pageSize=-1 查全量）
     */
    public IPage<AdminUser> queryPage(AdminUserQueryReq req) {
        Page<AdminUser> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .like(StrUtil.isNotBlank(req.getUsername()), AdminUser::getUsername, req.getUsername())
                .like(StrUtil.isNotBlank(req.getRealName()), AdminUser::getRealName, req.getRealName())
                .eq(req.getStatus() != null, AdminUser::getStatus, req.getStatus())
                .orderByDesc(AdminUser::getCreateTime)
                .orderByDesc(AdminUser::getId)
                .page(page);
    }
}
