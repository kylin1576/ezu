<template>
  <view class="placeholder">
    <wd-status-tip image="search" tip="功能开发中" />
    <text class="placeholder-note">商品列表 · M3c 商品接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
import { onLoad } from "@dcloudio/uni-app";

// 入口：首页搜索框 / 首页金刚区 / 分类页二级分类
// TODO(M3c): 接 /api/app/goods/list——分页 + 关键字/分类过滤，2 列商品卡复用首页样式
onLoad((options) => {
  console.info("goods/list query:", options?.category, options?.sub);
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
