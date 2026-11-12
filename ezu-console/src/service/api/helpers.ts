import type { PaginationData } from '@sa/hooks';
import { request } from '../request';

/** ezu backend page query params (POST body of all list APIs) */
export interface PageQuery {
  pageNum: number;
  pageSize: number;
}

/** ezu backend page response envelope `{ total, list }` */
export interface PageResult<T = any> {
  total: number;
  list: T[];
}

/** flat page response in the PaginationData shape expected by useTable hooks */
export type PageResponse<T> = { error: true } | { error: false; data: PaginationData<T> };

/**
 * fetch a paginated list from the ezu backend (POST + `{ total, list }` envelope)
 *
 * the backend does not echo `pageNum/pageSize`, so the request params are echoed back
 * manually (the useTable hook needs them to keep the pagination state consistent)
 */
export async function fetchPage<T>(url: string, params: PageQuery & Record<string, unknown>): Promise<PageResponse<T>> {
  const { data, error } = await request<PageResult<T>>({ url, method: 'post', data: params });

  if (error) {
    return { error: true };
  }

  return {
    error: false,
    data: {
      data: data.list,
      pageNum: params.pageNum,
      pageSize: params.pageSize,
      total: data.total
    }
  };
}

/** transform passed to useTable options: unwrap PageResponse safely */
export function pageDataTransform<T>(response: PageResponse<T>): PaginationData<T> {
  if (response.error) {
    return { data: [], pageNum: 1, pageSize: 10, total: 0 };
  }

  return response.data;
}
