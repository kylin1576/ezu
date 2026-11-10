-- ============================================================
-- 易租（ezu）物流域建表脚本
-- 表：t_logistics（物流单）
-- 说明：发货（寄出）与归还（寄回）双向，一订单可多段物流记录；
--       一期快递状态由运营手动维护（已发出/已签收），对接快递100查询接口为二期可选
-- ============================================================

DROP TABLE IF EXISTS `t_logistics`;
CREATE TABLE `t_logistics` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `type` tinyint NOT NULL COMMENT '物流类型（1=发货寄出，2=归还寄回）',
  `company` varchar(64) NOT NULL COMMENT '快递公司（如 顺丰速运）',
  `tracking_no` varchar(64) NOT NULL COMMENT '快递单号',
  `fee` decimal(12,2) DEFAULT NULL COMMENT '运费（元）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=已发出，2=已签收）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_tracking_no` (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物流记录表（发货/归还双向）';
