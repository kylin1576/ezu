<template>
  <view class="confirm-page">
    <!-- 收货地址卡 -->
    <view class="ezu-card address-card" @tap="chooseAddress">
      <template v-if="address">
        <view class="address-head">
          <text class="address-receiver">{{ address.receiver }}</text>
          <text class="address-phone">{{ address.phone }}</text>
          <view v-if="address.isDefault === 1" class="default-tag">默认</view>
        </view>
        <text class="address-text">
          {{ address.province }}{{ address.city }}{{ address.district ?? "" }}{{ address.detailAddress }}
        </text>
      </template>
      <view v-else class="address-empty">
        <wd-icon name="location" size="40rpx" color="#e93b3d" />
        <text class="address-empty-text">{{ addressLoading ? "地址加载中..." : "请选择收货地址" }}</text>
      </view>
      <wd-icon name="chevron-right" size="32rpx" color="#cccccc" />
    </view>

    <!-- 租期摘要 -->
    <view class="ezu-card section-card">
      <text class="section-title">租期信息</text>
      <view v-if="goodsName" class="row">
        <text class="row-label">商品</text>
        <text class="row-value">{{ goodsName }}</text>
      </view>
      <view class="row">
        <text class="row-label">租期</text>
        <text class="row-value">{{ rentDays }} 天</text>
      </view>
      <view v-if="trial" class="row">
        <text class="row-label">计费</text>
        <text class="row-value">¥{{ trial.unitPrice.toFixed(2) }}/{{ unitText }}/期 × {{ cycles }} 期</text>
      </view>
    </view>

    <!-- 金额试算 -->
    <view class="ezu-card section-card">
      <text class="section-title">费用明细</text>
      <template v-if="trial">
        <view class="row">
          <text class="row-label">租金合计</text>
          <text class="row-value">¥{{ trial.totalRent.toFixed(2) }}</text>
        </view>
        <view class="row">
          <text class="row-label">押金（还机退还）</text>
          <text class="row-value">¥{{ trial.deposit.toFixed(2) }}</text>
        </view>
        <view class="row">
          <text class="row-label">运费</text>
          <text class="row-value">{{ trial.freight.toFixed(2) === "0.00" ? "免运费" : `¥${trial.freight.toFixed(2)}` }}</text>
        </view>
      </template>
      <view v-else class="trial-hint">
        <text class="trial-hint-text">{{ trialLoading ? "金额试算中..." : "试算失败，请返回重试" }}</text>
      </view>
    </view>

    <!-- 备注 -->
    <view class="ezu-card section-card">
      <text class="section-title">订单备注</text>
      <wd-textarea
        v-model="remark"
        placeholder="选填，给商家留言（50 字内）"
        :maxlength="50"
        show-word-limit
        no-border
      />
    </view>

    <view class="bottom-blank" />

    <!-- 底部提交栏 -->
    <view class="action-bar">
      <view class="action-left">
        <text class="action-label">应付</text>
        <view class="ezu-price action-price">{{ trial ? trial.payAmount.toFixed(2) : "--" }}</view>
      </view>
      <wd-button
        custom-class="submit-btn"
        :loading="submitting"
        :disabled="!canSubmit"
        @click="handleSubmit"
      >
        提交订单
      </wd-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onUnmounted, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { addressList, type AddressVo } from "@/api/address";
import { orderCreate, orderTrial, type OrderTrialVo } from "@/api/order";
import { RENTAL_UNIT_TEXT, UNIT_DAYS } from "@/constants/product";
import { EVENTS } from "@/config";

const skuId = ref(0);
const planId = ref(0);
const rentDays = ref(1);
/** 计费单位（详情页方案透传，用于周期数展示） */
const leaseUnit = ref(3);
const goodsName = ref("");
const remark = ref("");

const address = ref<AddressVo | null>(null);
const addressLoading = ref(true);
const trial = ref<OrderTrialVo | null>(null);
const trialLoading = ref(true);
const submitting = ref(false);
/** 已成功提交（redirect 跳转前的 600ms 窗口内保持按钮禁用，防重复下单） */
const submitted = ref(false);

