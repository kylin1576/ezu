<template>
  <view class="detail-page">
    <template v-if="detail">
      <!-- 商品图轮播 -->
      <swiper class="gallery" indicator-dots circular>
        <swiper-item v-for="(img, index) in galleryImages" :key="index">
          <image v-if="img" class="gallery-img" :src="img" mode="aspectFill" />
          <view v-else class="gallery-img gallery-img--placeholder">
            <wd-icon name="picture" size="96rpx" color="#dddddd" />
          </view>
        </swiper-item>
      </swiper>

      <!-- 标题区 -->
      <view class="ezu-card base-card">
        <view class="price-row">
          <view class="ezu-price main-price">{{ currentPlan.price.toFixed(2) }}</view>
          <text class="price-unit">/{{ unitText }}/期</text>
        </view>
        <text class="goods-name">{{ detail.name }}</text>
        <text v-if="detail.subtitle" class="goods-subtitle">{{ detail.subtitle }}</text>
        <view class="stock-row">
          <text class="stock-text">押金 {{ currentSku.deposit.toFixed(2) }} 元</text>
          <text v-if="typeof currentSku.stockCount === 'number'" class="stock-text">
            可租 {{ currentSku.stockCount }} 台
          </text>
        </view>
      </view>

      <!-- 规格选择 -->
      <view class="ezu-card section-card">
        <text class="section-title">选择规格</text>
        <view class="chip-row">
          <view
            v-for="(sku, index) in detail.skus"
            :key="sku.id"
            class="chip"
            :class="{ 'chip--active': index === skuIndex, 'chip--disabled': sku.stockCount === 0 }"
            @tap="selectSku(index)"
          >
            {{ sku.specName }}
          </view>
        </view>
      </view>

      <!-- 租金方案 + 租期 -->
      <view class="ezu-card section-card">
        <text class="section-title">租金方案</text>
        <view class="chip-row">
          <view
            v-for="(plan, index) in currentSku.rentalPlans"
            :key="plan.id"
            class="chip"
            :class="{ 'chip--active': index === planIndex }"
            @tap="selectPlan(index)"
          >
            <text class="chip-price">¥{{ plan.price.toFixed(2) }}</text>
            <text class="chip-unit">/{{ unitTextMap[plan.unit] ?? "期" }}</text>
          </view>
        </view>

        <view class="days-row">
          <text class="days-label">租期（天）</text>
          <wd-input-number
            v-model="rentDays"
            :min="currentPlan.minDays"
            :max="999"
            @change="clampRentDays"
          />
        </view>
        <text class="days-hint">该方案最少租 {{ currentPlan.minDays }} 天</text>

        <view class="estimate-row">
          <text class="estimate-label">预计租金（确认订单页以试算为准）</text>
          <view class="ezu-price estimate-price">{{ estimatedRent.toFixed(2) }}</view>
        </view>
      </view>

      <!-- 商品参数 -->
      <view v-if="detail.params?.length" class="ezu-card section-card">
        <text class="section-title">商品参数</text>
        <view v-for="param in detail.params" :key="param.name" class="param-row">
          <text class="param-name">{{ param.name }}</text>
          <text class="param-value">{{ param.value }}</text>
        </view>
      </view>

      <view class="bottom-blank" />

      <!-- 底部操作栏 -->
      <view class="action-bar">
        <view class="action-left">
          <text class="action-label">预计租金</text>
          <view class="ezu-price action-price">{{ estimatedRent.toFixed(2) }}</view>
        </view>
        <wd-button custom-class="rent-btn" :loading="false" @click="goConfirm">
          立即租赁
        </wd-button>
      </view>
    </template>

    <!-- 加载/失败占位 -->
    <wd-status-tip v-else-if="loading" image="network" tip="加载中..." />
    <wd-status-tip v-else image="content" tip="商品不存在或已下架" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { productDetail, type AppProductDetailVo, type RentalPlanVo, type SkuVo } from "@/api/product";
