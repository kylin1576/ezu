<template>
  <view class="goods-card" @tap="goDetail">
    <!-- 主图：无图渲染占位色块 -->
    <image
      v-if="image"
      class="goods-img"
      :src="image"
      mode="aspectFill"
      lazy-load
    />
    <view v-else class="goods-img goods-img--placeholder">
      <wd-icon name="picture" size="64rpx" color="#dddddd" />
    </view>

    <view class="goods-info">
      <text class="goods-name ezu-ellipsis-2">{{ name }}</text>
      <text v-if="subtitle" class="goods-subtitle ezu-ellipsis">{{ subtitle }}</text>
      <view class="goods-bottom">
        <view class="ezu-price goods-price">{{ price.toFixed(2) }}</view>
        <text class="goods-unit">/{{ unitText }}起</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RENTAL_UNIT_TEXT } from "@/constants/product";

const props = withDefaults(
  defineProps<{
    /** 商品 SPU id */
    id: number;
    name: string;
    subtitle?: string;
    /** 主图 URL；空渲染占位 */
    image?: string;
    /** 展示价（最低租金） */
    price: number;
    /** 计费单位（1/2/3/4） */
    unit?: number;
  }>(),
  { subtitle: "", image: "", unit: 3 },
);

const unitText = computed(() => RENTAL_UNIT_TEXT[props.unit] ?? "月");

function goDetail(): void {
  uni.navigateTo({ url: `/pages/goods/detail?id=${props.id}` });
}
</script>

<style lang="scss" scoped>
.goods-card {
  background-color: #ffffff;
  border-radius: $ezu-radius-lg;
  overflow: hidden;
}

.goods-img {
  width: 100%;
  height: 320rpx;
  display: block;
}

.goods-img--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $ezu-bg-color-grey;
}

.goods-info {
  padding: 16rpx 20rpx 20rpx;
}

.goods-name {
  font-size: 26rpx;
  color: $ezu-text-color-base;
  line-height: 1.4;
  height: 74rpx;
}

.goods-subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: $ezu-text-color-hint;
}

.goods-bottom {
  margin-top: 12rpx;
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.goods-price {
  font-size: 34rpx;
}

.goods-unit {
  font-size: 22rpx;
  color: $ezu-text-color-hint;
}
</style>
