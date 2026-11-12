import { request } from '../request';

/**
 * Login
 *
 * the backend returns `{ token, userId, username, realName }`, only `token` is needed here
 */
export function fetchLogin(username: string, password: string) {
  return request<Api.Auth.LoginToken>({
    url: '/api/admin/auth/login',
    method: 'post',
    data: {
      username,
      password
    }
  });
}

/** Logout (invalidate the Sa-Token session on the backend) */
export function fetchLogout() {
  return request<null>({
    url: '/api/admin/auth/logout',
    method: 'post'
  });
}

/** raw response of GET /api/admin/auth/info */
interface AdminUserInfoResp {
  userId: number;
  username: string;
  realName: string | null;
  avatar: string | null;
  roles: string[];
  permissions: string[];
}

const emptyUserInfo: Api.Auth.UserInfo = {
  userId: '',
  userName: '',
  roles: [],
  buttons: []
};

/**
 * Get user info
 *
 * map the backend fields to the template shape:
 * `username -> userName`, `permissions -> buttons`, `userId(number) -> userId(string)`
 */
export async function fetchGetUserInfo(): Promise<{ data: Api.Auth.UserInfo; error: boolean }> {
  const { data, error } = await request<AdminUserInfoResp>({ url: '/api/admin/auth/info' });

  if (error) {
    return { data: emptyUserInfo, error: true };
  }

  return {
    error: false,
    data: {
      userId: String(data.userId),
      userName: data.username,
      roles: data.roles,
      buttons: data.permissions
    }
  };
}
