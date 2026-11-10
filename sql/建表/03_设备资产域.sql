-- ============================================================
-- 易租（ezu）设备资产域建表脚本
-- 表：t_device（SN设备台账）、t_device_check_report（质检/验收报告）、
--     t_device_repair（维修记录）
-- 说明：一台物理实机一行；设备状态机：
--   在库(1) → 锁定(2) → 在租(3) → 归还验收中(6) → 在库(1)
--   在租(3) → 维修(4) → 在库(1)
--   在租(3) → 报废(5)（灭失/严重损坏，终态）
--   维修(4) → 报废(5)（修不好，终态）
--   锁定(2) 订单取消 → 在库(1)
-- ============================================================

DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `device_no` varchar(32) NOT NULL COMMENT '设备资产编号（Z+yyyyMMdd+4位，平台自编，二维码标签用）',
  `sn` varchar(64) DEFAULT NULL COMMENT '厂商序列号（部分设备无SN可为空）',
  `sku_id` bigint NOT NULL COMMENT '所属SKU ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '设备状态（1=在库，2=锁定，3=在租，4=维修，5=报废，6=归还验收中）',
  `condition_level` tinyint DEFAULT NULL COMMENT '成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹）',
  `purchase_date` date DEFAULT NULL COMMENT '采购日期',
  `purchase_price` decimal(12,2) DEFAULT NULL COMMENT '采购价（元）',
  `purchase_channel` varchar(128) DEFAULT NULL COMMENT '采购渠道',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_no` (`device_no`),
  KEY `idx_sn` (`sn`),
  KEY `idx_sku_status` (`sku_id`, `status`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备SN台账表（一台实机一行）';

DROP TABLE IF EXISTS `t_device_check_report`;
CREATE TABLE `t_device_check_report` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `type` tinyint NOT NULL COMMENT '报告类型（1=入库质检，2=归还验收）',
  `order_no` varchar(32) DEFAULT NULL COMMENT '关联订单号（归还验收时填写）',
  `exterior_result` tinyint DEFAULT NULL COMMENT '外观结论（1=完好，2=轻微划痕，3=明显损伤）',
  `function_result` tinyint DEFAULT NULL COMMENT '功能结论（1=正常，2=异常）',
  `condition_level` tinyint DEFAULT NULL COMMENT '验收后成色定级（1=全新，2=准新，3=轻微使用痕迹，4=明显使用痕迹；定级后回写t_device）',
  `attachments` text COMMENT '质检照片（JSON数组：[{name,url}]）',
  `checker` varchar(64) DEFAULT NULL COMMENT '质检员',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备质检/验收报告表';

DROP TABLE IF EXISTS `t_device_repair`;
CREATE TABLE `t_device_repair` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '关联订单号（租期内报修填写，自发故障为空）',
  `fault_desc` varchar(500) NOT NULL COMMENT '故障描述',
  `repair_cost` decimal(12,2) DEFAULT NULL COMMENT '维修费用（元）',
  `cost_bearer` tinyint DEFAULT NULL COMMENT '费用承担（1=平台，2=用户）【预留：定损理赔】',
  `start_date` date DEFAULT NULL COMMENT '维修开始日期',
  `end_date` date DEFAULT NULL COMMENT '维修结束日期',
  `result` varchar(500) DEFAULT NULL COMMENT '维修结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备维修记录表';
