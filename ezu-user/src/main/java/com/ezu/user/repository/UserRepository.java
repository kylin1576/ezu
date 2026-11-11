package com.ezu.user.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ezu.user.domain.entity.User;
import com.ezu.user.domain.enums.AuthChannel;
import com.ezu.user.domain.req.UserQueryReq;
import com.ezu.user.repository.mapper.UserMapper;
import org.springframework.stereotype.Repository;

/**
 * C端用户数据访问
 */
@Repository
public class UserRepository extends ServiceImpl<UserMapper, User> {

    /**
     * 按渠道openid查询（channel=1 查微信，channel=2 查支付宝）
     */
    public User getByOpenid(Integer channel, String openid) {
        if (channel == AuthChannel.WECHAT) {
            return this.lambdaQuery().eq(User::getWxOpenid, openid).one();
        }
        return this.lambdaQuery().eq(User::getAliOpenid, openid).one();
    }

    /**
     * 分页查询（pageSize=-1 查全量）
     */
    public IPage<User> queryPage(UserQueryReq req) {
        Page<User> page = new Page<>(req.getPageNum(), req.getPageSize());
        return this.lambdaQuery()
                .like(StrUtil.isNotBlank(req.getNickname()), User::getNickname, req.getNickname())
                .like(StrUtil.isNotBlank(req.getPhone()), User::getPhone, req.getPhone())
                .eq(req.getStatus() != null, User::getStatus, req.getStatus())
                .orderByDesc(User::getCreateTime)
                .orderByDesc(User::getId)
                .page(page);
    }
}
