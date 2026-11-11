<template>
  <view class="address-page">
    <!-- 地址列表 -->
    <view v-if="addresses.length" class="address-list">
      <view v-for="item in addresses" :key="item.id" class="address-card ezu-card" @tap="onItemTap(item)">
        <view class="address-main">
          <view class="address-head">
            <text class="receiver">{{ item.receiver }}</text>
            <text class="phone">{{ maskPhone(item.phone) }}</text>
            <view v-if="item.isDefault === 1" class="default-tag">默认</view>
          </view>
          <text class="address-text">
            {{ fullAddress(item) }}
          </text>
        </view>
        <view class="address-actions">
          <view v-if="!selectMode" class="action-btn" @tap.stop="goEdit(item.id)">
            <wd-icon name="edit-outline" size="36rpx" color="#666666" />
          </view>
          <view v-if="!selectMode" class="action-btn" @tap.stop="handleDelete(item)">
            <wd-icon name="delete" size="36rpx" color="#999999" />
          </view>
          <wd-icon v-if="selectMode" name="chevron-right" size="32rpx" color="#cccccc" />
        </view>
      </view>
    </view>

    <!-- 空态 -->
    <wd-status-tip v-if="!loading && !addresses.length" image="content" tip="暂无收货地址" />

    <!-- 新增按钮（选择模式下隐藏） -->
    <view v-if="!selectMode" class="add-wrap">
      <wd-button block custom-class="add-btn" @click="goEdit()">
        <wd-icon name="add" size="32rpx" color="#ffffff" />
        新增地址
      </wd-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { addressDelete, addressList, type AddressVo } from "@/api/address";
import { EVENTS } from "@/config";

/** 地址全量文本（省市区 + 详细） */
function fullAddress(item: AddressVo): string {
  return `${item.province}${item.city}${item.district ?? ""}${item.detailAddress}`;
}

/** 手机号脱敏展示（138****1234） */
function maskPhone(phone: string): string {
  return phone.length === 11 ? `${phone.slice(0, 3)}****${phone.slice(7)}` : phone;
}

const addresses = ref<AddressVo[]>([]);
const loading = ref(true);
/** 选择模式：订单确认页进入（query.select=1），点选回传后返回 */
const selectMode = ref(false);

onLoad((options) => {
  selectMode.value = options?.select === "1";
});

onShow(() => {
  // 新增/编辑返回后刷新
  loadList();
});

async function loadList(): Promise<void> {
  loading.value = true;
  try {
    // pageSize=-1 全量（本人地址）
    const page = await addressList({ pageNum: 1, pageSize: -1 });
    addresses.value = page?.list ?? [];
  } catch {
    // 请求层已 toast；401 已自动跳登录
  } finally {
    loading.value = false;
  }
}

/** 条目点击：选择模式回传事件，普通模式进编辑 */
function onItemTap(item: AddressVo): void {
  if (selectMode.value) {
    uni.$emit(EVENTS.ADDRESS_SELECTED, item);
    uni.navigateBack();
    return;
  }
  goEdit(item.id);
}

function goEdit(id?: number): void {
  const query = id ? `?id=${id}` : "";
  uni.navigateTo({ url: `/pages/address/edit${query}` });
}

async function handleDelete(item: AddressVo): Promise<void> {
  const { cancel } = await uni.showModal({
    title: "删除地址",
    content: `确定删除「${item.receiver}」的地址吗？`,
  });
  if (cancel) return;
  try {
    await addressDelete(item.id);
    uni.showToast({ title: "已删除", icon: "none" });
    loadList();
  } catch {
    // 请求层已 toast
  }
}
</script>

<style lang="scss" scoped>
.address-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding: 24rpx 24rpx 160rpx;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.address-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 28rpx 24rpx;
}

.address-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  min-width: 0;
}

.address-head {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.receiver {
  font-size: 30rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.phone {
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
  font-size: 26rpx;
  color: $ezu-text-color-base;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  align-items: center;
  gap: 28rpx;
  flex-shrink: 0;
}

.action-btn {
  padding: 8rpx;
}

/* ===== 新增按钮 ===== */
.add-wrap {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: calc(40rpx + env(safe-area-inset-bottom));
}

:deep(.add-btn) {
  --wot-button-radius: 48rpx;
}
</style>
