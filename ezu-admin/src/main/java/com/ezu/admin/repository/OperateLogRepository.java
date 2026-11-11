package com.ezu.admin.repository;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.admin.domain.entity.OperateLog;
import com.ezu.admin.repository.mapper.OperateLogMapper;
import org.springframework.stereotype.Repository;

/**
 * 后台操作日志数据访问（只增）
 */
@Repository
public class OperateLogRepository extends ServiceImpl<OperateLogMapper, OperateLog> {
}
