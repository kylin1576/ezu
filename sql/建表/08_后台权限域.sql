-- ============================================================
-- 易租（ezu）后台权限域建表脚本
-- 表：t_admin_user（后台账号）、t_role（角色）、t_admin_user_role（账号角色）、
--     t_permission（权限：目录/菜单/按钮树）、t_role_permission（角色权限）、
--     t_operate_log（操作日志）
-- 说明：标准 RBAC；操作日志表只增不改，仅保留 create_time（不适用逻辑删除）
-- ============================================================

DROP TABLE IF EXISTS `t_admin_user`;
CREATE TABLE `t_admin_user` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `username` varchar(32) NOT NULL COMMENT '登录账号',
  `password` varchar(128) NOT NULL COMMENT '密码（bcrypt加密存储）',
  `real_name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，2=禁用）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台管理账号表';

DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `name` varchar(32) NOT NULL COMMENT '角色名称（如 超级管理员/运营）',
  `code` varchar(32) NOT NULL COMMENT '角色编码（如 admin/operator）',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，2=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台角色表';

DROP TABLE IF EXISTS `t_admin_user_role`;
CREATE TABLE `t_admin_user_role` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `admin_user_id` bigint NOT NULL COMMENT '后台账号ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台账号角色关联表';

DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级权限ID（0=顶级）',
  `name` varchar(32) NOT NULL COMMENT '权限名称（如 商品管理/SKU新增）',
  `type` tinyint NOT NULL COMMENT '类型（1=目录，2=菜单，3=按钮）',
  `permission_code` varchar(64) DEFAULT NULL COMMENT '权限编码（如 product:sku:add；按钮/接口鉴权必填，目录菜单可空）',
  `path` varchar(128) DEFAULT NULL COMMENT '前端路由路径（目录/菜单填）',
  `icon` varchar(64) DEFAULT NULL COMMENT '菜单图标',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，2=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台权限表（目录/菜单/按钮树）';

DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

DROP TABLE IF EXISTS `t_operate_log`;
CREATE TABLE `t_operate_log` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `admin_user_id` bigint DEFAULT NULL COMMENT '操作人ID（定时任务等场景为空）',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人姓名（冗余，展示用）',
  `module` varchar(32) NOT NULL COMMENT '业务模块（如 商品管理）',
  `operation` varchar(64) NOT NULL COMMENT '操作（如 新增SKU）',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法（类#方法）',
  `params` text COMMENT '请求参数（JSON）',
  `ip` varchar(64) DEFAULT NULL COMMENT '操作IP',
  `cost_ms` int DEFAULT NULL COMMENT '耗时（毫秒）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_user_id` (`admin_user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台操作日志表（只增不改，无逻辑删除）';