import { calcTotalRent, RENTAL_UNIT_TEXT } from "@/constants/product";
import { ensureLogin } from "@/utils/auth";

const unitTextMap = RENTAL_UNIT_TEXT;

const detail = ref<AppProductDetailVo | null>(null);
const loading = ref(true);
/** mock 降级数据（后端 T5 未就绪时兜底展示，禁止下单） */
const isMock = ref(false);

const skuIndex = ref(0);
const planIndex = ref(0);
const rentDays = ref(1);

const currentSku = computed<SkuVo>(() => {
  // 防御：skus 为空时给占位骨架，模板不崩
  return detail.value?.skus?.[skuIndex.value] ?? emptySku();
});

const currentPlan = computed<RentalPlanVo>(() => {
  return currentSku.value.rentalPlans?.[planIndex.value] ?? emptyPlan();
});

const unitText = computed(() => RENTAL_UNIT_TEXT[currentPlan.value.unit] ?? "月");

/** 预估租金（前端口径，下单以 trial 为准） */
const estimatedRent = computed(() =>
  calcTotalRent(currentPlan.value.unit, currentPlan.value.price, rentDays.value),
);

const galleryImages = computed<string[]>(() => {
  const images = detail.value?.images?.filter(Boolean) ?? [];
  if (images.length) return images;
  return detail.value?.mainImage ? [detail.value.mainImage] : [""];
});

function emptySku(): SkuVo {
  return { id: 0, specName: "-", deposit: 0, rentalPlans: [emptyPlan()] };
}

function emptyPlan(): RentalPlanVo {
  return { id: 0, unit: 3, price: 0, minDays: 1 };
}

onLoad(async (options) => {
  const id = options?.id ? Number(options.id) : 0;
  if (!id) {
    loading.value = false;
    return;
  }
  try {
    // 浏览面接口：autoRedirect=false，未登录/未就绪不踢登录页
    detail.value = await productDetail(id);
    initSelection();
  } catch (error) {
    // MOCK FALLBACK(T5 未就绪)：401/404/网络异常时降级本地数据渲染，不白屏
    if (isDegradable(error)) {
      isMock.value = true;
      detail.value = buildMockDetail(id);
      initSelection();
    }
  } finally {
    loading.value = false;
  }
});

/** 可降级错误：401 未登录 / 404 未实现 / -1 网络失败 / -2 响应格式异常 */
function isDegradable(error: unknown): boolean {
  const code = (error as { code?: number })?.code;
  return code === 401 || code === 404 || code === -1 || code === -2;
}

function initSelection(): void {
  const skus = detail.value?.skus ?? [];
  // 默认选第一个有库存的 SKU，全部无货则选第一个
  const firstAvailable = skus.findIndex((sku) => (sku.stockCount ?? 1) > 0);
  skuIndex.value = firstAvailable >= 0 ? firstAvailable : 0;
  planIndex.value = 0;
  rentDays.value = currentPlan.value.minDays || 1;
}

function selectSku(index: number): void {
  const sku = detail.value?.skus?.[index];
  if (!sku || sku.stockCount === 0) {
    uni.showToast({ title: "该规格暂无可租设备", icon: "none" });
    return;
  }
  skuIndex.value = index;
  // 换 SKU 后方案重置为第一个，租期重置为方案最小天数
  planIndex.value = 0;
  rentDays.value = currentPlan.value.minDays || 1;
}

function selectPlan(index: number): void {
  planIndex.value = index;
  rentDays.value = currentPlan.value.minDays || 1;
}

/** 方案切换瞬间 min 抬升时夹紧当前值 */
function clampRentDays(): void {
  if (rentDays.value < currentPlan.value.minDays) {
    rentDays.value = currentPlan.value.minDays;
  }
}

// 租期最小值跟随方案变化（wd-input-number 的 min 不响应式回夹）
watch(
  () => currentPlan.value.id,
  () => clampRentDays(),
);

