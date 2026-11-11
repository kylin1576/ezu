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

    <!-- 已登录：状态 tab + 订单列表 -->
    <template v-else>
      <wd-tabs v-model="activeTab" @change="onTabChange">
        <wd-tab v-for="tab in ORDER_TABS" :key="tab.key" :title="tab.title" :name="tab.key" />
      </wd-tabs>

      <view v-if="pager.list.value.length" class="order-list">
        <order-card
          v-for="order in pager.list.value"
          :key="order.orderNo"
          :order="order"
          @action="onAction"
        />
        <wd-loadmore
          :state="pager.loading.value ? 'loading' : 'finished'"
          loading-text="加载中..."
          finished-text="没有更多了"
        />
      </view>

      <!-- 空态（首次加载完成且无数据） -->
      <wd-status-tip
        v-if="!pager.loading.value && !pager.list.value.length"
        image="content"
        tip="暂无相关订单"
      />
    </template>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { onPullDownRefresh, onReachBottom, onShow } from "@dcloudio/uni-app";
import { useUserStore } from "@/stores/user";
import OrderCard from "@/components/order-card/order-card.vue";
import { orderCancel, orderConfirmReceive, orderList, orderReturnApply, type OrderListVo } from "@/api/order";
import { ORDER_TABS, type OrderAction } from "@/constants/order";
import { STORAGE_KEYS } from "@/config";
import { usePagination } from "@/composables/use-pagination";

const userStore = useUserStore();
const activeTab = ref(ORDER_TABS[0]?.key ?? "all");
/** 是否已消费 onShow 的 storage tab（避免 watch 重复触发） */
let initialized = false;

const pager = usePagination<OrderListVo>((page, size) => {
  const tab = ORDER_TABS.find((item) => item.key === activeTab.value);
  return orderList({
    pageNum: page,
    pageSize: size,
    status: tab?.status,
  });
});

onShow(() => {
  if (!userStore.isLoggedIn) return;
  // 我的页 4 入口经 storage 指定 tab（switchTab 不能带参）
  const target = uni.getStorageSync(STORAGE_KEYS.ORDER_TAB) as string;
  if (target && ORDER_TABS.some((tab) => tab.key === target)) {
    uni.removeStorageSync(STORAGE_KEYS.ORDER_TAB);
    if (activeTab.value !== target) {
      activeTab.value = target;
      refresh();
      initialized = true;
      return;
    }
  }
  // 已有数据不重复首拉；tab 页从详情页返回时也不强制刷新
  if (!initialized && !pager.list.value.length) {
    refresh();
    initialized = true;
  }
});

watch(
  () => userStore.isLoggedIn,
  (loggedIn) => {
    if (loggedIn && !pager.list.value.length) {
      refresh();
      initialized = true;
    }
  },
);

onReachBottom(() => {
  pager.load();
});

onPullDownRefresh(async () => {
  await pager.load();
  uni.stopPullDownRefresh();
});

/** tab 切换 → 重置分页 */
function onTabChange(): void {
  refresh();
}

function refresh(): void {
  pager.reset();
  pager.load();
}

/** 卡片操作按钮（确认弹窗 → 调接口 → 刷新） */
async function onAction(action: OrderAction, orderNo: string): Promise<void> {
  if (action === "pay") {
    // 支付在订单详情页发起
    uni.navigateTo({ url: `/pages/order/detail?orderNo=${orderNo}` });
    return;
  }
  const confirmMap: Record<string, { title: string; content: string; api: (no: string) => Promise<void>; success: string }> = {
    cancel: { title: "取消订单", content: "确定取消该订单吗？", api: orderCancel, success: "订单已取消" },
    confirmReceive: { title: "确认收货", content: "确认已收到设备？确认后租约开始生效", api: orderConfirmReceive, success: "已确认收货" },
    returnApply: { title: "申请归还", content: "申请后请按指引寄回设备", api: orderReturnApply, success: "归还申请已提交" },
  };
  const task = confirmMap[action];
  if (!task) return;
  const { cancel } = await uni.showModal({ title: task.title, content: task.content });
  if (cancel) return;
  try {
    await task.api(orderNo);
    uni.showToast({ title: task.success, icon: "none" });
    refresh();
  } catch {
    // 请求层已 toast
  }
}

function goLogin(): void {
  uni.navigateTo({ url: "/pages/login/index" });
}
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  /* H5 端 tabBar 为 DOM 固定条，长列表底部留白防遮挡 */
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  padding: 20rpx 24rpx 0;
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
