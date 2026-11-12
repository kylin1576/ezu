package com.ezu.user.domainService.impl;

import cn.hutool.core.util.StrUtil;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.satoken.StpUserUtil;
import com.ezu.user.domain.entity.User;
import com.ezu.user.domain.enums.AuthChannel;
import com.ezu.user.domain.enums.UserStatus;
import com.ezu.user.domain.req.LoginReq;
import com.ezu.user.domain.vo.AppLoginVo;
import com.ezu.user.domainService.AuthDomainService;
import com.ezu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * C端认证领域服务实现
 */
@Service("userAuthDomainServiceImpl")
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {

    private final UserRepository userRepository;

    @Override
    public AppLoginVo mockLogin(LoginReq req) {
        // mock：不调微信/支付宝，openid 由渠道+授权码拼装，同一 code 重复登录命中同一账号
        String openid = "mock-" + req.getChannel() + "-" + req.getCode();
        User user = userRepository.getByOpenid(req.getChannel(), openid);
        boolean isNew = false;
        if (user == null) {
            user = createUser(req, openid);
            isNew = true;
        }
        if (user.getStatus() != null && user.getStatus() == UserStatus.BLACKLIST) {
            throw new BusinessException("账号已被限制");
        }
        StpUserUtil.login(user.getId());
        AppLoginVo vo = new AppLoginVo();
        vo.setToken(StpUserUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setIsNew(isNew);
        return vo;
    }

    /**
     * mock 注册新用户（昵称=租客+授权码后4位）
     */
    private User createUser(LoginReq req, String openid) {
        User user = new User();
        user.setNickname("租客" + StrUtil.sub(req.getCode(), -4, req.getCode().length()));
        user.setStatus(UserStatus.NORMAL);
        if (req.getChannel() == AuthChannel.WECHAT) {
            user.setWxOpenid(openid);
        } else {
            user.setAliOpenid(openid);
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public void logout() {
        StpUserUtil.logout();
    }
}
