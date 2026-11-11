package com.ezu.admin.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.entity.AdminUserRole;
import com.ezu.admin.domain.entity.Role;
import com.ezu.admin.domain.req.AdminUserQueryReq;
import com.ezu.admin.domain.req.AdminUserSaveReq;
import com.ezu.admin.domain.vo.AdminUserListVo;
import com.ezu.admin.domainService.AdminUserDomainService;
import com.ezu.admin.repository.AdminUserRepository;
import com.ezu.admin.repository.AdminUserRoleRepository;
import com.ezu.admin.repository.RoleRepository;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 后台账号领域服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminUserDomainServiceImpl implements AdminUserDomainService {

    /** BCryptPasswordEncoder 无状态线程安全，静态复用 */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final AdminUserRepository adminUserRepository;
    private final AdminUserRoleRepository adminUserRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    public PageVo<AdminUserListVo> queryPage(AdminUserQueryReq req) {
        IPage<AdminUser> page = adminUserRepository.queryPage(req);
        List<AdminUserListVo> list = page.getRecords().stream().map(user -> {
            AdminUserListVo vo = new AdminUserListVo();
            BeanUtil.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageVo<>(page.getTotal(), list);
    }

    @Override
    public Long save(AdminUserSaveReq req) {
        AdminUser sameUsername = adminUserRepository.getByUsername(req.getUsername());
        if (req.getId() == null) {
            return insert(req, sameUsername);
        }
        return update(req, sameUsername);
    }

    private Long insert(AdminUserSaveReq req, AdminUser sameUsername) {
        if (sameUsername != null) {
            throw new BusinessException("账号【{}】已存在", req.getUsername());
        }
        if (StrUtil.isBlank(req.getPassword())) {
            throw new BusinessException("新增账号密码不能为空");
        }
        AdminUser adminUser = new AdminUser();
        BeanUtil.copyProperties(req, adminUser);
        adminUser.setPassword(PASSWORD_ENCODER.encode(req.getPassword()));
        adminUserRepository.save(adminUser);
        bindRoles(adminUser.getId(), req.getRoleIds());
        return adminUser.getId();
    }

    private Long update(AdminUserSaveReq req, AdminUser sameUsername) {
        if (adminUserRepository.getById(req.getId()) == null) {
            throw new BusinessException("账号不存在或已删除");
        }
        if (sameUsername != null && !sameUsername.getId().equals(req.getId())) {
            throw new BusinessException("账号【{}】已存在", req.getUsername());
        }
        AdminUser adminUser = new AdminUser();
        BeanUtil.copyProperties(req, adminUser);
        if (StrUtil.isNotBlank(req.getPassword())) {
            adminUser.setPassword(PASSWORD_ENCODER.encode(req.getPassword()));
        } else {
            // 编辑不传密码=不修改：置 null 让 updateById 跳过该字段
            adminUser.setPassword(null);
        }
        adminUserRepository.updateById(adminUser);
        bindRoles(req.getId(), req.getRoleIds());
        return req.getId();
    }

    /**
     * 绑定角色：先批量校验角色存在，再全删全插
     */
    private void bindRoles(Long adminUserId, List<Long> roleIds) {
        List<Long> distinctRoleIds = roleIds.stream().distinct().collect(Collectors.toList());
        checkRolesExist(distinctRoleIds);
        adminUserRoleRepository.removeByAdminUserId(adminUserId);
        if (CollectionUtil.isEmpty(distinctRoleIds)) {
            return;
        }
        List<AdminUserRole> relations = distinctRoleIds.stream().map(roleId -> {
            AdminUserRole relation = new AdminUserRole();
            relation.setAdminUserId(adminUserId);
            relation.setRoleId(roleId);
            return relation;
        }).collect(Collectors.toList());
        adminUserRoleRepository.saveBatch(relations);
    }

    /**
     * 角色存在性校验：单次批量 IN 查询，缺失即抛业务异常
     */
    private void checkRolesExist(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        Set<Long> existRoleIds = roleRepository.listByIds(roleIds).stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        roleIds.stream()
                .filter(roleId -> !existRoleIds.contains(roleId))
                .findFirst()
                .ifPresent(roleId -> {
                    throw new BusinessException("角色ID【{}】不存在", roleId);
                });
    }

    @Override
    public AdminUser getById(Long id) {
        return adminUserRepository.getById(id);
    }
}
