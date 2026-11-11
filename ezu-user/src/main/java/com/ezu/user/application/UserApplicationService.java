package com.ezu.user.application;

import com.ezu.common.page.PageVo;
import com.ezu.user.domain.req.UserQueryReq;
import com.ezu.user.domain.vo.UserListVo;
import com.ezu.user.domain.vo.UserVo;
import com.ezu.user.domainService.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * C端用户应用服务
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserDomainService userDomainService;

    /**
     * 查询用户信息
     */
    public UserVo info(Long userId) {
        return userDomainService.getUserVo(userId);
    }

    /**
     * B端用户列表（一期只读）
     */
    public PageVo<UserListVo> list(UserQueryReq req) {
        return userDomainService.queryPage(req);
    }
}
