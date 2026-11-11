<template>
  <view class="placeholder">
    <wd-status-tip image="content" tip="功能开发中" />
    <text class="placeholder-note">编辑地址 · M3c 地址接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
import { onLoad } from "@dcloudio/uni-app";

// 入口：地址列表「新增/编辑」
// TODO(M3c): 接 /api/app/user/address/save（id?/receiver/phone/province/city/district?/detailAddress/isDefault）+ 省市区选择器
onLoad((options) => {
  console.info("address/edit id:", options?.id);
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
