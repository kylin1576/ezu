import { ref } from "vue";
import type { Ref } from "vue";
import type { PageVo } from "@/types/api";

/** 分页请求函数签名 */
export type PageFetcher<T> = (pageNum: number, pageSize: number) => Promise<PageVo<T>>;

/**
 * 列表页分页状态机
 * - load()：首页加载 + onReachBottom 加载更多（幂等：同代请求 loading 中直接返回）
 * - reset()：条件变化（tab/搜索词切换、下拉刷新）后清空重查
 * - epoch 代次：reset 自增代次，使 in-flight 旧响应过期丢弃，且新代请求不被旧 loading 短路
 *
 * 用法：
 * ```ts
 * const pager = usePagination((page, size) => orderList({ pageNum: page, pageSize: size, status }))
 * pager.load()
 * ```
 */
export function usePagination<T>(fetcher: PageFetcher<T>, pageSize = 10) {
  const list: Ref<T[]> = ref([]);
  const total = ref(0);
  const loading = ref(false);
  const finished = ref(false);
  let pageNum = 1;
  /** 请求代次（reset 时自增） */
  let epoch = 0;
  /** 当前 loading 标志归属的代次 */
  let loadingEpoch = 0;

  async function load(): Promise<void> {
    if (finished.value) return;
    // 仅同代请求互斥；reset 后的新代次放行（旧请求响应会被代次校验丢弃）
    if (loading.value && loadingEpoch === epoch) return;
    const current = epoch;
    loadingEpoch = current;
    loading.value = true;
    try {
      const page = await fetcher(pageNum, pageSize);
      // 过期响应：reset 已发生，丢弃整批数据
      if (current !== epoch) return;
      // 后端 PageVo.list 可能 null（空页），防御性兜底
      const rows = page?.list ?? [];
      list.value = pageNum === 1 ? rows : [...list.value, ...rows];
      total.value = page?.total ?? list.value.length;
      if (list.value.length >= total.value || rows.length < pageSize) {
        finished.value = true;
      } else {
        pageNum += 1;
      }
    } catch {
      // 请求层已 toast；首屏失败保持空列表让页面显示空态
      if (pageNum === 1 && current === epoch) finished.value = true;
    } finally {
      // 仅当前代次可清除 loading，防旧请求 finally 误清新请求的加载态
      if (current === epoch) loading.value = false;
    }
  }

  /** 清空并回到第一页（不自动发起请求，由调用方决定时机） */
  function reset(): void {
    epoch += 1;
    pageNum = 1;
    list.value = [];
    total.value = 0;
    finished.value = false;
  }

  return { list, total, loading, finished, load, reset };
}
