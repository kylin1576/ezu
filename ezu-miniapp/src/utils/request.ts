import type { ApiResponse } from "@/types/api";
import { BASE_URL, LOGIN_PAGE, REQUEST_TIMEOUT, STORAGE_KEYS, TOKEN_HEADER } from "@/config";
import { useUserStore } from "@/stores/user";

/** 业务错误：HTTP 200 但 code !== 200 时 reject */
export class ApiError extends Error {
  readonly code: number;

  constructor(code: number, msg: string) {
    super(msg);
    this.name = "ApiError";
    this.code = code;
  }
}

export interface RequestOptions {
  url: string;
  method?: "GET" | "POST" | "PUT" | "DELETE";
  /** 请求参数/请求体（可传类型化 Req 接口对象，AnyObject=Record<string,any>） */
  data?: AnyObject;
  header?: Record<string, string>;
  /** 401 时是否自动跳登录页（登录/登出接口传 false 防循环跳转） */
  autoRedirect?: boolean;
}

/** 401 跳登录页节流：窗口内的并发/迟到的 401 只触发一次跳转 */
const REDIRECT_THROTTLE_MS = 1500;
let lastRedirectAt = 0;

function redirectToLogin(): void {
  const now = Date.now();
  if (now - lastRedirectAt < REDIRECT_THROTTLE_MS) return;
  const currentRoute = getCurrentPages().pop()?.route ?? "";
  if (currentRoute.includes("pages/login/index")) return;
  lastRedirectAt = now;
  // 走 store 清理：同时清 pinia 内存态与本地 storage，保持一致
  useUserStore().clearLocal();
  uni.showToast({ title: "请先登录", icon: "none" });
  setTimeout(() => {
    uni.reLaunch({ url: LOGIN_PAGE });
  }, 600);
}

/** 校验响应体符合统一信封结构 */
function isEnvelope(body: unknown): body is ApiResponse<unknown> {
  return (
    typeof body === "object" &&
    body !== null &&
    typeof (body as ApiResponse<unknown>).code === "number"
  );
}

/**
 * uni.request Promise 封装
 * - header 自动注入 ezu-token
 * - HTTP 非 200（如后端未实现的 404）→ toast「接口暂未开放」，避免白屏
 * - 信封格式异常 → 按网络异常处理（避免 dereference 崩溃导致 promise 悬挂）
 * - code 401 → 清凭证 + 防抖跳登录页
 * - 其他 code !== 200 → toast msg + reject ApiError
 */
export function request<T>(options: RequestOptions): Promise<T> {
  const { url, method = "GET", data, autoRedirect = true } = options;
  // 浅拷贝 header，避免污染调用方传入的对象
  const header: Record<string, string> = { ...options.header };

  const token = uni.getStorageSync(STORAGE_KEYS.TOKEN) as string;
  if (token) {
    header[TOKEN_HEADER] = token;
  }

  return new Promise<T>((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      timeout: REQUEST_TIMEOUT,
      success: (res) => {
        if (res.statusCode !== 200) {
          // 后端接口尚未实现（404）或网关异常
          uni.showToast({ title: "接口暂未开放", icon: "none" });
          reject(new ApiError(res.statusCode, "接口暂未开放"));
          return;
        }
        // 信封格式校验：非 {code,msg,data} 结构按服务异常处理
        if (!isEnvelope(res.data)) {
          uni.showToast({ title: "服务异常，请稍后重试", icon: "none" });
          reject(new ApiError(-2, "响应格式异常"));
          return;
        }
        const body = res.data as ApiResponse<T>;
        if (body.code === 200) {
          resolve(body.data);
          return;
        }
        if (body.code === 401) {
          if (autoRedirect) redirectToLogin();
          reject(new ApiError(401, body.msg || "请先登录"));
          return;
        }
        uni.showToast({ title: body.msg || "请求失败", icon: "none" });
        reject(new ApiError(body.code, body.msg || "请求失败"));
      },
      fail: () => {
        // 网络层失败：后端未启动 / 断网
        uni.showToast({ title: "网络连接失败", icon: "none" });
        reject(new ApiError(-1, "网络连接失败"));
      },
    });
  });
}

export function get<T>(url: string, data?: AnyObject): Promise<T> {
  return request<T>({ url, method: "GET", data });
}

export function post<T>(url: string, data?: AnyObject, autoRedirect = true): Promise<T> {
  return request<T>({ url, method: "POST", data, autoRedirect });
}
