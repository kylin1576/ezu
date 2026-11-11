package com.ezu.user.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageVo;
import com.ezu.user.domain.entity.User;
import com.ezu.user.domain.req.UserQueryReq;
import com.ezu.user.domain.vo.UserListVo;
import com.ezu.user.domain.vo.UserVo;
import com.ezu.user.domainService.UserDomainService;
import com.ezu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * C端用户领域服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    public UserVo getUserVo(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new BusinessException("用户【{}】不存在", userId);
        }
        UserVo vo = new UserVo();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }

    @Override
    public PageVo<UserListVo> queryPage(UserQueryReq req) {
        IPage<User> page = userRepository.queryPage(req);
        List<UserListVo> list = page.getRecords().stream().map(user -> {
            UserListVo vo = new UserListVo();
            BeanUtil.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        // pageSize=-1 全量时 MP 跳过 count 查询，total 以实际条数回填
        long total = req.getPageSize() == -1 ? list.size() : page.getTotal();
        return new PageVo<>(total, list);
    }
}