async function goConfirm(): Promise<void> {
  if (isMock.value) {
    uni.showToast({ title: "演示数据，后端商品接口就绪后可下单", icon: "none" });
    return;
  }
  if ((currentSku.value.stockCount ?? 1) <= 0) {
    uni.showToast({ title: "该规格暂无可租设备", icon: "none" });
    return;
  }
  if (!(await ensureLogin())) return;
  uni.navigateTo({
    url:
      `/pages/order/confirm?skuId=${currentSku.value.id}&planId=${currentPlan.value.id}` +
      `&days=${rentDays.value}&unit=${currentPlan.value.unit}` +
      `&goodsName=${encodeURIComponent(detail.value?.name ?? "")}`,
  });
}

/** mock 降级详情（结构与 AppProductDetailVo 对齐；skuId=0 防真实下单） */
function buildMockDetail(id: number): AppProductDetailVo {
  return {
    id,
    name: "Apple MacBook Pro 14 M3 18G+512G 深空灰（演示数据）",
    subtitle: "演示数据 · 后端商品接口就绪后展示真实商品",
    mainImage: "",
    images: ["", ""],
    params: [
      { name: "处理器", value: "Apple M3" },
      { name: "内存", value: "18GB" },
      { name: "存储", value: "512GB SSD" },
    ],
    skus: [
      {
        id: 0,
        specName: "深空灰 512G",
        deposit: 999,
        buyoutPrice: 12999,
        stockCount: 3,
        rentalPlans: [
          { id: 0, unit: 3, price: 328, minDays: 90 },
          { id: 0, unit: 1, price: 25, minDays: 30 },
        ],
      },
    ],
  };
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding-bottom: 24rpx;
}

/* ===== 轮播 ===== */
.gallery {
  height: 640rpx;
}

.gallery-img {
  width: 100%;
  height: 100%;
}

.gallery-img--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $ezu-bg-color-grey;
}

/* ===== 标题卡 ===== */
.base-card {
  margin: 20rpx 24rpx 0;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.main-price {
  font-size: 44rpx;
}

.price-unit {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.goods-name {
  font-size: 32rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
  line-height: 1.4;
}

.goods-subtitle {
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.stock-row {
  display: flex;
  gap: 32rpx;
}

.stock-text {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}

/* ===== 区块卡 ===== */
.section-card {
  margin: 20rpx 24rpx 0;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $ezu-text-color-main;
}

/* ===== chip ===== */
.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.chip {
  padding: 12rpx 28rpx;
  border-radius: 32rpx;
  border: 2rpx solid $ezu-border-color;
  font-size: 26rpx;
  color: $ezu-text-color-base;
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.chip--active {
  border-color: $ezu-color-primary;
  background-color: $ezu-color-primary-light;
  color: $ezu-color-primary;
}

.chip--disabled {
  opacity: 0.45;
}

.chip-price {
  font-weight: 600;
}

.chip-unit {
  font-size: 22rpx;
}

/* ===== 租期 ===== */
.days-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.days-label {
  font-size: 26rpx;
  color: $ezu-text-color-base;
}

.days-hint {
  font-size: 22rpx;
  color: $ezu-text-color-hint;
}

.estimate-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1rpx solid $ezu-border-color-light;
  padding-top: 20rpx;
}

.estimate-label {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}

.estimate-price {
  font-size: 36rpx;
}

/* ===== 参数 ===== */
.param-row {
  display: flex;
  gap: 24rpx;
}

.param-name {
  width: 160rpx;
  flex-shrink: 0;
  font-size: 24rpx;
  color: $ezu-text-color-hint;
}

.param-value {
  flex: 1;
  font-size: 24rpx;
  color: $ezu-text-color-base;
}

/* ===== 底部操作栏 ===== */
.bottom-blank {
  height: 140rpx;
}

.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.action-left {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.action-label {
  font-size: 24rpx;
  color: $ezu-text-color-secondary;
}

.action-price {
  font-size: 40rpx;
}

:deep(.rent-btn) {
  --wot-button-radius: 44rpx;
  padding: 0 72rpx;
}
</style>
