<template>
  <view class="detail-page">
    <template v-if="order">
      <!-- 状态横幅 -->
      <view class="status-banner" :style="{ background: bannerBackground }">
        <text class="status-text">{{ statusText }}</text>
        <text v-if="order.cancelReason" class="status-reason">取消原因：{{ order.cancelReason }}</text>
      </view>

      <!-- 收货信息 -->
      <view class="ezu-card section-card">
        <view class="receiver-row">
          <wd-icon name="location" size="32rpx" color="#e93b3d" />
          <text class="receiver">{{ order.receiver }} {{ order.receiverPhone }}</text>
        </view>
        <text class="address-text">{{ order.receiverAddress }}</text>
      </view>

      <!-- 商品卡 -->
      <view class="ezu-card section-card">
        <view v-if="order.item" class="goods-row">
          <image v-if="order.item.skuImage" class="goods-img" :src="order.item.skuImage" mode="aspectFill" />
          <view v-else class="goods-img goods-img--placeholder">
            <wd-icon name="picture" size="40rpx" color="#dddddd" />
          </view>
          <view class="goods-info">
            <text class="goods-name ezu-ellipsis-2">{{ order.item.productName }}</text>
            <text v-if="order.item.skuSpecName" class="goods-spec">{{ order.item.skuSpecName }}</text>
            <text class="goods-deposit">押金 ¥{{ Number(order.item.depositSnapshot ?? 0).toFixed(2) }}</text>
          </view>
        </view>
      </view>

      <!-- 租约信息 -->
      <view class="ezu-card section-card">
        <text class="section-title">租约信息</text>
        <view class="row">
          <text class="row-label">计费方式</text>
          <text class="row-value">¥{{ Number(order.unitPrice ?? 0).toFixed(2) }}/{{ unitText }}/期 × {{ order.rentDays }} 天</text>
        </view>
        <view v-if="order.rentStartDate" class="row">
          <text class="row-label">起租日期</text>
          <text class="row-value">{{ order.rentStartDate }}</text>
        </view>
        <view v-if="order.rentEndDate" class="row">
          <text class="row-label">预计归还</text>
          <text class="row-value">{{ order.rentEndDate }}</text>
        </view>
      </view>

      <!-- 金额明细 -->
      <view class="ezu-card section-card">
        <text class="section-title">费用明细</text>
        <view class="row">
          <text class="row-label">租金合计</text>
          <text class="row-value">¥{{ Number(order.totalRent ?? 0).toFixed(2) }}</text>
        </view>
        <view class="row">
          <text class="row-label">押金（还机退还）</text>
          <text class="row-value">¥{{ Number(order.depositAmount ?? 0).toFixed(2) }}</text>
        </view>
        <view class="row">
          <text class="row-label">运费</text>
          <text class="row-value">{{ Number(order.freight ?? 0) === 0 ? "免运费" : `¥${Number(order.freight).toFixed(2)}` }}</text>
        </view>
        <view class="row row--total">
          <text class="row-label">应付金额</text>
          <view class="ezu-price total-price">{{ Number(order.payAmount ?? 0).toFixed(2) }}</view>
        </view>
      </view>

      <!-- 租约（确认收货后生成） -->
      <view v-if="order.lease?.startDate" class="ezu-card section-card">
        <text class="section-title">租约</text>
        <view class="row">
          <text class="row-label">租期</text>
          <text class="row-value">{{ order.lease.startDate }} ~ {{ order.lease.endDate }}</text>
        </view>
      </view>

      <!-- 押金流水 -->
      <view v-if="order.deposit?.recordNo" class="ezu-card section-card">
        <text class="section-title">押金流水</text>
        <view class="row">
          <text class="row-label">{{ order.deposit.type === 2 ? "退还" : "收取" }}</text>
          <text class="row-value">¥{{ Number(order.deposit.amount ?? 0).toFixed(2) }}</text>
        </view>
        <view v-if="order.deposit.reason" class="row">
          <text class="row-label">说明</text>
          <text class="row-value">{{ order.deposit.reason }}</text>
        </view>
      </view>

      <!-- 物流信息 -->
      <view v-if="order.logistics?.company" class="ezu-card section-card">
        <text class="section-title">物流信息</text>
        <view class="row">
          <text class="row-label">{{ order.logistics.type === 2 ? "寄回物流" : "寄出物流" }}</text>
          <text class="row-value">{{ order.logistics.company }} {{ order.logistics.logisticsNo }}</text>
        </view>
      </view>

      <!-- 订单信息 -->
      <view class="ezu-card section-card">
        <text class="section-title">订单信息</text>
        <view class="row">
          <text class="row-label">订单编号</text>
          <text class="row-value">{{ order.orderNo }}</text>
        </view>
        <view class="row">
          <text class="row-label">创建时间</text>
          <text class="row-value">{{ order.createTime }}</text>
        </view>
        <view v-if="order.remark" class="row">
          <text class="row-label">备注</text>
          <text class="row-value">{{ order.remark }}</text>
        </view>
      </view>

      <view class="bottom-blank" />

      <!-- 底部操作栏 -->
      <view v-if="actions.length" class="action-bar">
        <view class="action-btns">
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

    <!-- 加载/失败占位 -->
    <wd-status-tip v-else-if="loading" image="network" tip="加载中..." />
    <wd-status-tip v-else image="content" tip="订单不存在" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { orderDetail, type OrderDetailVo } from "@/api/order";
