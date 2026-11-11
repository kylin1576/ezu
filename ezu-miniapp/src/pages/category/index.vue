<template>
  <view class="category-page">
    <!-- 左侧一级分类栏 -->
    <scroll-view class="side-bar" scroll-y>
      <view
        v-for="item in categoryList"
        :key="item.id"
        class="side-item"
        :class="{ 'side-item--active': item.id === activeId }"
        @tap="activeId = item.id"
      >
        <view class="side-indicator" />
        <text class="side-name">{{ item.name }}</text>
      </view>
    </scroll-view>

    <!-- 右侧二级分类 -->
    <scroll-view class="sub-panel" scroll-y>
      <template v-for="item in categoryList" :key="item.id">
        <view v-if="item.id === activeId" class="sub-grid">
          <view
            v-for="sub in item.children"
            :key="sub.id"
            class="sub-item"
            @tap="goGoodsList(item.name, sub.name)"
          >
            <!-- TODO(M3c): 换真实分类图 -->
            <view class="sub-img" :style="{ backgroundColor: sub.bg }" />
            <text class="sub-name">{{ sub.name }}</text>
          </view>
        </view>
      </template>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from "vue";

/** 二级分类 mock */
interface SubCategoryMock {
  id: number;
  name: string;
  bg: string;
}

/** 一级分类 mock */
interface CategoryMock {
  id: number;
  name: string;
  children: SubCategoryMock[];
}

// TODO(M3c): 本地 mock，待品类接口就绪后替换为 /api/app/category/**
const categoryList: CategoryMock[] = [
  {
    id: 1,
    name: "热门推荐",
    children: [
      { id: 11, name: " iPhone ", bg: "#eff6ff" },
      { id: 12, name: "MacBook", bg: "#fdeeee" },
      { id: 13, name: "iPad", bg: "#ecfdf5" },
      { id: 14, name: "Apple Watch", bg: "#fef3c7" },
      { id: 15, name: "Switch", bg: "#f3e8ff" },
      { id: 16, name: "无人机", bg: "#fff7ed" },
    ],
  },
  {
    id: 2,
    name: "手机通讯",
    children: [
      { id: 21, name: "苹果", bg: "#fdeeee" },
      { id: 22, name: "华为", bg: "#eff6ff" },
      { id: 23, name: "小米", bg: "#fff7ed" },
      { id: 24, name: "三星", bg: "#ecfdf5" },
      { id: 25, name: "折叠屏", bg: "#fef3c7" },
      { id: 26, name: "备用机", bg: "#f3e8ff" },
    ],
  },
  {
    id: 3,
    name: "电脑办公",
    children: [
      { id: 31, name: "笔记本", bg: "#eff6ff" },
      { id: 32, name: "台式机", bg: "#fdeeee" },
      { id: 33, name: "显示器", bg: "#ecfdf5" },
      { id: 34, name: "键盘", bg: "#fef3c7" },
      { id: 35, name: "打印机", bg: "#f3e8ff" },
      { id: 36, name: "绘图板", bg: "#fff7ed" },
    ],
  },
  {
    id: 4,
    name: "摄影摄像",
    children: [
      { id: 41, name: "微单相机", bg: "#fef3c7" },
      { id: 42, name: "单反相机", bg: "#eff6ff" },
      { id: 43, name: "镜头", bg: "#fdeeee" },
      { id: 44, name: "运动相机", bg: "#ecfdf5" },
      { id: 45, name: "稳定器", bg: "#f3e8ff" },
      { id: 46, name: "直播设备", bg: "#fff7ed" },
    ],
  },
  {
    id: 5,
    name: "智能穿戴",
    children: [
      { id: 51, name: "智能手表", bg: "#ecfdf5" },
      { id: 52, name: "智能眼镜", bg: "#eff6ff" },
      { id: 53, name: "健康监测", bg: "#fef3c7" },
      { id: 54, name: "VR 设备", bg: "#f3e8ff" },
    ],
  },
  {
    id: 6,
    name: "游戏设备",
    children: [
      { id: 61, name: "游戏主机", bg: "#fdeeee" },
      { id: 62, name: "掌机", bg: "#eff6ff" },
      { id: 63, name: "游戏手柄", bg: "#ecfdf5" },
      { id: 64, name: "电竞外设", bg: "#fef3c7" },
    ],
  },
  {
    id: 7,
    name: "办公设备",
    children: [
      { id: 71, name: "投影仪", bg: "#fff7ed" },
      { id: 72, name: "会议大屏", bg: "#eff6ff" },
      { id: 73, name: "考勤机", bg: "#fdeeee" },
    ],
  },
];

const activeId = ref<number>(categoryList[0]?.id ?? 0);

/** 二级分类点击 → 商品列表（按分类过滤） */
function goGoodsList(categoryName: string, subName: string): void {
  // TODO(M3c): 传分类 id，由商品列表接口按分类查询
  uni.navigateTo({
    url: `/pages/goods/list?category=${encodeURIComponent(categoryName)}&sub=${encodeURIComponent(subName)}`,
  });
}
</script>

<style lang="scss" scoped>
.category-page {
  display: flex;
  height: calc(100vh - 88rpx);
}

/* ===== 左侧一级栏（176rpx） ===== */
.side-bar {
  width: 176rpx;
  height: 100%;
  background-color: $ezu-bg-color-grey;
}

.side-item {
  position: relative;
  height: 108rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.side-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6rpx;
  height: 0;
  border-radius: 3rpx;
  background-color: $ezu-color-primary;
  transition: height 0.15s ease;
}

.side-name {
  font-size: 26rpx;
  color: $ezu-text-color-base;
}

.side-item--active {
  background-color: #ffffff;

  .side-indicator {
    height: 40rpx;
  }

  .side-name {
    color: $ezu-color-primary;
    font-weight: 600;
  }
}

/* ===== 右侧二级网格 ===== */
.sub-panel {
  flex: 1;
  height: 100%;
  background-color: #ffffff;
}

.sub-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx;
}

.sub-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 0;
}

.sub-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 16rpx;
}

.sub-name {
  font-size: 24rpx;
  color: $ezu-text-color-base;
}
</style>
