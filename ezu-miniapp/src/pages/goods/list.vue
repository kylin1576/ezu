<template>
  <view class="goods-list-page">
    <!-- 搜索条 -->
    <view class="search-wrap">
      <wd-search
        v-model="keyword"
        placeholder="搜索商品名称 / 副标题"
        hide-cancel
        @search="onSearch"
        @clear="onSearch"
      />
    </view>

    <!-- 列表头：分类名 / 结果数 -->
    <view v-if="categoryTitle" class="list-header">
      <text class="list-header-title">{{ categoryTitle }}</text>
      <text v-if="pager.total.value" class="list-header-count">共 {{ pager.total.value }} 件</text>
    </view>

    <!-- 商品网格 -->
    <view v-if="pager.list.value.length" class="goods-grid">
      <goods-card
        v-for="item in pager.list.value"
        :key="item.id"
        class="grid-item"
        :id="item.id"
        :name="item.name"
        :subtitle="item.subtitle ?? ''"
        :image="item.mainImage ?? ''"
        :price="item.minRentPrice"
        :unit="item.minRentUnit"
      />
    </view>

    <!-- 首屏加载中 -->
    <wd-loadmore v-if="!pager.list.value.length && pager.loading.value" state="loading" loading-text="加载中..." />

    <!-- 空态 -->
    <wd-status-tip
      v-else-if="!pager.loading.value && !pager.list.value.length"
      image="search"
      tip="没有找到相关商品"
    />

    <!-- 分页状态：加载中 / 没有更多 -->
    <wd-loadmore v-if="pager.list.value.length" :state="pager.loading.value ? 'loading' : 'finished'" loading-text="加载中..." finished-text="没有更多了" />
  </view>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { onLoad, onPullDownRefresh, onReachBottom } from "@dcloudio/uni-app";
import GoodsCard from "@/components/goods-card/goods-card.vue";
import { productList, type AppProductListVo, type ProductQueryReq } from "@/api/product";
import { usePagination } from "@/composables/use-pagination";

/** 列表查询条件（tab 内不变的部分） */
const categoryId = ref<number>();
const categoryTitle = ref("");
const keyword = ref("");

const pager = usePagination<AppProductListVo>((page, size) => {
  const query: ProductQueryReq = { pageNum: page, pageSize: size };
  if (categoryId.value) query.categoryId = categoryId.value;
  if (keyword.value.trim()) query.keyword = keyword.value.trim();
  return productList(query);
});

onLoad((options) => {
  if (options?.categoryId) categoryId.value = Number(options.categoryId);
  if (options?.title) {
    categoryTitle.value = decodeURIComponent(options.title);
    uni.setNavigationBarTitle({ title: categoryTitle.value });
  }
  if (options?.keyword) keyword.value = decodeURIComponent(options.keyword);
  pager.load();
});

onReachBottom(() => {
  pager.load();
});

onPullDownRefresh(async () => {
  pager.reset();
  await pager.load();
  uni.stopPullDownRefresh();
});

/** 搜索/清空：条件变化重新拉第一页 */
function onSearch(): void {
  pager.reset();
  pager.load();
}
</script>

<style lang="scss" scoped>
.goods-list-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding-bottom: 40rpx;
}

.search-wrap {
  background-color: #ffffff;
  padding: 8rpx 16rpx 0;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 24rpx 8rpx;
}

.list-header-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

.list-header-count {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.goods-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 16rpx 24rpx 0;
}

.grid-item {
  width: calc(50% - 8rpx);
}
</style>
