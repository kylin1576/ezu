package com.ezu.common.satoken;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;

/**
 * C 端用户账号体系（loginType=user）
 */
public class StpUserUtil {

    public static final StpLogic stpLogic = new StpLogic("user");

    public static void login(Object id) { stpLogic.login(id); }
    public static void logout() { stpLogic.logout(); }
    public static long getUserId() { return stpLogic.getLoginIdAsLong(); }
    public static boolean isLogin() { return stpLogic.isLogin(); }
    public static void checkLogin() { stpLogic.checkLogin(); }
    public static String getTokenValue() { return stpLogic.getTokenValue(); }
    public static SaSession getSession() { return stpLogic.getSession(); }
}
