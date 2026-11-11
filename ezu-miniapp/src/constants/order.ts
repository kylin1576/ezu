/**
 * 订单域常量（与后端 t_order.status / t_rental_plan.unit 对齐）
 */

/** 订单状态枚举值 */
export const ORDER_STATUS = {
  /** 待支付 */
  PENDING_PAY: 10,
  /** 待发货 */
  PENDING_SHIP: 20,
  /** 已发货 */
  SHIPPED: 30,
  /** 租赁中 */
  RENTING: 40,
  /** 归还中 */
  RETURNING: 50,
  /** 验收中 */
  CHECKING: 60,
  /** 已完成 */
  FINISHED: 70,
  /** 已取消 */
  CANCELLED: 80,
} as const;

/** 状态 → 文案（全 8 态，单一来源） */
export const ORDER_STATUS_TEXT: Record<number, string> = {
  [ORDER_STATUS.PENDING_PAY]: "待支付",
  [ORDER_STATUS.PENDING_SHIP]: "待发货",
  [ORDER_STATUS.SHIPPED]: "已发货",
  [ORDER_STATUS.RENTING]: "租赁中",
  [ORDER_STATUS.RETURNING]: "归还中",
  [ORDER_STATUS.CHECKING]: "验收中",
  [ORDER_STATUS.FINISHED]: "已完成",
  [ORDER_STATUS.CANCELLED]: "已取消",
};

/** 订单状态 → 展示色（横幅/角标） */
export const ORDER_STATUS_COLOR: Record<number, string> = {
  [ORDER_STATUS.PENDING_PAY]: "#e93b3d",
  [ORDER_STATUS.PENDING_SHIP]: "#f59e0b",
  [ORDER_STATUS.SHIPPED]: "#3b82f6",
  [ORDER_STATUS.RENTING]: "#10b981",
  [ORDER_STATUS.RETURNING]: "#8b5cf6",
  [ORDER_STATUS.CHECKING]: "#6366f1",
  [ORDER_STATUS.FINISHED]: "#999999",
  [ORDER_STATUS.CANCELLED]: "#999999",
};

/** 订单列表 tab 配置（与我的页 4 入口对应） */
export interface OrderTabConfig {
  /** tab 标识（存 STORAGE_KEYS.ORDER_TAB） */
  key: string;
  title: string;
  /** 对应后端订单状态；undefined 表示全部 */
  status?: number;
}

export const ORDER_TABS: OrderTabConfig[] = [
  { key: "all", title: "全部" },
  { key: "pay", title: ORDER_STATUS_TEXT[ORDER_STATUS.PENDING_PAY], status: ORDER_STATUS.PENDING_PAY },
  { key: "ship", title: ORDER_STATUS_TEXT[ORDER_STATUS.PENDING_SHIP], status: ORDER_STATUS.PENDING_SHIP },
  { key: "renting", title: ORDER_STATUS_TEXT[ORDER_STATUS.RENTING], status: ORDER_STATUS.RENTING },
  { key: "return", title: ORDER_STATUS_TEXT[ORDER_STATUS.RETURNING], status: ORDER_STATUS.RETURNING },
];

/** 订单卡片/详情可执行操作 */
export type OrderAction = "pay" | "cancel" | "confirmReceive" | "returnApply";

/** 按状态返回可执行操作按钮（顺序即展示顺序） */
export function getOrderActions(status: number): OrderAction[] {
  switch (status) {
    case ORDER_STATUS.PENDING_PAY:
      return ["cancel", "pay"];
    case ORDER_STATUS.PENDING_SHIP:
      return ["cancel"];
    case ORDER_STATUS.SHIPPED:
      return ["confirmReceive"];
    case ORDER_STATUS.RENTING:
      return ["returnApply"];
    default:
      return [];
  }
}

/** 操作按钮文案 */
export const ORDER_ACTION_TEXT: Record<OrderAction, string> = {
  pay: "去支付",
  cancel: "取消订单",
  confirmReceive: "确认收货",
  returnApply: "申请归还",
};
