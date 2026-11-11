import { post } from "@/utils/request";

/** 登录渠道：1-微信 2-支付宝 */
export type LoginChannel = 1 | 2;

/** C 端登录返回 */
export interface AppLoginVo {
  token: string;
  userId: number;
  nickname: string;
  avatar: string;
  isNew: boolean;
}

export interface LoginReq {
  channel: LoginChannel;
  code: string;
}

/**
 * C 端登录（后端 mock：不校验 code 真伪，任意 code 可换 token）
 * autoRedirect=false：登录接口自身 401 不跳转，防循环
 */
export function login(data: LoginReq): Promise<AppLoginVo> {
  return post<AppLoginVo>("/api/app/auth/login", data, false);
}

/** 退出登录 */
export function logout(): Promise<void> {
  return post<void>("/api/app/auth/logout");
}
