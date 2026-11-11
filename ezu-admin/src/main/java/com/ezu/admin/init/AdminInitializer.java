package com.ezu.admin.init;

import com.ezu.admin.domain.entity.AdminUser;
import com.ezu.admin.domain.entity.AdminUserRole;
import com.ezu.admin.domain.entity.Permission;
import com.ezu.admin.domain.entity.Role;
import com.ezu.admin.domain.entity.RolePermission;
import com.ezu.admin.domain.enums.AdminUserStatus;
import com.ezu.admin.domain.enums.PermissionStatus;
import com.ezu.admin.domain.enums.PermissionType;
import com.ezu.admin.domain.enums.RoleStatus;
import com.ezu.admin.repository.AdminUserRoleRepository;
import com.ezu.admin.repository.AdminUserRepository;
import com.ezu.admin.repository.PermissionRepository;
import com.ezu.admin.repository.RolePermissionRepository;
import com.ezu.admin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 后台权限域初始化（幂等：每步先查再插；失败只记日志，不阻断启动）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final AdminUserRoleRepository adminUserRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            AdminUser adminUser = initAdminUser();
            Role adminRole = initAdminRole();
            initPermissionTree();
            bindAdminUserRole(adminUser, adminRole);
            bindAdminRolePermissions(adminRole);
            log.info("后台权限数据初始化完成：账号 {} 个、角色 {} 个、权限 {} 条",
                    adminUserRepository.count(), roleRepository.count(), permissionRepository.count());
        } catch (Exception e) {
            log.error("后台权限数据初始化失败（不阻断启动）", e);
        }
    }

    /**
     * 超管账号：无 admin 账号时插入
     */
    private AdminUser initAdminUser() {
        AdminUser adminUser = adminUserRepository.getByUsername("admin");
        if (adminUser != null) {
            return adminUser;
        }
        adminUser = new AdminUser();
        adminUser.setUsername("admin");
        adminUser.setPassword(PASSWORD_ENCODER.encode("admin123"));
        adminUser.setRealName("超级管理员");
        adminUser.setStatus(AdminUserStatus.ENABLED);
        adminUserRepository.save(adminUser);
        log.info("初始化默认超管账号完成（初始口令见部署文档）");
        return adminUser;
    }

    /**
     * 超管角色：无 code=admin 角色时插入
     */
    private Role initAdminRole() {
        Role role = roleRepository.getByCode("admin");
        if (role != null) {
            return role;
        }
        role = new Role();
        role.setName("超级管理员");
        role.setCode("admin");
        role.setStatus(RoleStatus.ENABLED);
        roleRepository.save(role);
        log.info("初始化超管角色：admin");
        return role;
    }

    /**
     * 权限树：权限表为空时整树插入（目录/菜单两级 + 按钮节点）
     */
    private void initPermissionTree() {
        if (permissionRepository.count() > 0) {
            return;
        }
        // 系统管理
        Permission system = insertPermission(0L, "系统管理", PermissionType.CATALOG, null, "/system", "system", 10);
        Permission userMenu = insertPermission(system.getId(), "账号管理", PermissionType.MENU, null, "/system/user", "user", 10);
        insertPermission(userMenu.getId(), "新增/编辑账号", PermissionType.BUTTON, "user:save", null, null, 10);
        Permission roleMenu = insertPermission(system.getId(), "角色管理", PermissionType.MENU, null, "/system/role", "role", 20);
        insertPermission(roleMenu.getId(), "新增/编辑角色", PermissionType.BUTTON, "role:save", null, null, 10);
        // 商品管理
        Permission product = insertPermission(0L, "商品管理", PermissionType.CATALOG, null, "/product", "goods", 20);
        Permission categoryMenu = insertPermission(product.getId(), "品类管理", PermissionType.MENU, null, "/product/category", "tree", 10);
        insertPermission(categoryMenu.getId(), "新增/编辑品类", PermissionType.BUTTON, "category:save", null, null, 10);
        Permission spuMenu = insertPermission(product.getId(), "商品SPU", PermissionType.MENU, null, "/product/spu", "goods", 20);
        insertPermission(spuMenu.getId(), "新增/编辑商品", PermissionType.BUTTON, "spu:save", null, null, 10);
        insertPermission(spuMenu.getId(), "商品上下架", PermissionType.BUTTON, "spu:shelf", null, null, 20);
        // 库存设备
        Permission device = insertPermission(0L, "库存设备", PermissionType.CATALOG, null, "/device", "device", 30);
        Permission deviceLedger = insertPermission(device.getId(), "设备台账", PermissionType.MENU, null, "/device/list", "list", 10);
        insertPermission(deviceLedger.getId(), "设备验机", PermissionType.BUTTON, "device:check", null, null, 10);
        insertPermission(deviceLedger.getId(), "设备维修", PermissionType.BUTTON, "device:repair", null, null, 20);
        insertPermission(deviceLedger.getId(), "设备报废", PermissionType.BUTTON, "device:scrapped", null, null, 30);
        Permission inboundMenu = insertPermission(device.getId(), "采购入库", PermissionType.MENU, null, "/device/inbound", "inbound", 20);
        insertPermission(inboundMenu.getId(), "采购入库", PermissionType.BUTTON, "device:inbound", null, null, 10);
        // 订单管理
        Permission order = insertPermission(0L, "订单管理", PermissionType.CATALOG, null, "/order", "order", 40);
        Permission orderList = insertPermission(order.getId(), "订单列表", PermissionType.MENU, null, "/order/list", "list", 10);
        insertPermission(orderList.getId(), "订单发货", PermissionType.BUTTON, "order:ship", null, null, 10);
        insertPermission(orderList.getId(), "订单验收", PermissionType.BUTTON, "order:accept", null, null, 20);
        // 用户管理
        Permission member = insertPermission(0L, "用户管理", PermissionType.CATALOG, null, "/member", "member", 50);
        insertPermission(member.getId(), "用户列表", PermissionType.MENU, null, "/member/list", "list", 10);
        log.info("初始化权限树完成");
    }

    /**
     * admin 账号 ↔ admin 角色：不存在该组合才插
     */
    private void bindAdminUserRole(AdminUser adminUser, Role adminRole) {
        boolean bound = adminUserRoleRepository.listByAdminUserId(adminUser.getId()).stream()
                .anyMatch(relation -> relation.getRoleId().equals(adminRole.getId()));
        if (bound) {
            return;
        }
        AdminUserRole relation = new AdminUserRole();
        relation.setAdminUserId(adminUser.getId());
        relation.setRoleId(adminRole.getId());
        adminUserRoleRepository.save(relation);
    }

    /**
     * admin 角色 ↔ 全部权限：逐条差量补插（已存在的组合跳过）
     */
    private void bindAdminRolePermissions(Role adminRole) {
        Set<Long> boundPermissionIds = rolePermissionRepository.listByRoleId(adminRole.getId()).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());
        List<RolePermission> toInsert = permissionRepository.list().stream()
                .filter(permission -> !boundPermissionIds.contains(permission.getId()))
                .map(permission -> {
                    RolePermission relation = new RolePermission();
                    relation.setRoleId(adminRole.getId());
                    relation.setPermissionId(permission.getId());
                    return relation;
                })
                .collect(Collectors.toList());
        if (!toInsert.isEmpty()) {
            rolePermissionRepository.saveBatch(toInsert);
        }
    }

    /**
     * 插入单条权限节点并回填雪花ID
     */
    private Permission insertPermission(Long parentId, String name, int type, String permissionCode,
                                        String path, String icon, int sort) {
        Permission permission = new Permission();
        permission.setParentId(parentId);
        permission.setName(name);
        permission.setType(type);
        permission.setPermissionCode(permissionCode);
        permission.setPath(path);
        permission.setIcon(icon);
        permission.setSort(sort);
        permission.setStatus(PermissionStatus.ENABLED);
        permissionRepository.save(permission);
        return permission;
    }
}
