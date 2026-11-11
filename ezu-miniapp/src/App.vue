<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";

onLaunch(() => {
  console.log("App Launch");
});
onShow(() => {
  console.log("App Show");
});
onHide(() => {
  console.log("App Hide");
});
</script>

<style>
/* wot-design-uni v1 主题变量覆盖（京东红，全局组件生效） */
page {
  --wot-color-theme: #e93b3d;
  --wot-color-theme-light: #fdedee;
  background-color: #f5f6f7;
  font-size: 28rpx;
  color: #333333;
}

/* ===== 全局工具类 ===== */

/* 白底圆角卡片 */
.ezu-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin: 16rpx 24rpx;
  padding: 24rpx;
}

/* 价格（自动带 ¥ 前缀） */
.ezu-price {
  color: #e93b3d;
  font-weight: 600;
  font-size: 36rpx;
}
.ezu-price::before {
  content: "¥";
  font-size: 0.72em;
  margin-right: 2rpx;
}

/* 单行省略 */
.ezu-ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

/* 两行省略 */
.ezu-ellipsis-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
