import { get } from "@/utils/request";

/** C 端用户信息（不含实名/身份证等预留字段） */
export interface UserVo {
  userId: number;
  nickname: string;
  avatar: string;
  phone?: string;
}

/** 获取当前登录用户信息 */
export function getUserInfo(): Promise<UserVo> {
  return get<UserVo>("/api/app/user/info");
}
