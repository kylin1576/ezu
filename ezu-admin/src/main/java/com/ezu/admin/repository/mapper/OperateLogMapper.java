package com.ezu.admin.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezu.admin.domain.entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台操作日志 Mapper
 */
@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLog> {
}
