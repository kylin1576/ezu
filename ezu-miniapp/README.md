# ezu-miniapp —— 易租用户端小程序

微信 / 支付宝双端小程序（uni-app + Vue3 + TS + pinia + wot-design-uni）。视觉参考京东购物小程序（白底 + 京东红 `#E93B3D`）。

> 当前状态：M3b 前端骨架已就绪——目录结构 / tabBar / 全部页面 / 请求封装 / 登录闭环。业务数据均为本地 mock（标注 `TODO(M3c)`），后端 M3a 的 C 端接口（T3+）完成后即可联调。

## 启动

```bash
npm install

# H5 开发（推荐日常开发；/api 自动代理到 localhost:9000）
npm run dev:h5

# 微信小程序（需自装微信开发者工具）
npm run dev:mp-weixin
# 工具中导入 dist/dev/mp-weixin 目录

# 支付宝小程序
npm run dev:mp-alipay

# 类型检查
npm run type-check
```

### 微信开发者工具注意事项

- **appid**：`src/manifest.json` 中 `mp-weixin.appid` 当前留空（测试号模式可开发）；上线前替换为正式 appid
- **合法域名**：`urlCheck: false` 已配置；若工具仍拦截本地请求，在「详情 → 本地设置」勾选「不校验合法域名」
- 后端本地端口为 `9000`，小程序直连 `http://localhost:9000`（`src/config/index.ts` 中按平台条件编译）

## 目录结构

```
src/
├── main.ts               # createSSRApp + pinia
├── App.vue               # onLaunch 水合用户 store + wot 主题变量 + 全局工具类
├── pages.json            # easycom（wd-*）+ 页面注册 + tabBar
├── uni.scss              # 京东红设计 token（$ezu-*）
├── manifest.json         # appid 留空（测试号模式）
├── config/index.ts       # STORAGE_KEYS / BASE_URL（条件编译）/ 超时
├── types/api.d.ts        # ApiResponse / PageQuery / PageVo
├── utils/request.ts      # uni.request Promise 封装（核心）
├── utils/auth.ts         # ensureLogin() 敏感入口前置拦截
├── api/                  # 按域组织：auth / user（后端契约映射）
├── stores/user.ts        # pinia 用户 store（登录态持久化）
├── pages/
│   ├── index/            # tab1 首页：搜索 + banner + 金刚区 + 推荐流（mock）
│   ├── category/         # tab2 分类：左右分栏（mock）
│   ├── order/            # tab3 订单：状态 tab + 空状态；confirm/detail 占位
│   ├── mine/             # tab4 我的：用户卡双态 + 订单入口 + 工具栏
│   ├── login/            # 登录页（闭环，非占位）
│   ├── goods/            # list / detail 占位（M3c）
│   └── address/          # list / edit 占位（M3c）
└── static/
```

## 后端联调约定

- 统一响应信封 `{code, msg, data}`：HTTP 恒 200，`code === 200` 为成功（`docs/04-API规范.md`）
- token：请求头 **`ezu-token`**（Sa-Token 双账号体系，B/C 端 loginType 隔离）
- C 端接口前缀 `/api/app`；登录 `POST /api/app/auth/login`（后端 mock 阶段任意 code 可换 token）
- **后端未就绪时**：404 → toast「接口暂未开放」、连接失败 → toast「网络连接失败」，不白屏

## 登录闭环（无全局路由守卫，分层拦截）

1. **启动水合**：`App.vue` onLaunch 实例化 store（state 自动读 storage），有 token 则静默刷新用户信息
2. **请求层最终防线**：响应 `code === 401` → 清凭证 + 防抖 `reLaunch` 登录页
3. **交互层前置**：下单/地址等敏感入口调用 `ensureLogin()`
4. **订单 tab**：未登录显示「登录后查看」软引导（不强跳）

## 开发约定

- 设计 token 统一用 `uni.scss` 的 `$ezu-*` 变量；组件库主题色经 `App.vue` 的 `--wot-color-theme` 全局覆盖
- 业务数据 mock 一律标注 `TODO(M3c)`，接口就绪后按 `api/` 层逐域替换
- easycom（`pages.json` 的 custom 映射）修改后需重启 dev server
