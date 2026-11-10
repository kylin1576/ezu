-- ============================================================
-- 易租（ezu）AI 域建表脚本
-- 表：t_ai_knowledge_doc（知识库文档）、t_ai_conversation（会话）、
--     t_ai_message（消息）
-- 说明：向量化内容存 Spring AI VectorStore（一期 SimpleVectorStore 内存，
--       规划切 pgvector，当前无 PG 环境注释预留）；本域三表只管源数据与审计，
--       源文档在 t_ai_knowledge_doc.content，向量丢失可随时重建
-- ============================================================

DROP TABLE IF EXISTS `t_ai_knowledge_doc`;
CREATE TABLE `t_ai_knowledge_doc` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `title` varchar(128) NOT NULL COMMENT '文档标题',
  `type` tinyint NOT NULL COMMENT '文档类型（1=FAQ，2=设备文档，3=租赁规则）',
  `content` mediumtext NOT NULL COMMENT '源内容（分段向量化的数据源，向量重建依据）',
  `chunk_count` int NOT NULL DEFAULT 0 COMMENT '分段数量（向量化时更新）',
  `status` tinyint NOT NULL DEFAULT 2 COMMENT '状态（1=已向量化，2=待向量化，3=已禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI知识库文档表（源数据）';

DROP TABLE IF EXISTS `t_ai_conversation`;
CREATE TABLE `t_ai_conversation` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `conversation_id` varchar(64) NOT NULL COMMENT '会话ID（UUID，用户维度，透传给spring-ai ChatMemory）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(128) DEFAULT NULL COMMENT '会话标题（首条用户消息截取）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversation_id` (`conversation_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI会话表';

DROP TABLE IF EXISTS `t_ai_message`;
CREATE TABLE `t_ai_message` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `conversation_id` varchar(64) NOT NULL COMMENT '会话ID',
  `role` varchar(16) NOT NULL COMMENT '角色（user=用户，assistant=AI，system=系统）',
  `content` text NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=未删除，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI消息表（会话记录审计）';
