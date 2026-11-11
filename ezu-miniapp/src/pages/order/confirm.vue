<template>
  <view class="placeholder">
    <wd-status-tip image="content" tip="功能开发中" />
    <text class="placeholder-note">确认订单 · M3c 订单接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
// 入口：商品详情「立即租赁」（前置 ensureLogin）
// TODO(M3c): 地址选择 + 租期/押金试算（/api/app/order/trial）+ 提交创建（/api/app/order/create）
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
