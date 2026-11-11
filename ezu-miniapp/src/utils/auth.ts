import { LOGIN_PAGE } from "@/config";
import { useUserStore } from "@/stores/user";

/**
 * 敏感入口前置拦截：未登录则跳登录页
 *
 * 用法（下单/地址管理等入口）：
 * ```ts
 * if (!(await ensureLogin())) return;
 * ```
 *
 * @returns 是否已登录（false 时已发起跳转）
 */
export async function ensureLogin(): Promise<boolean> {
  const userStore = useUserStore();
  if (userStore.isLoggedIn) return true;
  uni.navigateTo({ url: LOGIN_PAGE });
  return false;
}
