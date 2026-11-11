import { post } from "@/utils/request";

/** 支付返回（后端 PayVo；mock 渠道即时回调成功） */
export interface PayVo {
  paymentNo: string;
  /** 支付单状态（1=已支付） */
  status: number;
}

/** 发起支付（mock 即时成功，成功后重查订单详情即变待发货） */
export function payOrderApi(orderNo: string): Promise<PayVo> {
  return post<PayVo>("/api/app/payment/pay", { orderNo });
}
