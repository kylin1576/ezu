<template>
  <view class="placeholder">
    <wd-status-tip image="content" tip="功能开发中" />
    <text class="placeholder-note">商品详情 · M3c 商品接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
import { onLoad } from "@dcloudio/uni-app";

// 入口：首页推荐流商品卡
// TODO(M3c): 接 /api/app/goods/{id}——轮播图 + 租期选择 + 租金方案 + 立即租赁（ensureLogin 后跳订单确认页）
onLoad((options) => {
  console.info("goods/detail id:", options?.id);
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
