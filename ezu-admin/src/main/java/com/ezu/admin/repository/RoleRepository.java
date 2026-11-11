package com.ezu.admin.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.admin.domain.entity.Role;
import com.ezu.admin.domain.req.RoleQueryReq;
import com.ezu.admin.repository.mapper.RoleMapper;
import org.springframework.stereotype.Repository;

/**
 * 后台角色数据访问
 */
@Repository
public class RoleRepository extends ServiceImpl<RoleMapper, Role> {

    /**
     * 按角色编码查询
     */
    public Role getByCode(String code) {
        return this.lambdaQuery().eq(Role::getCode, code).one();
    }

    /**
     * 分页查询（pageSize=-1 查全量）
     */
    public IPage<Role> queryPage(RoleQueryReq req) {
        Page<Role> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .like(StrUtil.isNotBlank(req.getName()), Role::getName, req.getName())
                .eq(req.getStatus() != null, Role::getStatus, req.getStatus())
                .orderByDesc(Role::getCreateTime)
                .orderByDesc(Role::getId)
                .page(page);
    }
}
