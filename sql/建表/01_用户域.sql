-- ============================================================
-- 易租（ezu）用户域建表脚本
-- 表：t_user（C端用户）、t_user_address（收货地址）
-- 约定：雪花ID主键 / 逻辑删除0=未删1=已删 / 金额decimal(12,2)
-- ============================================================

DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号（绑定后可跨微信/支付宝两端合并账号）',
  `wx_openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
  `ali_openid` varchar(64) DEFAULT NULL COMMENT '支付宝openid',
  `real_name` varchar(64) DEFAULT NULL COMMENT '实名姓名【预留：信用免押二期】',
  `id_card_no` varchar(32) DEFAULT NULL COMMENT '身份证号【预留：信用免押二期；上线前需加密存储】',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '用户状态（1=正常，2=禁用，3=黑名单）',
  `credit_status` tinyint NOT NULL DEFAULT 0 COMMENT '信用状态（0=未开通，1=已开通免押）【预留：支付宝芝麻信用】',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_wx_openid` (`wx_openid`),
  UNIQUE KEY `uk_ali_openid` (`ali_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='C端用户表';

DROP TABLE IF EXISTS `t_user_address`;
CREATE TABLE `t_user_address` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver` varchar(32) NOT NULL COMMENT '收货人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(32) NOT NULL COMMENT '省',
  `city` varchar(32) NOT NULL COMMENT '市',
  `district` varchar(32) DEFAULT NULL COMMENT '区',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '默认地址（0=否，1=是；每用户至多一条默认）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户收货地址表';