import { execOrderAction, execOrderPay } from "@/utils/order-actions";
import {
  getOrderActions,
  ORDER_ACTION_TEXT,
  ORDER_STATUS_COLOR,
  ORDER_STATUS_TEXT,
  type OrderAction,
} from "@/constants/order";
import { RENTAL_UNIT_TEXT } from "@/constants/product";

const orderNo = ref("");
const order = ref<OrderDetailVo | null>(null);
const loading = ref(true);
const submitting = ref(false);

const statusText = computed(() => ORDER_STATUS_TEXT[order.value?.status ?? 0] ?? "未知状态");
const unitText = computed(() => RENTAL_UNIT_TEXT[order.value?.leaseUnit ?? 3] ?? "月");
const actions = computed(() => getOrderActions(order.value?.status ?? 0));

/** 状态横幅底色（跟随状态色） */
const bannerBackground = computed(() => {
  const color = ORDER_STATUS_COLOR[order.value?.status ?? 0] ?? "#999999";
  return `linear-gradient(135deg, ${color}, ${color}cc)`;
});

onLoad(async (options) => {
  orderNo.value = options?.orderNo ?? "";
  if (!orderNo.value) {
    loading.value = false;
    return;
  }
  await loadDetail();
});

async function loadDetail(): Promise<void> {
  loading.value = true;
  try {
    order.value = await orderDetail(orderNo.value);
  } catch {
    order.value = null;
  } finally {
    loading.value = false;
  }
}

/** 按状态执行操作：支付走 execOrderPay，其余走共享流程（成功后重查详情；守卫防重复提交） */
function onAction(action: OrderAction): void {
  if (submitting.value) return;
  submitting.value = true;
  const run =
    action === "pay"
      ? execOrderPay(orderNo.value, Number(order.value?.payAmount ?? 0), loadDetail)
      : execOrderAction(action, orderNo.value, loadDetail);
  void run.finally(() => {
    submitting.value = false;
  });
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding-bottom: 24rpx;
}

/* ===== 状态横幅 ===== */
.status-banner {
  padding: 40rpx 32rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.status-text {
  font-size: 40rpx;
  font-weight: 600;
  color: #ffffff;
}

.status-reason {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
}

/* ===== 区块卡 ===== */
.section-card {
  margin: 20rpx 24rpx 0;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.receiver-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.receiver {
  font-size: 28rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.address-text {
  font-size: 26rpx;
  color: $ezu-text-color-base;
  line-height: 1.5;
}

/* ===== 商品行 ===== */
.goods-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.goods-img {
  width: 160rpx;
  height: 160rpx;
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
  font-size: 28rpx;
  color: $ezu-text-color-base;
  line-height: 1.4;
}

.goods-spec {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.goods-deposit {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}

/* ===== 行 ===== */
.row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 24rpx;
}

.row-label {
  font-size: 26rpx;
  color: $ezu-text-color-secondary;
  flex-shrink: 0;
}

.row-value {
  font-size: 26rpx;
  color: $ezu-text-color-base;
  text-align: right;
}

.row--total {
  border-top: 1rpx solid $ezu-border-color-light;
  padding-top: 20rpx;
}

.total-price {
  font-size: 36rpx;
}

/* ===== 底部操作栏 ===== */
.bottom-blank {
  height: 140rpx;
}

.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffffff;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.action-btns {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
}

.action-btn {
  padding: 12rpx 40rpx;
  border-radius: 36rpx;
  border: 2rpx solid $ezu-border-color;
  font-size: 26rpx;
  color: $ezu-text-color-base;
}

.action-btn--primary {
  border-color: $ezu-color-primary;
  background-color: $ezu-color-primary;
  color: #ffffff;
}
</style>
