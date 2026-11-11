<template>
  <view class="mine-page">
    <!-- 用户卡（双态） -->
    <view class="user-card" @tap="onUserCardTap">
      <view class="avatar">
        <image
          v-if="userStore.userInfo?.avatar"
          class="avatar-img"
          :src="userStore.userInfo.avatar"
          mode="aspectFill"
        />
        <wd-icon v-else name="user-avatar" size="96rpx" color="#ffffff" />
      </view>
      <view class="user-info">
        <text class="user-name">
          {{ userStore.isLoggedIn ? userStore.userInfo?.nickname ?? "租客" : "点击登录" }}
        </text>
        <text v-if="userStore.isLoggedIn" class="user-id">
          ID: {{ userStore.userInfo?.userId ?? "-" }}
        </text>
        <text v-else class="user-hint">登录后享受更多权益</text>
      </view>
      <wd-icon name="chevron-right" size="32rpx" color="#ffffff" />
    </view>

    <!-- 订单入口横条 -->
    <view class="ezu-card order-entry">
      <view class="order-entry-header" @tap="goOrderList('all')">
        <text class="order-entry-title">我的订单</text>
        <view class="order-entry-more">
          <text class="order-entry-more-text">查看全部</text>
          <wd-icon name="chevron-right" size="28rpx" color="#999999" />
        </view>
      </view>
      <view class="order-entry-grid">
        <view
          v-for="entry in orderEntries"
          :key="entry.key"
          class="order-entry-item"
          @tap="goOrderList(entry.key)"
        >
          <wd-icon :name="entry.icon" size="52rpx" color="#333333" />
          <text class="order-entry-name">{{ entry.name }}</text>
        </view>
      </view>
    </view>

    <!-- 工具栏 -->
    <view class="ezu-card tools">
      <view class="tool-item" @tap="goAddress">
        <wd-icon name="location" size="44rpx" color="#333333" />
        <text class="tool-name">收货地址</text>
        <wd-icon name="chevron-right" size="28rpx" color="#cccccc" />
      </view>
      <view class="tool-item" @tap="contactService">
        <wd-icon name="service" size="44rpx" color="#333333" />
        <text class="tool-name">联系客服</text>
        <wd-icon name="chevron-right" size="28rpx" color="#cccccc" />
      </view>
    </view>

    <!-- 退出登录 -->
    <view v-if="userStore.isLoggedIn" class="logout-wrap">
      <wd-button block plain custom-class="logout-btn" @click="handleLogout">
        退出登录
      </wd-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { ensureLogin } from "@/utils/auth";
import { STORAGE_KEYS } from "@/config";

/** 订单快捷入口（与订单页 tab 对应） */
interface OrderEntry {
  key: string;
  name: string;
  icon: string;
}

const orderEntries: OrderEntry[] = [
  { key: "pay", name: "待支付", icon: "wallet" },
  { key: "ship", name: "待发货", icon: "bags" },
  { key: "renting", name: "租赁中", icon: "goods" },
  { key: "return", name: "归还中", icon: "rollback" },
];

const userStore = useUserStore();

/** 用户卡点击：未登录去登录，已登录无操作（M3c 个人资料页） */
function onUserCardTap(): void {
  if (userStore.isLoggedIn) return;
  uni.navigateTo({ url: "/pages/login/index" });
}

/** 订单入口：敏感入口前置拦截；switchTab 不能带参，经 storage 通知订单页切 tab */
async function goOrderList(tab: string): Promise<void> {
  if (!(await ensureLogin())) return;
  uni.setStorageSync(STORAGE_KEYS.ORDER_TAB, tab);
  uni.switchTab({ url: "/pages/order/list" });
}

/** 地址管理：敏感入口前置拦截 */
async function goAddress(): Promise<void> {
  if (!(await ensureLogin())) return;
  uni.navigateTo({ url: "/pages/address/list" });
}

/** 客服占位 */
function contactService(): void {
  // TODO(M3c): 接入客服会话/电话
  uni.showToast({ title: "客服功能开发中", icon: "none" });
}

async function handleLogout(): Promise<void> {
  const { cancel } = await uni.showModal({ title: "提示", content: "确定退出登录吗？" });
  if (cancel) return;
  await userStore.logout();
  uni.showToast({ title: "已退出登录", icon: "none" });
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding-bottom: 48rpx;
}

/* ===== 用户卡 ===== */
.user-card {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 64rpx 32rpx 48rpx;
  background: linear-gradient(135deg, #e93b3d, #f57a7c);
  color: #ffffff;
}

.avatar {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.user-name {
  font-size: 38rpx;
  font-weight: 600;
}

.user-id,
.user-hint {
  font-size: 24rpx;
  opacity: 0.85;
}

/* ===== 订单入口 ===== */
.order-entry-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;
}

.order-entry-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.order-entry-more {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.order-entry-more-text {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.order-entry-grid {
  display: flex;
  justify-content: space-around;
}

.order-entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.order-entry-name {
  font-size: 24rpx;
  color: $ezu-text-color-base;
}

/* ===== 工具栏 ===== */
.tools {
  display: flex;
  flex-direction: column;
}

.tool-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx 8rpx;
}

.tool-item + .tool-item {
  border-top: 1rpx solid $ezu-border-color-light;
}

.tool-name {
  flex: 1;
  font-size: 28rpx;
  color: $ezu-text-color-base;
}

/* ===== 退出登录 ===== */
.logout-wrap {
  margin: 32rpx 24rpx 0;
}

:deep(.logout-btn) {
  color: $ezu-text-color-secondary;
  border-color: $ezu-border-color;
}
</style>
