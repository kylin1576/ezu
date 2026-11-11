package com.ezu.user.domainService.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ezu.common.exception.BusinessException;
import com.ezu.common.page.PageQuery;
import com.ezu.common.page.PageVo;
import com.ezu.user.domain.entity.UserAddress;
import com.ezu.user.domain.req.AddressSaveReq;
import com.ezu.user.domain.vo.AddressVo;
import com.ezu.user.domainService.UserAddressDomainService;
import com.ezu.user.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收货地址领域服务实现
 */
@Service
@RequiredArgsConstructor
public class UserAddressDomainServiceImpl implements UserAddressDomainService {

    /** is_default 列值：是 */
    private static final int DEFAULT_YES = 1;

    private final UserAddressRepository userAddressRepository;

    @Override
    public PageVo<AddressVo> queryPage(Long userId, PageQuery pageQuery) {
        IPage<UserAddress> page = userAddressRepository.queryPage(userId, pageQuery);
        List<AddressVo> list = page.getRecords().stream().map(address -> {
            AddressVo vo = new AddressVo();
            BeanUtil.copyProperties(address, vo);
            return vo;
        }).collect(Collectors.toList());
        // pageSize=-1 全量时 MP 跳过 count 查询，total 以实际条数回填
        long total = pageQuery.getPageSize() == -1 ? list.size() : page.getTotal();
        return new PageVo<>(total, list);
    }

    @Override
    public Long save(Long userId, AddressSaveReq req) {
        if (req.getId() != null) {
            checkOwnership(userId, req.getId());
        }
        // 设为默认前先清掉本人已有默认标记（含正被编辑的这条），随后插/更落回唯一默认
        if (req.getIsDefault() != null && req.getIsDefault() == DEFAULT_YES) {
            userAddressRepository.clearDefault(userId);
        }
        UserAddress address = new UserAddress();
        BeanUtil.copyProperties(req, address);
        address.setUserId(userId);
        if (req.getId() != null) {
            userAddressRepository.updateById(address);
        } else {
            userAddressRepository.save(address);
        }
        return address.getId();
    }

    @Override
    public void delete(Long userId, Long id) {
        checkOwnership(userId, id);
        userAddressRepository.removeById(id);
    }

    /**
     * 归属校验：地址必须存在且属于当前用户
     */
    private void checkOwnership(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址【{}】不存在或无权操作", addressId);
        }
    }
}
