-- ============================================================
-- 易租（ezu）支付域建表脚本
-- 表：t_payment_record（支付记录）
-- 说明：支付渠道抽象（PaymentChannel 接口）的落库层；
--       一期 MockChannel（channel=1 模拟），二期加支付宝/微信实现不改表结构
-- ============================================================

DROP TABLE IF EXISTS `t_payment_record`;
CREATE TABLE `t_payment_record` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `payment_no` varchar(32) NOT NULL COMMENT '支付单号（P+yyyyMMdd+4位）',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `bill_no` varchar(32) DEFAULT NULL COMMENT '关联账单号（biz_type=1租金时填写）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `biz_type` tinyint NOT NULL COMMENT '业务类型（1=租金，2=押金冻结）',
  `amount` decimal(12,2) NOT NULL COMMENT '金额（元）',
  `channel` tinyint NOT NULL DEFAULT 1 COMMENT '支付渠道（1=模拟，2=支付宝，3=微信）【2/3预留】',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=待支付，2=成功，3=失败，4=已退款）',
  `channel_trade_no` varchar(64) DEFAULT NULL COMMENT '渠道交易号（模拟渠道为空）',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付记录表（渠道无关，含模拟渠道）';
