<template>
  <view class="placeholder">
    <wd-status-tip image="content" tip="功能开发中" />
    <text class="placeholder-note">收货地址 · M3c 地址接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
// 入口：我的页工具栏 / 订单确认页地址选择（前置 ensureLogin）
// TODO(M3c): 接 /api/app/user/address/list（pageSize=-1 全量）+ 删除/设默认
</script>

<style lang="scss" scoped>
.placeholder {
  padding-top: 160rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
}

.placeholder-note {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}
</style>
