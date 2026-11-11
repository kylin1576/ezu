package com.ezu.common.satoken;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;

/**
 * B 端管理员账号体系（loginType=admin）
 */
public class StpAdminUtil {

    public static final StpLogic stpLogic = new StpLogic("admin");

    public static void login(Object id) { stpLogic.login(id); }
    public static void logout() { stpLogic.logout(); }
    public static long getAdminUserId() { return stpLogic.getLoginIdAsLong(); }
    public static boolean isLogin() { return stpLogic.isLogin(); }
    public static void checkLogin() { stpLogic.checkLogin(); }
    public static String getTokenValue() { return stpLogic.getTokenValue(); }
    public static SaSession getSession() { return stpLogic.getSession(); }
}
