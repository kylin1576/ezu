<template>
  <view class="edit-page">
    <wd-form ref="formRef" :model="form">
      <wd-form-item label="收货人" prop="receiver" :rules="receiverRules" required>
        <wd-input
          v-model="form.receiver"
          no-border
          placeholder="请输入收货人姓名"
          :maxlength="20"
        />
      </wd-form-item>

      <wd-form-item label="手机号" prop="phone" :rules="phoneRules" required>
        <wd-input
          v-model="form.phone"
          no-border
          type="digit"
          placeholder="请输入 11 位手机号"
          :maxlength="11"
        />
      </wd-form-item>

      <!-- 小程序端：原生省市区选择器（三联） -->
      <!-- #ifdef MP-WEIXIN -->
      <wd-form-item label="所在地区" prop="province" :rules="regionRules" required>
        <picker mode="region" :value="regionArray" @change="onRegionChange">
          <view class="region-display" :class="{ 'region-display--empty': !form.province }">
            <text v-if="form.province">{{ form.province }} {{ form.city }} {{ form.district }}</text>
            <text v-else>请选择省 / 市 / 区</text>
            <wd-icon name="chevron-right" size="28rpx" color="#c0c0c0" />
          </view>
        </picker>
      </wd-form-item>
      <!-- #endif -->

      <!-- H5 端：uni-h5 未实现 region picker，降级为三个输入框 -->
      <!-- #ifdef H5 -->
      <wd-form-item label="省份" prop="province" :rules="regionRules" required>
        <wd-input v-model="form.province" no-border placeholder="如：广东省" :maxlength="20" />
      </wd-form-item>
      <wd-form-item label="城市" prop="city" :rules="cityRules" required>
        <wd-input v-model="form.city" no-border placeholder="如：深圳市" :maxlength="20" />
      </wd-form-item>
      <wd-form-item label="区县" prop="district">
        <wd-input v-model="form.district" no-border placeholder="如：南山区（可选）" :maxlength="20" />
      </wd-form-item>
      <!-- #endif -->

      <wd-form-item label="详细地址" prop="detailAddress" :rules="detailRules" required>
        <wd-input
          v-model="form.detailAddress"
          no-border
          placeholder="街道、楼牌号等"
          :maxlength="100"
        />
      </wd-form-item>

      <wd-form-item label="设为默认" prop="isDefault" center>
        <wd-switch
          v-model="form.isDefault"
          :active-value="1"
          :inactive-value="0"
          active-color="#e93b3d"
        />
      </wd-form-item>
    </wd-form>

    <view class="btn-area">
      <wd-button block custom-class="save-btn" :loading="submitting" @click="handleSave">
        保存
      </wd-button>
      <wd-button
        v-if="isEdit"
        block
        plain
        custom-class="delete-btn"
        :loading="deleting"
        @click="handleDelete"
      >
        删除该地址
      </wd-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { addressDelete, addressList, addressSave, type AddressVo } from "@/api/address";

/** wd-form 实例最小类型（避免深路径引库类型触发库内 TS1371） */
interface FormExposeLike {
  validate: () => Promise<{ valid: boolean }>;
}

/** 校验规则最小类型（同 wot-design-uni FormItemRule 的使用面） */
interface FormRule {
  required: boolean;
  message: string;
  pattern?: RegExp;
}

/** 校验规则（手机号 1 开头 11 位） */
const receiverRules: FormRule[] = [{ required: true, message: "请输入收货人姓名" }];
const phoneRules: FormRule[] = [
  { required: true, message: "请输入手机号" },
  { required: true, pattern: /^1\d{10}$/, message: "手机号格式不正确" },
];
const regionRules: FormRule[] = [{ required: true, message: "请选择/填写省份" }];
const cityRules: FormRule[] = [{ required: true, message: "请填写城市" }];
const detailRules: FormRule[] = [{ required: true, message: "请输入详细地址" }];

const formRef = ref<FormExposeLike>();
const isEdit = ref(false);
const submitting = ref(false);
const deleting = ref(false);

const form = reactive({
  id: undefined as number | undefined,
  receiver: "",
  phone: "",
  province: "",
  city: "",
  district: "",
  detailAddress: "",
  isDefault: 0,
});

/** 小程序 region picker 绑定值 */
const regionArray = computed(() => [form.province, form.city, form.district]);

onLoad(async (options) => {
  const id = options?.id ? Number(options.id) : undefined;
  if (!id) return;
  isEdit.value = true;
  uni.setNavigationBarTitle({ title: "编辑地址" });
  try {
    // 编辑模式：拉本人地址列表按 id 取详情（地址域无单查接口）
    const page = await addressList({ pageNum: 1, pageSize: -1 });
    const target = (page?.list ?? []).find((item) => item.id === id);
    if (!target) {
      uni.showToast({ title: "地址不存在", icon: "none" });
      setTimeout(() => uni.navigateBack(), 800);
      return;
    }
    fillForm(target);
  } catch {
    // 拉取失败不能停留在空表单（保存会误建新地址），退回列表页
    setTimeout(() => uni.navigateBack(), 800);
  }
});

function fillForm(vo: AddressVo): void {
  form.id = vo.id;
  form.receiver = vo.receiver;
  form.phone = vo.phone;
  form.province = vo.province;
  form.city = vo.city;
  form.district = vo.district ?? "";
  form.detailAddress = vo.detailAddress;
  form.isDefault = vo.isDefault;
}

/** 小程序 region picker 回填 */
function onRegionChange(e: { detail: { value: string[] } }): void {
  const [province, city, district] = e.detail.value ?? [];
  form.province = province ?? "";
  form.city = city ?? "";
  form.district = district ?? "";
}

async function handleSave(): Promise<void> {
  if (submitting.value) return;
  const { valid } = await formRef.value?.validate() ?? { valid: false };
  if (!valid) return;
  submitting.value = true;
  try {
    await addressSave({
      id: form.id,
      receiver: form.receiver.trim(),
      phone: form.phone.trim(),
      province: form.province.trim(),
      city: form.city.trim(),
      district: form.district?.trim() || undefined,
      detailAddress: form.detailAddress.trim(),
      isDefault: form.isDefault,
    });
    uni.showToast({ title: "保存成功", icon: "none" });
    setTimeout(() => uni.navigateBack(), 500);
  } catch {
    // 请求层已 toast
  } finally {
    submitting.value = false;
  }
}

async function handleDelete(): Promise<void> {
  if (deleting.value || !form.id) return;
  const { cancel } = await uni.showModal({ title: "删除地址", content: "确定删除该地址吗？" });
  if (cancel) return;
  deleting.value = true;
  try {
    await addressDelete(form.id);
    uni.showToast({ title: "已删除", icon: "none" });
    setTimeout(() => uni.navigateBack(), 500);
  } catch {
    // 请求层已 toast
  } finally {
    deleting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.edit-page {
  min-height: 100vh;
  background-color: $ezu-bg-color-page;
  padding: 24rpx;
}

/* region picker 展示行（小程序端） */
.region-display {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8rpx;
  font-size: 28rpx;
  color: $ezu-text-color-base;
  min-height: 48rpx;
}

.region-display--empty {
  color: $ezu-text-color-hint;
}

.btn-area {
  margin-top: 64rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

:deep(.save-btn) {
  --wot-button-radius: 48rpx;
}

:deep(.delete-btn) {
  --wot-button-radius: 48rpx;
  color: $ezu-text-color-secondary;
  border-color: $ezu-border-color;
}
</style>