/** 计费周期数（向上取整，与后端 RentalCalculator 口径一致） */
const cycles = computed(() =>
  Math.max(1, Math.ceil(rentDays.value / (UNIT_DAYS[leaseUnit.value] ?? 1))),
);

const unitText = computed(() => RENTAL_UNIT_TEXT[leaseUnit.value] ?? "月");

const canSubmit = computed(() =>
  Boolean(address.value && trial.value && !submitting.value && !submitted.value),
);

onLoad((options) => {
  skuId.value = options?.skuId ? Number(options.skuId) : 0;
  planId.value = options?.planId ? Number(options.planId) : 0;
  rentDays.value = options?.days ? Number(options.days) : 1;
  if (options?.unit) leaseUnit.value = Number(options.unit);
  if (options?.goodsName) goodsName.value = decodeURIComponent(options.goodsName);
  if (!skuId.value || !planId.value) {
    uni.showToast({ title: "参数缺失，请从商品详情进入", icon: "none" });
    setTimeout(() => uni.navigateBack(), 800);
    return;
  }
  loadAddress();
  loadTrial();
  // 地址列表 select 模式回传
  uni.$on(EVENTS.ADDRESS_SELECTED, onAddressSelected);
});

onUnmounted(() => {
  uni.$off(EVENTS.ADDRESS_SELECTED, onAddressSelected);
});

function onAddressSelected(vo: AddressVo): void {
  address.value = vo;
}

async function loadAddress(): Promise<void> {
  addressLoading.value = true;
  try {
    const page = await addressList({ pageNum: 1, pageSize: -1 });
    const list = page?.list ?? [];
    // 默认地址优先，否则第一笔
    address.value = list.find((item) => item.isDefault === 1) ?? list[0] ?? null;
  } catch {
    // 请求层已 toast（401 自动跳登录）
  } finally {
    addressLoading.value = false;
  }
}

async function loadTrial(): Promise<void> {
  trialLoading.value = true;
  try {
    trial.value = await orderTrial({
      skuId: skuId.value,
      rentalPlanId: planId.value,
      rentDays: rentDays.value,
    });
  } catch {
    trial.value = null;
  } finally {
    trialLoading.value = false;
  }
}

function chooseAddress(): void {
  uni.navigateTo({ url: "/pages/address/list?select=1" });
}

async function handleSubmit(): Promise<void> {
  if (!canSubmit.value || !address.value) return;
  submitting.value = true;
  try {
    const result = await orderCreate({
      skuId: skuId.value,
      rentalPlanId: planId.value,
      rentDays: rentDays.value,
      addressId: address.value.id,
      remark: remark.value.trim() || undefined,
    });
    // 先锁死提交态再 toast/跳转，防 redirect 前的重复点击窗口
    submitted.value = true;
    uni.showToast({ title: "下单成功", icon: "success" });
    // 下单成功 → 订单详情（redirectTo 防回退回确认页重复下单）
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/order/detail?orderNo=${result.orderNo}` });
    }, 600);
  } catch {
    // 请求层已 toast
  } finally {
    submitting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.confirm-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding: 24rpx 24rpx 0;
}

/* ===== 地址卡 ===== */
.address-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx 24rpx;
}

.address-head {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.address-receiver {
  font-size: 30rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.address-phone {
  font-size: 26rpx;
  color: $ezu-text-color-secondary;
}

.default-tag {
  padding: 2rpx 12rpx;
  border-radius: 6rpx;
  background-color: $ezu-color-primary-light;
  color: $ezu-color-primary;
  font-size: 20rpx;
}

.address-text {
  flex: 1;
  font-size: 26rpx;
  color: $ezu-text-color-base;
  line-height: 1.5;
}

.address-empty {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.address-empty-text {
  font-size: 28rpx;
  color: $ezu-text-color-base;
}

/* ===== 区块卡 ===== */
.section-card {
  margin-top: 20rpx;
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

.trial-hint {
  padding: 8rpx 0;
}

.trial-hint-text {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

/* ===== 底部提交栏 ===== */
.bottom-blank {
  height: 160rpx;
}

.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.action-left {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.action-label {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}

.action-price {
  font-size: 40rpx;
}

:deep(.submit-btn) {
  --wot-button-radius: 44rpx;
  padding: 0 64rpx;
}
</style>
