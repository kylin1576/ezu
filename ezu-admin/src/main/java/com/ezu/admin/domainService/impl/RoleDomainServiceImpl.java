package com.ezu.admin.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.admin.domain.entity.Role;
import com.ezu.admin.domain.req.RoleQueryReq;
import com.ezu.admin.domain.req.RoleSaveReq;
import com.ezu.admin.domain.vo.RoleVo;
import com.ezu.admin.domainService.RoleDomainService;
import com.ezu.admin.repository.RoleRepository;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台角色领域服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleDomainServiceImpl implements RoleDomainService {

    private final RoleRepository roleRepository;

    @Override
    public PageVo<RoleVo> queryPage(RoleQueryReq req) {
        IPage<Role> page = roleRepository.queryPage(req);
        List<RoleVo> list = page.getRecords().stream().map(role -> {
            RoleVo vo = new RoleVo();
            BeanUtil.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageVo<>(page.getTotal(), list);
    }

    @Override
    public Long save(RoleSaveReq req) {
        Role sameCode = roleRepository.getByCode(req.getCode());
        if (req.getId() == null) {
            if (sameCode != null) {
                throw new BusinessException("角色编码【{}】已存在", req.getCode());
            }
            Role role = new Role();
            BeanUtil.copyProperties(req, role);
            roleRepository.save(role);
            return role.getId();
        }
        if (roleRepository.getById(req.getId()) == null) {
            throw new BusinessException("角色不存在或已删除");
        }
        if (sameCode != null && !sameCode.getId().equals(req.getId())) {
            throw new BusinessException("角色编码【{}】已存在", req.getCode());
        }
        Role role = new Role();
        BeanUtil.copyProperties(req, role);
        roleRepository.updateById(role);
        return req.getId();
    }
}
