/**
 * 后端 API 契约类型定义
 */

/** 后端统一响应结构（HTTP 恒 200，业务成败看 code，200 为成功） */
export interface ApiResponse<T = unknown> {
  code: number;
  msg: string;
  data: T;
}

/** 分页查询参数（POST body 携带） */
export interface PageQuery {
  pageNum: number;
  pageSize: number;
}

/** 分页返回结构 */
export interface PageVo<T> {
  total: number;
  list: T[];
}
