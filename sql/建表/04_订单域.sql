-- ============================================================
-- 易租（ezu）订单域建表脚本
-- 表：t_order（订单）、t_order_item（订单明细）、t_lease（租约）、
--     t_deposit_record（押金流水）、t_bill（账单）
-- 订单状态机：
--   待支付(10) → 待发货(20) → 已发货(30) → 租赁中(40) → 归还中(50) → 验收中(60) → 已完成(70)
--   待支付(10)/待发货(20) → 已取消(80)
--   验收中(60) 验收不通过 → 定损流程【二期预留】
-- 子表用 order_no 关联主表（单号交互约定，不用数据库ID）
-- ============================================================

DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号（R+yyyyMMdd+4位，如 R202611100001）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` tinyint NOT NULL DEFAULT 10 COMMENT '订单状态（10=待支付，20=待发货，30=已发货，40=租赁中，50=归还中，60=验收中，70=已完成，80=已取消）',
  `total_rent` decimal(12,2) NOT NULL COMMENT '总租金（元）',
  `deposit_amount` decimal(12,2) NOT NULL COMMENT '押金金额（元）',
  `pay_amount` decimal(12,2) NOT NULL COMMENT '应付金额（元，一期=总租金+运费）',
  `freight` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '运费（元）',
  `lease_unit` tinyint NOT NULL COMMENT '计费单位（1=日，2=周，3=月，4=季）',
  `unit_price` decimal(12,2) NOT NULL COMMENT '单价快照（元/周期，下单时租金方案价格）',
  `rent_days` int NOT NULL COMMENT '租期天数',
  `rent_start_date` date DEFAULT NULL COMMENT '起租日期',
  `rent_end_date` date DEFAULT NULL COMMENT '预计归还日期（起租+租期；实际以租约为准）',
  `receiver` varchar(32) NOT NULL COMMENT '收货人（下单时快照）',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货电话（快照）',
  `receiver_address` varchar(300) NOT NULL COMMENT '收货地址（快照，省市区+详细）',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='租赁订单主表';

DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `device_id` bigint NOT NULL COMMENT '设备ID（锁定的SN台账行，一行一设备）',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称快照',
  `sku_spec_name` varchar(128) NOT NULL COMMENT '规格名称快照',
  `sku_image` varchar(500) DEFAULT NULL COMMENT '规格图快照URL',
  `price_snapshot` decimal(12,2) NOT NULL COMMENT '单价快照（元/周期）',
  `deposit_snapshot` decimal(12,2) NOT NULL COMMENT '押金快照（元）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表（一期单SKU单设备，仍保留明细结构）';

DROP TABLE IF EXISTS `t_lease`;
CREATE TABLE `t_lease` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '租约类型（1=初始租期，2=续租）【续租为预留功能】',
  `start_date` date NOT NULL COMMENT '本段开始日期',
  `end_date` date NOT NULL COMMENT '本段结束日期',
  `rent` decimal(12,2) NOT NULL COMMENT '本段租金（元，续租段单独计费）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='租约表（初始租期+续租段，一单多段）';

DROP TABLE IF EXISTS `t_deposit_record`;
CREATE TABLE `t_deposit_record` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `record_no` varchar(32) NOT NULL COMMENT '押金流水号（D+yyyyMMdd+4位）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(12,2) NOT NULL COMMENT '金额（元）',
  `type` tinyint NOT NULL COMMENT '操作类型（1=收取/冻结，2=退还，3=扣除）【3为定损预留】',
  `channel` tinyint NOT NULL DEFAULT 1 COMMENT '渠道（1=模拟，2=支付宝信用免押）【2预留】',
  `reason` varchar(200) DEFAULT NULL COMMENT '操作原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='押金流水表（收取/退还/扣除全程可审计）';

DROP TABLE IF EXISTS `t_bill`;
CREATE TABLE `t_bill` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `bill_no` varchar(32) NOT NULL COMMENT '账单号（B+yyyyMMdd+4位）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` tinyint NOT NULL COMMENT '账单类型（1=租金，2=续租租金，3=买断差额，4=维修费）【3/4预留】',
  `amount` decimal(12,2) NOT NULL COMMENT '金额（元）',
  `status` tinyint NOT NULL DEFAULT 2 COMMENT '状态（1=已支付，2=待支付，3=已退款）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='账单表（一期租金一次性支付一条记录；周期付二期）';
