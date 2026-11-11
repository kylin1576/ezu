<template>
  <view class="placeholder">
    <wd-status-tip image="search" tip="功能开发中" />
    <text class="placeholder-note">商品列表 · M3c 商品接口就绪后接入</text>
  </view>
</template>

<script setup lang="ts">
// 入口：首页搜索框 / 首页金刚区 / 分类页二级分类
// 入口参数：query 携带 category（一级分类名）/ sub（二级分类名）/ keyword（M3c 搜索）
// TODO(M3c): 接 /api/app/goods/list——分页 + 关键字/分类过滤，2 列商品卡复用首页样式
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
