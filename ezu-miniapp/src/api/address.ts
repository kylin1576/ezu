import { post } from "@/utils/request";
import type { PageQuery, PageVo } from "@/types/api";

/** 收货地址（后端 AddressVo） */
export interface AddressVo {
  id: number;
  userId?: number;
  receiver: string;
  phone: string;
  province: string;
  city: string;
  district?: string;
  detailAddress: string;
  /** 默认地址（0=否，1=是） */
  isDefault: number;
}

/** 地址保存入参（id 空 = 新增） */
export interface AddressSaveReq {
  id?: number;
  receiver: string;
  phone: string;
  province: string;
  city: string;
  district?: string;
  detailAddress: string;
  isDefault: number;
}

/** 本人地址列表（pageSize=-1 全量） */
export function addressList(query: PageQuery): Promise<PageVo<AddressVo>> {
  return post<PageVo<AddressVo>>("/api/app/user/address/list", query);
}

/** 新增/编辑地址，返回 addressId */
export function addressSave(data: AddressSaveReq): Promise<number> {
  return post<number>("/api/app/user/address/save", data);
}

/** 删除地址（逻辑删） */
export function addressDelete(id: number): Promise<void> {
  return post<void>(`/api/app/user/address/delete/${id}`);
}
