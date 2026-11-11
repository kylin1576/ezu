import { defineStore } from "pinia";
import {
  login as loginApi,
  logout as logoutApi,
  type AppLoginVo,
  type LoginChannel,
} from "@/api/auth";
import { getUserInfo, type UserVo } from "@/api/user";
import { STORAGE_KEYS } from "@/config";

interface UserState {
  token: string;
  userInfo: UserVo | null;
}

/** 冷启动水合：从本地 storage 读 token */
function readToken(): string {
  return (uni.getStorageSync(STORAGE_KEYS.TOKEN) as string) || "";
}

/** 冷启动水合：从本地 storage 读用户信息 */
function readUserInfo(): UserVo | null {
  const raw = uni.getStorageSync(STORAGE_KEYS.USER_INFO) as string;
  if (!raw) return null;
  try {
    return JSON.parse(raw) as UserVo;
  } catch {
    return null;
  }
}

/**
 * 获取登录 code
 * - 小程序：uni.login 换取平台临时凭证
 * - H5：固定 mock code（配合后端 mock 登录策略，浏览器无 uni.login）
 */
function getLoginCode(channel: LoginChannel): Promise<string> {
  // #ifdef MP
  const provider = channel === 1 ? "weixin" : "alipay";
  return new Promise((resolve, reject) => {
    uni.login({
      // @dcloudio/types 的 provider 枚举未收录 "alipay"，运行时实际支持
      provider: provider as "weixin",
      success: (res) => {
        if (res.code) {
          resolve(res.code);
        } else {
          reject(new Error("获取登录凭证失败"));
        }
      },
      fail: (err) => reject(new Error(err.errMsg || "获取登录凭证失败")),
    });
  });
  // #endif
  // #ifdef H5
  return Promise.resolve("dev-h5");
  // #endif
}

function persist(token: string, userInfo: UserVo): void {
  uni.setStorageSync(STORAGE_KEYS.TOKEN, token);
  uni.setStorageSync(STORAGE_KEYS.USER_INFO, JSON.stringify(userInfo));
}

export const useUserStore = defineStore("user", {
  // state 初始化即读 storage，冷启动自动水合
  state: (): UserState => ({
    token: readToken(),
    userInfo: readUserInfo(),
  }),
  getters: {
    isLoggedIn: (state): boolean => state.token !== "",
  },
  actions: {
    /** 登录：取 code → 调接口 → 持久化 */
    async login(channel: LoginChannel): Promise<AppLoginVo> {
      const code = await getLoginCode(channel);
      const vo = await loginApi({ channel, code });
      this.token = vo.token;
      this.userInfo = {
        userId: vo.userId,
        nickname: vo.nickname,
        avatar: vo.avatar,
      };
      persist(this.token, this.userInfo);
      return vo;
    },
    /** 退出：调接口（失败也继续）→ 清本地 */
    async logout(): Promise<void> {
      try {
        await logoutApi();
      } catch {
        // 后端不可用时仍完成本地登出
      }
      this.clearLocal();
    },
    /** 静默刷新用户信息（App onLaunch 有 token 时调用） */
    async fetchUserInfo(): Promise<void> {
      const info = await getUserInfo();
      this.userInfo = info;
      uni.setStorageSync(STORAGE_KEYS.USER_INFO, JSON.stringify(info));
    },
    /** 清空本地登录态 */
    clearLocal(): void {
      this.token = "";
      this.userInfo = null;
      uni.removeStorageSync(STORAGE_KEYS.TOKEN);
      uni.removeStorageSync(STORAGE_KEYS.USER_INFO);
    },
  },
});
