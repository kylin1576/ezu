package com.ezu.user.domainService;

import com.ezu.common.page.PageVo;
import com.ezu.user.domain.req.UserQueryReq;
import com.ezu.user.domain.vo.UserListVo;
import com.ezu.user.domain.vo.UserVo;

/**
 * C端用户领域服务
 */
public interface UserDomainService {

    /**
     * 查询用户信息（不存在抛业务异常）
     */
    UserVo getUserVo(Long userId);

    /**
     * 分页查询用户（B端）
     */
    PageVo<UserListVo> queryPage(UserQueryReq req);
}
