<template>
  <view class="home">
    <!-- 顶部搜索区（京东式：红色渐变头 + 白色圆角搜索条） -->
    <view class="search-bar" @tap="goGoodsList">
      <view class="search-input">
        <wd-icon name="search1" size="32rpx" color="#999999" />
        <text class="search-placeholder">iPhone 16 Pro</text>
      </view>
    </view>

    <!-- banner 轮播 -->
    <swiper class="banner" indicator-dots autoplay circular :interval="4000">
      <swiper-item v-for="item in banners" :key="item.id">
        <view class="banner-item" :style="{ background: item.color }">
          <text class="banner-title">{{ item.title }}</text>
        </view>
      </swiper-item>
    </swiper>

    <!-- 金刚区：品类入口 -->
    <view class="kingkong ezu-card">
      <view
        v-for="item in categories"
        :key="item.id"
        class="kingkong-item"
        @tap="goCategory(item.id)"
      >
        <view class="kingkong-icon" :style="{ backgroundColor: item.bg }">
          <wd-icon :name="item.icon" size="48rpx" :color="item.color" />
        </view>
        <text class="kingkong-name">{{ item.name }}</text>
      </view>
    </view>

    <!-- 推荐流：2 列商品卡 -->
    <view class="section">
      <view class="section-header">
        <view class="section-line" />
        <text class="section-title">为你推荐</text>
        <view class="section-line" />
      </view>
      <view class="goods-grid">
        <view
          v-for="goods in goodsList"
          :key="goods.id"
          class="goods-card"
          @tap="goGoodsDetail(goods.id)"
        >
          <!-- TODO(M3c): 换真实商品图 -->
          <view class="goods-img" :style="{ backgroundColor: goods.bg }" />
          <view class="goods-info">
            <text class="goods-title ezu-ellipsis-2">{{ goods.title }}</text>
            <view class="goods-bottom">
              <view class="ezu-price goods-price">
                {{ goods.price.toFixed(2) }}
              </view>
              <text class="goods-unit">/月</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/** 顶部 banner mock */
interface BannerMock {
  id: number;
  title: string;
  color: string;
}

/** 金刚区品类 mock */
interface CategoryEntryMock {
  id: number;
  name: string;
  icon: string;
  color: string;
  bg: string;
}

/** 推荐商品 mock */
interface GoodsMock {
  id: number;
  title: string;
  price: number;
  bg: string;
}

// TODO(M3c): 以下均为本地 mock，待商品接口就绪后替换为 /api/app/goods/**
const banners: BannerMock[] = [
  { id: 1, title: "新机首发 · 0 押金起租", color: "linear-gradient(135deg, #e93b3d, #ff8a65)" },
  { id: 2, title: "数码好物 · 低至 3 折日租", color: "linear-gradient(135deg, #1e3a8a, #4f9dff)" },
  { id: 3, title: "企业租赁 · 专属客服上门", color: "linear-gradient(135deg, #6d28d9, #a855f7)" },
];

const categories: CategoryEntryMock[] = [
  { id: 1, name: "电脑办公", icon: "computer", color: "#e93b3d", bg: "#fdedee" },
  { id: 2, name: "手机通讯", icon: "mobile", color: "#3b82f6", bg: "#eff6ff" },
  { id: 3, name: "摄影摄像", icon: "camera", color: "#f59e0b", bg: "#fef3c7" },
  { id: 4, name: "智能设备", icon: "video", color: "#10b981", bg: "#ecfdf5" },
];

const goodsList: GoodsMock[] = [
  { id: 1, title: "Apple MacBook Pro 14 M3 18G+512G 深空灰", price: 328, bg: "#fdeeee" },
  { id: 2, title: "iPhone 16 Pro 256G 原色钛金属", price: 299, bg: "#eff6ff" },
  { id: 3, title: "Sony A7M4 全画幅微单相机机身", price: 456, bg: "#fef3c7" },
  { id: 4, title: "DJI Mavic 3 Pro 无人机畅飞套装", price: 388, bg: "#ecfdf5" },
];

/** 搜索框点击 → 商品列表页 */
function goGoodsList(): void {
  // TODO(M3c): 携带搜索关键字
  uni.navigateTo({ url: "/pages/goods/list" });
}

/** 金刚区点击 → 分类页并定位 */
function goCategory(categoryId: number): void {
  uni.switchTab({ url: "/pages/category/index" });
  // TODO(M3c): 通过事件/全局状态把 categoryId 传给分类页定位
}

/** 商品卡点击 → 商品详情 */
function goGoodsDetail(goodsId: number): void {
  uni.navigateTo({ url: `/pages/goods/detail?id=${goodsId}` });
}
</script>

<style lang="scss" scoped>
.home {
  padding-bottom: 24rpx;
}

/* ===== 搜索区 ===== */
.search-bar {
  padding: 20rpx 24rpx 32rpx;
  background: linear-gradient(180deg, #e93b3d 0%, #f5585a 70%, #f5f6f7 100%);
}

.search-input {
  height: 68rpx;
  border-radius: 34rpx;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  gap: 12rpx;
}

.search-placeholder {
  font-size: 26rpx;
  color: $ezu-text-color-hint;
}

/* ===== banner ===== */
.banner {
  height: 260rpx;
  margin: -16rpx 24rpx 0;
  border-radius: 16rpx;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-title {
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 600;
  letter-spacing: 4rpx;
}

/* ===== 金刚区 ===== */
.kingkong {
  display: flex;
  justify-content: space-around;
}

.kingkong-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.kingkong-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kingkong-name {
  font-size: 24rpx;
  color: $ezu-text-color-base;
}

/* ===== 推荐流 ===== */
.section-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
  padding: 24rpx 0 8rpx;
}

.section-line {
  width: 48rpx;
  height: 4rpx;
  border-radius: 2rpx;
  background-color: $ezu-border-color;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.goods-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 16rpx 24rpx 0;
}

.goods-card {
  width: calc(50% - 8rpx);
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
}

.goods-img {
  width: 100%;
  height: 320rpx;
}

.goods-info {
  padding: 16rpx 20rpx 20rpx;
}

.goods-title {
  font-size: 26rpx;
  color: $ezu-text-color-base;
  line-height: 1.4;
  height: 74rpx;
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
