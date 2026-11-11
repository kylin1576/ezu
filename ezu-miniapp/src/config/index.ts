/**
 * 全局配置常量
 */

/** 本地存储 key */
export const STORAGE_KEYS = {
  /** 登录 token（与后端请求头 ezu-token 对应） */
  TOKEN: "ezu-token",
  /** 用户信息缓存 */
  USER_INFO: "ezu-user-info",
} as const;

/** token 请求头名称（后端 Sa-Token 双账号体系约定） */
export const TOKEN_HEADER = "ezu-token";

/** 登录页路由 */
export const LOGIN_PAGE = "/pages/login/index";

/**
 * 后端服务基地址
 * - H5：空串，走 vite dev server 代理（/api -> http://localhost:9000）
 * - 小程序/App：直连本地后端
 */
let BASE_URL: string;
// #ifdef H5
BASE_URL = "";
// #endif
// #ifndef H5
BASE_URL = "http://localhost:9000";
// #endif
export { BASE_URL };

/** 请求超时时间（毫秒） */
export const REQUEST_TIMEOUT = 10_000;
