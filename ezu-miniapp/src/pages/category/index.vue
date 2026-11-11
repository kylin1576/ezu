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
            @tap="goGoodsList(sub)"
          >
            <!-- 图标位：真实数据无 icon 时渲染名称首字占位 -->
            <view class="sub-img" :style="{ backgroundColor: palette[sub.id % palette.length] }">
              <text class="sub-img-text">{{ sub.name.slice(0, 1) }}</text>
            </view>
            <text class="sub-name">{{ sub.name }}</text>
          </view>
        </view>
      </template>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { categoryTree } from "@/api/product";

/** 页面内统一品类展示结构（真实 CategoryTreeVo / 本地 mock 双向兼容） */
interface SubCategory {
  id: number;
  name: string;
}

interface TopCategory {
  id: number;
  name: string;
  children: SubCategory[];
}

/** 二级分类图标位配色（按 id 取模轮换） */
const palette = ["#eff6ff", "#fdeeee", "#ecfdf5", "#fef3c7", "#f3e8ff", "#fff7ed"];

// MOCK FALLBACK(T5 未就绪)：品类树接口失败时降级本地数据，不白屏
function buildMockTree(): TopCategory[] {
  return [
    {
      id: 9001,
      name: "热门推荐",
      children: [
        { id: 900101, name: "iPhone" },
        { id: 900102, name: "MacBook" },
        { id: 900103, name: "iPad" },
        { id: 900104, name: "Apple Watch" },
        { id: 900105, name: "Switch" },
        { id: 900106, name: "无人机" },
      ],
    },
    {
      id: 9002,
      name: "手机通讯",
      children: [
        { id: 900201, name: "苹果" },
        { id: 900202, name: "华为" },
        { id: 900203, name: "小米" },
        { id: 900204, name: "三星" },
        { id: 900205, name: "折叠屏" },
        { id: 900206, name: "备用机" },
      ],
    },
    {
      id: 9003,
      name: "电脑办公",
      children: [
        { id: 900301, name: "笔记本" },
        { id: 900302, name: "台式机" },
        { id: 900303, name: "显示器" },
        { id: 900304, name: "键盘" },
        { id: 900305, name: "打印机" },
      ],
    },
    {
      id: 9004,
      name: "摄影摄像",
      children: [
        { id: 900401, name: "微单相机" },
        { id: 900402, name: "单反相机" },
        { id: 900403, name: "镜头" },
        { id: 900404, name: "运动相机" },
        { id: 900405, name: "稳定器" },
      ],
    },
    {
      id: 9005,
      name: "智能穿戴",
      children: [
        { id: 900501, name: "智能手表" },
        { id: 900502, name: "智能眼镜" },
        { id: 900503, name: "VR 设备" },
      ],
    },
    {
      id: 9006,
      name: "游戏设备",
      children: [
        { id: 900601, name: "游戏主机" },
        { id: 900602, name: "掌机" },
        { id: 900603, name: "游戏手柄" },
      ],
    },
  ];
}

const categoryList = ref<TopCategory[]>(buildMockTree());
const activeId = ref<number>(categoryList.value[0]?.id ?? 0);

onLoad(() => {
  loadTree();
});

async function loadTree(): Promise<void> {
  try {
    // 浏览面接口：autoRedirect=false（categoryTree 内部已传）
    const tree = await categoryTree();
    const mapped = (tree ?? [])
      .filter((top) => (top.children?.length ?? 0) > 0)
      .map((top) => ({
        id: top.id,
        name: top.name,
        children: (top.children ?? []).map((sub) => ({ id: sub.id, name: sub.name })),
      }));
    if (mapped.length) {
      categoryList.value = mapped;
      activeId.value = mapped[0]?.id ?? 0;
    }
  } catch {
    // 请求层已 toast；保留 mock 渲染
  }
}

/** 二级分类点击 → 商品列表（按二级品类过滤） */
function goGoodsList(sub: SubCategory): void {
  uni.navigateTo({
    url: `/pages/goods/list?categoryId=${sub.id}&title=${encodeURIComponent(sub.name)}`,
  });
}
</script>

<style lang="scss" scoped>
.category-page {
  display: flex;
  /* 微信小程序端 100vh 即页面可用高度（原生 tabBar 已排除）；
     H5 端 tabBar 为 fixed 覆盖，底部可能被遮 ~88rpx，长列表滚动区留白 */
  height: 100vh;
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.sub-img-text {
  font-size: 36rpx;
  font-weight: 600;
  color: $ezu-text-color-secondary;
}

.sub-name {
  font-size: 24rpx;
  color: $ezu-text-color-base;
}
</style>
