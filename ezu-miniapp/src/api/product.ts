import { get, post } from "@/utils/request";
import type { PageVo } from "@/types/api";

/** 品类树节点（后端 CategoryTreeVo，两级） */
export interface CategoryTreeVo {
  id: number;
  parentId?: number;
  name: string;
  icon?: string;
  sort?: number;
  children?: CategoryTreeVo[];
}

/** 商品列表项（后端 AppProductListVo） */
export interface AppProductListVo {
  id: number;
  productNo?: string;
  name: string;
  subtitle?: string;
  mainImage?: string;
  brand?: string;
  categoryName?: string;
  /** 最低租金（启用 SKU 启用方案的最低价） */
  minRentPrice: number;
  /** 最低租金对应计费单位（1/2/3/4） */
  minRentUnit: number;
  /** 在库可租设备数合计 */
  stockCount?: number;
}

/** 商品列表查询入参 */
export interface ProductQueryReq {
  pageNum: number;
  pageSize: number;
  /** 二级品类 id */
  categoryId?: number;
  brand?: string;
  /** 模糊匹配 name|subtitle */
  keyword?: string;
}

/** 租金方案（后端 t_rental_plan 投影） */
export interface RentalPlanVo {
  id: number;
  /** 计费单位（1=日 2=周 3=月 4=季） */
  unit: number;
  /** 单周期价格（元） */
  price: number;
  /** 最少租用天数 */
  minDays: number;
}

/** SKU（后端 AppProductDetailVo.skus 元素） */
export interface SkuVo {
  id: number;
  skuNo?: string;
  specName: string;
  specImage?: string;
  /** 押金（元） */
  deposit: number;
  /** 买断价（元，可选） */
  buyoutPrice?: number;
  rentalPlans: RentalPlanVo[];
  /** 在库可租设备数 */
  stockCount?: number;
}

/** 商品参数键值对 */
export interface ProductParamVo {
  name: string;
  value: string;
}

/** 商品详情（后端 AppProductDetailVo） */
export interface AppProductDetailVo {
  id?: number;
  name: string;
  subtitle?: string;
  mainImage?: string;
  images?: string[];
  params?: ProductParamVo[];
  skus: SkuVo[];
  stockCount?: number;
}

/** 品类两级树（仅启用品类；浏览面接口，失败由调用方降级 mock） */
export function categoryTree(): Promise<CategoryTreeVo[]> {
  return get<CategoryTreeVo[]>("/api/app/product/category/list", undefined, false);
}

/** 商品分页列表（浏览面接口，失败由调用方降级 mock） */
export function productList(data: ProductQueryReq): Promise<PageVo<AppProductListVo>> {
  return post<PageVo<AppProductListVo>>("/api/app/product/list", data, false);
}

/** 商品详情（浏览面接口，失败由调用方降级 mock） */
export function productDetail(id: number): Promise<AppProductDetailVo> {
  return get<AppProductDetailVo>(`/api/app/product/detail/${id}`, undefined, false);
}
