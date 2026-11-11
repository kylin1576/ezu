import { get, post } from "@/utils/request";
import type { PageVo } from "@/types/api";

/** 下单试算返回（后端 TrialVo） */
export interface OrderTrialVo {
  /** 单周期价格 */
  unitPrice: number;
  /** 总租金 */
  totalRent: number;
  /** 押金 */
  deposit: number;
  /** 运费（一期恒 0） */
  freight: number;
  /** 应付金额 = totalRent + freight */
  payAmount: number;
}

/** 下单试算入参 */
export interface OrderTrialReq {
  skuId: number;
  rentalPlanId: number;
  rentDays: number;
}

/** 创建订单返回（后端 CreateVo） */
export interface OrderCreateVo {
  orderNo: string;
  payAmount: number;
}

/** 创建订单入参 */
export interface OrderCreateReq {
  skuId: number;
  rentalPlanId: number;
  rentDays: number;
  addressId: number;
  remark?: string;
}

/** 订单商品明细（快照） */
export interface OrderItemVo {
  productName: string;
  skuSpecName: string;
  skuImage?: string;
  priceSnapshot: number;
  depositSnapshot: number;
}

/** 订单租约（确认收货后生成） */
export interface OrderLeaseVo {
  /** 1=正常租约 */
  type?: number;
  startDate?: string;
  endDate?: string;
  rent?: number;
}

/** 订单账单（type=1 租金；status 1=已支付 2=待支付 3=已退款） */
export interface OrderBillVo {
  billNo?: string;
  type?: number;
  amount?: number;
  status?: number;
  payTime?: string;
}

/** 押金流水（type=1 收取 2=退还） */
export interface OrderDepositVo {
  recordNo?: string;
  type?: number;
  amount?: number;
  reason?: string;
  createTime?: string;
}

/** 物流信息（type=1 寄出 2=寄回；status=1 已发出） */
export interface OrderLogisticsVo {
  type?: number;
  company?: string;
  logisticsNo?: string;
  status?: number;
  createTime?: string;
}

/**
 * 订单详情（主表字段 + item/lease/bill/deposit/logistics）
 * 字段与 t_order/t_order_item DDL 对齐
 */
export interface OrderDetailVo {
  orderNo: string;
  status: number;
  totalRent: number;
  depositAmount: number;
  payAmount: number;
  freight?: number;
  /** 计费单位（1/2/3/4） */
  leaseUnit: number;
  /** 单价快照（元/周期） */
  unitPrice: number;
  rentDays: number;
  rentStartDate?: string;
  rentEndDate?: string;
  receiver: string;
  receiverPhone: string;
  receiverAddress: string;
  cancelReason?: string;
  remark?: string;
  createTime: string;
  item?: OrderItemVo;
  lease?: OrderLeaseVo;
  bill?: OrderBillVo;
  deposit?: OrderDepositVo;
  logistics?: OrderLogisticsVo;
}

/**
 * 订单列表项（按详情主表 + item 投影；OrderListVo 字段未最终定形，模板按可选渲染）
 */
export interface OrderListVo extends Omit<OrderDetailVo, "item" | "lease" | "bill" | "deposit" | "logistics"> {
  item?: OrderItemVo;
}

/** 订单列表查询入参 */
export interface OrderQueryReq {
  pageNum: number;
  pageSize: number;
  /** 订单状态（可选，缺省=全部） */
  status?: number;
}

/** 下单试算（确认页展示金额以服务端为准） */
export function orderTrial(data: OrderTrialReq): Promise<OrderTrialVo> {
  return post<OrderTrialVo>("/api/app/order/trial", data);
}

/** 创建订单（锁设备 → 建单 → 待支付） */
export function orderCreate(data: OrderCreateReq): Promise<OrderCreateVo> {
  return post<OrderCreateVo>("/api/app/order/create", data);
}

/** 订单详情（本人校验） */
export function orderDetail(orderNo: string): Promise<OrderDetailVo> {
  return get<OrderDetailVo>(`/api/app/order/detail/${orderNo}`);
}

/** 订单分页列表（只查本人） */
export function orderList(data: OrderQueryReq): Promise<PageVo<OrderListVo>> {
  return post<PageVo<OrderListVo>>("/api/app/order/list", data);
}

/** 取消订单（status∈{10,20}→80，已锁设备释放） */
export function orderCancel(orderNo: string): Promise<void> {
  return post<void>(`/api/app/order/cancel/${orderNo}`);
}

/** 确认收货（30→40，生成租约） */
export function orderConfirmReceive(orderNo: string): Promise<void> {
  return post<void>(`/api/app/order/confirmReceive/${orderNo}`);
}

/** 申请归还（40→50） */
export function orderReturnApply(orderNo: string): Promise<void> {
  return post<void>(`/api/app/order/returnApply/${orderNo}`);
}
