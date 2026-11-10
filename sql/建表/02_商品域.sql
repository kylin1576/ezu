-- ============================================================
-- 易租（ezu）商品域建表脚本
-- 表：t_category（品类）、t_product（SPU）、t_product_sku（SKU）、
--     t_rental_plan（租金方案）、t_product_param（商品参数）
-- 说明：SPU/SKU 与 SN 分离——SKU 管定价展示，SN（t_device）管实物资产；
--       SKU 可租数量 = t_device 中 sku_id 匹配且 status=1（在库）的数量，运行时统计不冗余
-- ============================================================

DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级品类ID（0=顶级，两级树：如 摄像机→微单相机）',
  `name` varchar(32) NOT NULL COMMENT '品类名称',
  `icon` varchar(500) DEFAULT NULL COMMENT '品类图标URL',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，2=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品品类表（两级树）';

DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `product_no` varchar(32) NOT NULL COMMENT '商品编号（G+yyyyMMdd+4位，如 G202611100001）',
  `category_id` bigint NOT NULL COMMENT '品类ID（取二级品类）',
  `brand` varchar(64) DEFAULT NULL COMMENT '品牌（如 佳能/苹果/惠普）',
  `name` varchar(128) NOT NULL COMMENT '商品名称（SPU，如 佳能 EOS R6 Mark II）',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '副标题/卖点',
  `main_image` varchar(500) DEFAULT NULL COMMENT '主图URL',
  `images` text COMMENT '图集（JSON数组：url字符串列表）',
  `description` text COMMENT '图文详情（富文本HTML）',
  `status` tinyint NOT NULL DEFAULT 2 COMMENT '状态（1=上架，2=下架；新建默认下架）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_no` (`product_no`),
  KEY `idx_category_status` (`category_id`, `status`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品SPU表';

DROP TABLE IF EXISTS `t_product_sku`;
CREATE TABLE `t_product_sku` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `sku_no` varchar(32) NOT NULL COMMENT 'SKU编号（S+yyyyMMdd+4位，如 S202611100001）',
  `product_id` bigint NOT NULL COMMENT '所属SPU ID',
  `spec_name` varchar(128) NOT NULL COMMENT '规格名称（如：单机身 / 24-105套机 / 16G+512G）',
  `spec_image` varchar(500) DEFAULT NULL COMMENT '规格图URL',
  `deposit` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '押金（元）',
  `buyout_price` decimal(12,2) DEFAULT NULL COMMENT '买断价（元）【预留：以租代购】',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，2=停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_no` (`sku_no`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品SKU表（规格）';

DROP TABLE IF EXISTS `t_rental_plan`;
CREATE TABLE `t_rental_plan` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `unit` tinyint NOT NULL COMMENT '计费单位（1=日，2=周，3=月，4=季）',
  `price` decimal(12,2) NOT NULL COMMENT '该周期租金（元/周期）',
  `min_days` int NOT NULL DEFAULT 1 COMMENT '起租天数（该方案最少租期，如日租3天起租）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，2=停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='SKU租金方案表（一SKU多方案）';

DROP TABLE IF EXISTS `t_product_param`;
CREATE TABLE `t_product_param` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `product_id` bigint NOT NULL COMMENT 'SPU ID',
  `name` varchar(64) NOT NULL COMMENT '参数名（如：传感器尺寸）',
  `value` varchar(255) NOT NULL COMMENT '参数值（如：全画幅 2420万像素）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品参数表（键值对，挂SPU）';
