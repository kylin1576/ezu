<template>
  <view class="order-page">
    <!-- 未登录软引导（不强跳） -->
    <view v-if="!userStore.isLoggedIn" class="login-guide ezu-card">
      <wd-icon name="wallet" size="80rpx" color="#dddddd" />
      <text class="login-guide-title">登录后查看订单</text>
      <text class="login-guide-sub">登录即可查看租赁订单、续租与归还进度</text>
      <wd-button size="small" custom-class="login-guide-btn" @click="goLogin">
        去登录
      </wd-button>
    </view>

    <!-- 已登录：状态 tab + 空状态 -->
    <template v-else>
      <wd-tabs v-model="activeTab">
        <wd-tab v-for="tab in tabs" :key="tab.key" :title="tab.title" :name="tab.key" />
      </wd-tabs>
      <!-- TODO(M3c): 接 /api/app/order/list，按 tab 状态过滤分页拉取 -->
      <wd-status-tip image="content" tip="暂无相关订单" />
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useUserStore } from "@/stores/user";

/**
 * 订单状态映射（与后端 t_order.status 对齐）
 * TODO(M3c): 抽到 types/order.ts 供列表/详情共用
 */
const ORDER_STATUS_MAP: Record<number, string> = {
  10: "待支付",
  20: "待发货",
  30: "配送中",
  40: "租赁中",
  50: "归还中",
  80: "已取消",
};

const ORDER_TAB = {
  ALL: "all",
  PAY: "pay",
  RENTING: "renting",
  RETURN: "return",
} as const;

interface OrderTabConfig {
  key: string;
  title: string;
  /** 对应后端订单状态；undefined 表示全部 */
  status?: number;
}

/** tab 配置由状态映射派生，保持文案单一来源 */
const tabs = computed<OrderTabConfig[]>(() => [
  { key: ORDER_TAB.ALL, title: "全部" },
  { key: ORDER_TAB.PAY, title: ORDER_STATUS_MAP[10], status: 10 },
  { key: ORDER_TAB.RENTING, title: ORDER_STATUS_MAP[40], status: 40 },
  { key: ORDER_TAB.RETURN, title: ORDER_STATUS_MAP[50], status: 50 },
]);

const userStore = useUserStore();
const activeTab = ref<string>(ORDER_TAB.ALL);

function goLogin(): void {
  uni.navigateTo({ url: "/pages/login/index" });
}
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
}

/* 未登录软引导 */
.login-guide {
  margin-top: 120rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  padding: 64rpx 24rpx;
}

.login-guide-title {
  margin-top: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: $ezu-text-color-base;
}

.login-guide-sub {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

:deep(.login-guide-btn) {
  margin-top: 24rpx;
}
</style>
