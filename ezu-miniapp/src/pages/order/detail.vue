<template>
  <view class="placeholder">
    <wd-status-tip image="content" tip="功能开发中" />
    <text class="placeholder-note">订单详情 · M3c 订单接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
import { onLoad } from "@dcloudio/uni-app";

// 入口：订单列表卡片
// TODO(M3c): 接 /api/app/order/{orderNo}——状态流转时间线 + 设备信息 + 支付/续租/归还操作
onLoad((options) => {
  console.info("order/detail orderNo:", options?.orderNo);
});
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
