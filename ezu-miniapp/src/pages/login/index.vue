<template>
  <view class="login-page">
    <!-- 品牌区 -->
    <view class="brand">
      <view class="brand-logo">易</view>
      <text class="brand-name">易租</text>
      <text class="brand-slogan">轻松租 · 放心用</text>
    </view>

    <!-- 登录按钮 -->
    <view class="login-actions">
      <!-- #ifdef MP-WEIXIN -->
      <wd-button block size="large" :loading="loading" @click="handleLogin(1)">
        微信一键登录
      </wd-button>
      <!-- #endif -->
      <!-- #ifdef MP-ALIPAY -->
      <wd-button block size="large" :loading="loading" @click="handleLogin(2)">
        支付宝一键登录
      </wd-button>
      <!-- #endif -->
      <!-- #ifdef H5 -->
      <wd-button block size="large" :loading="loading" @click="handleLogin(1)">
        模拟登录（开发）
      </wd-button>
      <!-- #endif -->
    </view>

    <!-- 协议占位 -->
    <view class="agreement">
      <text class="agreement-text">登录即代表同意</text>
      <text class="agreement-link">《用户协议》</text>
      <text class="agreement-text">与</text>
      <text class="agreement-link">《隐私政策》</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from "vue";
import type { LoginChannel } from "@/api/auth";
import { ApiError } from "@/utils/request";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const loading = ref(false);

/** 登录成功 toast 的最短展示时长后再回跳 */
const LOGIN_REDIRECT_DELAY_MS = 400;

async function handleLogin(channel: LoginChannel): Promise<void> {
  if (loading.value) return;
  loading.value = true;
  try {
    await userStore.login(channel);
    uni.showToast({ title: "登录成功", icon: "success" });
    setTimeout(redirectBack, LOGIN_REDIRECT_DELAY_MS);
  } catch (error) {
    // ApiError（网络/接口/业务错误）由请求层统一 toast；
    // 其余为 uni.login 取 code 失败等前置错误，此处兜底提示
    if (!(error instanceof ApiError)) {
      uni.showToast({ title: "登录失败，请重试", icon: "none" });
    }
  } finally {
    loading.value = false;
  }
}

/** 成功回跳：有上一页则返回，否则回首页 tab */
function redirectBack(): void {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack();
  } else {
    uni.switchTab({ url: "/pages/index/index" });
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background-color: $ezu-bg-color;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 64rpx;
}

.brand {
  margin-top: 200rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-logo {
  width: 140rpx;
  height: 140rpx;
  border-radius: 32rpx;
  background-color: $ezu-color-primary;
  color: #ffffff;
  font-size: 80rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-name {
  margin-top: 32rpx;
  font-size: 44rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.brand-slogan {
  margin-top: 16rpx;
  font-size: 26rpx;
  color: $ezu-text-color-hint;
}

.login-actions {
  width: 100%;
  margin-top: 120rpx;
}

.agreement {
  position: fixed;
  bottom: 60rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.agreement-text {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.agreement-link {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}
</style>
