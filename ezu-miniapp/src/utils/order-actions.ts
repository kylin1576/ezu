import { orderCancel, orderConfirmReceive, orderReturnApply } from "@/api/order";
import { payOrderApi } from "@/api/payment";
import type { OrderAction } from "@/constants/order";

/** 操作成功后的回调（刷新列表/详情） */
export type OrderActionSuccessHandler = () => void | Promise<void>;

interface ActionTask {
  /** 确认弹窗标题 */
  title: string;
  /** 确认弹窗内容 */
  content: string;
  /** 调用的后端接口 */
  api: (orderNo: string) => Promise<void>;
  /** 成功 toast 文案 */
  success: string;
}

/** 非支付类操作配置（订单列表与订单详情共用） */
const ACTION_TASKS: Partial<Record<OrderAction, ActionTask>> = {
  cancel: {
    title: "取消订单",
    content: "确定取消该订单吗？",
    api: orderCancel,
    success: "订单已取消",
  },
  confirmReceive: {
    title: "确认收货",
    content: "确认已收到设备？确认后租约开始生效",
    api: orderConfirmReceive,
    success: "已确认收货",
  },
  returnApply: {
    title: "申请归还",
    content: "申请后请按指引寄回设备",
    api: orderReturnApply,
    success: "归还申请已提交",
  },
};

/**
 * 执行订单操作（cancel/confirmReceive/returnApply）
 * 流程：确认弹窗 → 调接口 → 成功 toast → onSuccess 刷新
 * 失败/取消静默返回（请求层已 toast）
 *
 * 注意：pay 不在本流程内（金额展示与 mock 支付特殊），使用 execOrderPay
 */
export async function execOrderAction(
  action: OrderAction,
  orderNo: string,
  onSuccess: OrderActionSuccessHandler,
): Promise<void> {
  const task = ACTION_TASKS[action];
  if (!task) return;
  const { cancel } = await uni.showModal({ title: task.title, content: task.content });
  if (cancel) return;
  try {
    await task.api(orderNo);
    uni.showToast({ title: task.success, icon: "none" });
    await onSuccess();
  } catch {
    // 请求层已 toast
  }
}

/**
 * 发起支付（mock 渠道即时成功）
 * 流程：金额确认弹窗 → pay 接口 → 成功 toast → onSuccess 重查订单
 */
export async function execOrderPay(
  orderNo: string,
  payAmount: number,
  onSuccess: OrderActionSuccessHandler,
): Promise<void> {
  const { cancel } = await uni.showModal({
    title: "确认支付",
    content: `使用模拟支付通道支付 ¥${payAmount.toFixed(2)}？`,
  });
  if (cancel) return;
  try {
    await payOrderApi(orderNo);
    uni.showToast({ title: "支付成功", icon: "success" });
    await onSuccess();
  } catch {
    // 请求层已 toast
  }
}
