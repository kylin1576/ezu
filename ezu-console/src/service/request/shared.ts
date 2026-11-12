import { localStg } from '@/utils/storage';
import type { RequestInstanceState } from './type';

/**
 * get the token headers for ezu backend
 *
 * the backend uses Sa-Token with header name `ezu-token`, and the value is the raw token (no Bearer prefix)
 */
export function getTokenHeaders() {
  const token = localStg.get('token');

  return token ? { 'ezu-token': token } : null;
}

export function showErrorMsg(state: RequestInstanceState, message: string) {
  if (!state.errMsgStack?.length) {
    state.errMsgStack = [];
  }

  const isExist = state.errMsgStack.includes(message);

  if (!isExist) {
    state.errMsgStack.push(message);

    window.$message?.error(message, {
      onLeave: () => {
        state.errMsgStack = state.errMsgStack.filter(msg => msg !== message);

        setTimeout(() => {
          state.errMsgStack = [];
        }, 5000);
      }
    });
  }
}
