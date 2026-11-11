<template>
  <view class="order-card ezu-card" @tap="goDetail">
    <!-- 头部：单号 + 状态 -->
    <view class="card-head">
      <text class="order-no">{{ order.orderNo }}</text>
      <text class="order-status" :style="{ color: statusColor }">{{ statusText }}</text>
    </view>

    <!-- 商品行（OrderListVo.item 可选，防御渲染） -->
    <view v-if="order.item" class="goods-row">
      <image v-if="order.item.skuImage" class="goods-img" :src="order.item.skuImage" mode="aspectFill" />
      <view v-else class="goods-img goods-img--placeholder">
        <wd-icon name="picture" size="40rpx" color="#dddddd" />
      </view>
      <view class="goods-info">
        <text class="goods-name ezu-ellipsis-2">{{ order.item.productName }}</text>
        <text v-if="order.item.skuSpecName" class="goods-spec">{{ order.item.skuSpecName }}</text>
      </view>
      <view class="goods-amount">
        <view class="ezu-price amount">{{ payAmountText }}</view>
        <text class="amount-label">应付</text>
      </view>
    </view>

    <!-- 租期行 -->
    <view class="lease-row">
      <text class="lease-text">租期 {{ order.rentDays }} 天</text>
      <text v-if="order.createTime" class="lease-time">{{ order.createTime }}</text>
    </view>

    <!-- 操作按钮（事件上抛页面处理） -->
    <view v-if="actions.length" class="action-row" @tap.stop>
      <view
        v-for="action in actions"
        :key="action"
        class="action-btn"
        :class="{ 'action-btn--primary': action === 'pay' }"
        @tap="onAction(action)"
      >
        {{ ORDER_ACTION_TEXT[action] }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { OrderListVo } from "@/api/order";
import { getOrderActions, ORDER_ACTION_TEXT, ORDER_STATUS_COLOR, ORDER_STATUS_TEXT, type OrderAction } from "@/constants/order";

const props = defineProps<{ order: OrderListVo }>();

const emit = defineEmits<{
  /** 操作按钮点击（pay/cancel/confirmReceive/returnApply），处理流程由页面实现 */
  (e: "action", action: OrderAction, orderNo: string): void;
}>();

const statusText = computed(() => ORDER_STATUS_TEXT[props.order.status] ?? "未知状态");
const statusColor = computed(() => ORDER_STATUS_COLOR[props.order.status] ?? "#999999");
const payAmountText = computed(() => Number(props.order.payAmount ?? 0).toFixed(2));
const actions = computed(() => getOrderActions(props.order.status));

function goDetail(): void {
  uni.navigateTo({ url: `/pages/order/detail?orderNo=${props.order.orderNo}` });
}

function onAction(action: OrderAction): void {
  emit("action", action, props.order.orderNo);
}
</script>

<style lang="scss" scoped>
.order-card {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

/* ===== 头部 ===== */
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.order-no {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.order-status {
  font-size: 26rpx;
  font-weight: 600;
}

/* ===== 商品行 ===== */
.goods-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.goods-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: $ezu-radius-md;
  flex-shrink: 0;
}

.goods-img--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $ezu-bg-color-grey;
}

.goods-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.goods-name {
  font-size: 26rpx;
  color: $ezu-text-color-base;
  line-height: 1.4;
}

.goods-spec {
  font-size: 22rpx;
  color: $ezu-text-color-hint;
}

.goods-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
  flex-shrink: 0;
}

.amount {
  font-size: 30rpx;
}

.amount-label {
  font-size: 20rpx;
  color: $ezu-text-color-hint;
}

/* ===== 租期行 ===== */
.lease-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.lease-text {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}

.lease-time {
  font-size: 22rpx;
  color: $ezu-text-color-hint;
}

/* ===== 操作按钮 ===== */
.action-row {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
}

.action-btn {
  padding: 10rpx 32rpx;
  border-radius: 32rpx;
  border: 2rpx solid $ezu-border-color;
  font-size: 24rpx;
  color: $ezu-text-color-base;
}

.action-btn--primary {
  border-color: $ezu-color-primary;
  background-color: $ezu-color-primary;
  color: #ffffff;
}
</style>
