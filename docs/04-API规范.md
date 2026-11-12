# 易租（ezu）API 规范

> 版本：v0.2（M3a 后端核心闭环已回填 §2.1~2.5、2.7；2.6 AI 随 M4 补充）
> 日期：2026-11-12
> 状态：随各模块开发逐节补充

---

## 1. 全局约定

### 1.1 路径规范

| 端 | 前缀 | 认证 |
|----|------|------|
| C 端小程序 | `/api/app/**` | Sa-Token（C 端 loginType），登录/白名单接口除外 |
| B 端后台 | `/api/admin/**` | Sa-Token（B 端 loginType）+ RBAC 注解 |
| 支付回调 | `/api/callback/payment/{channel}` | 渠道签名校验，免登录 |

### 1.2 请求/响应

- 请求：`POST` + JSON body 为主（查询条件复杂）；简单详情用 `GET /{no}` 路径参数
- 统一响应信封：

```json
{ "code": 200, "msg": "success", "data": {} }
```

- 业务异常 HTTP 仍返回 200，以 `code` 判断成败（与 light-supplyhub 一致）
- 错误码段规划：`200` 成功；`4xxxx` 客户端错误；`5xxxx` 服务端错误

### 1.3 通用规范（沿用 light-supplyhub 惯例）

- 列表查询：Req 继承 `PageQuery`（pageNum/pageSize），返回 `PageVo<T>`
- 批量操作（删除/上下架等）：返回 `BatchOperateResultVo`（totalCount/successCount/skippedCount/successList）
- 单号交互：业务单据对外用单号（如 `orderNo`），不用数据库 ID
- 新增/修改复用 Req 时用 `@Validated({Insert.class, Update.class})` 分组校验
- 日期字段：`LocalDate`/`LocalDateTime` 必须加 `@JsonFormat`，注释标注格式
- 写接口加 `@RepeatSubmit` 防重复提交

## 2. 模块接口清单（M3a 已落地，请求/响应细节见各 Controller）

### 2.1 认证与用户模块（ezu-user / ezu-admin）

C 端（loginType=user，header `ezu-token`）：

| 接口 | 说明 |
|------|------|
| POST `/api/app/auth/login` | mock 登录：{channel(1微信/2支付宝), code}，openid=mock-{channel}-{code} 自动注册 → {token,userId,nickname,avatar,isNew} |
| POST `/api/app/auth/logout` | 登出 |
| GET `/api/app/user/info` | 当前用户信息 |
| POST `/api/app/user/address/list` | 地址簿分页（pageSize=-1 全量，只查本人） |
| POST `/api/app/user/address/save` | 地址新增/修改（isDefault=1 同事务清其他默认）→ addressId |
| POST `/api/app/user/address/delete/{id}` | 地址逻辑删（归属校验） |
| POST `/api/admin/appUser/list` | B 端 C 用户列表（一期只读，禁用/黑名单 M5） |

### 2.2 商品模块（ezu-product）

B 端：

| 接口 | 说明 |
|------|------|
| POST `/api/admin/product/category/list` | 品类两级树（全量，sort 升序） |
| POST `/api/admin/product/category/save` | 品类新增/修改（只允许两级） |
| POST `/api/admin/product/category/delete/{id}` | 品类删除（有子级/挂商品拦截） |
| POST `/api/admin/product/spu/list` | SPU 分页（品类名/SKU数/最低租金起回显） |
| POST `/api/admin/product/spu/save` | SPU+SKU+租金方案+参数**整体保存**（SKU 全删全插）→ productNo |
| GET `/api/admin/product/spu/detail/{id}` | SPU 详情（主表+SKU 各带方案+参数） |
| POST `/api/admin/product/spu/shelf` | 批量上下架 {ids, status(1/2)} → BatchOperateResultVo |

C 端（只暴露上架 SPU）：

| 接口 | 说明 |
|------|------|
| GET `/api/app/product/category/list` | 启用品类两级树 |
| POST `/api/app/product/list` | 商品分页（categoryId/brand/keyword；minRentPrice/stockCount 统计） |
| GET `/api/app/product/detail/{id}` | 商品详情（SKU 带租金方案与在库可租数） |

### 2.3 订单模块（ezu-order）

订单状态：10待支付 → 20待发货 → 30已发货 → 40租赁中 → 50归还中 → 60验收中 → 70已完成；10/20 → 80已取消。

C 端：

| 接口 | 说明 |
|------|------|
| POST `/api/app/order/trial` | 试算 {skuId,rentalPlanId,rentDays} → {unitPrice,totalRent,deposit,freight,payAmount} |
| POST `/api/app/order/create` | 下单（CAS 锁设备→快照→建单→待支付账单）→ {orderNo,payAmount} |
| POST `/api/app/order/list` | 本人订单分页（status 可选） |
| GET `/api/app/order/detail/{orderNo}` | 详情（明细/租约/账单/押金/物流，本人校验） |
| POST `/api/app/order/cancel/{orderNo}` | 取消（10/20→80，释放设备） |
| POST `/api/app/order/confirmReceive/{orderNo}` | 确认收货（30→40，生成初始租约+租期回写主表） |
| POST `/api/app/order/returnApply/{orderNo}` | 申请归还（40→50，设备 3→6） |

B 端：

| 接口 | 说明 |
|------|------|
| POST `/api/admin/order/list` | 全量分页（orderNo/userId/status/日期范围/overdue 逾期过滤） |
| GET `/api/admin/order/detail/{orderNo}` | 全量详情（含 userId） |
| POST `/api/admin/order/ship/{orderNo}` | 发货 {company,logisticsNo}（20→30，设备 2→3，记寄出物流） |
| POST `/api/admin/order/returnSign/{orderNo}` | 登记归还寄回物流（订单状态不变） |
| POST `/api/admin/order/acceptCheck/{orderNo}` | 归还验收（50→60，type=2 验收报告回写成色；pass=true 直接完成）→ Boolean 是否已完成 |
| POST `/api/admin/order/acceptConfirm/{orderNo}` | 验收完成确认（60→70，设备 6→1，押金退还流水） |

### 2.4 设备资产模块（ezu-product，B 端）

设备状态：1在库 2锁定 3在租 4维修 5报废(终态) 6归还验收中。

| 接口 | 说明 |
|------|------|
| POST `/api/admin/device/list` | 台账分页（skuId/status/keyword 模糊 sn·deviceNo；规格/商品名回显） |
| POST `/api/admin/device/inbound` | 批量入库 {skuId, items[]} → 设备编号列表（Z 单号，status=1） |
| POST `/api/admin/device/check/save` | 质检/验收报告（type=1 入库质检回写成色；type=2 归还验收） |
| POST `/api/admin/device/repair/save` | 维修登记（endDate 空=开始维修 3/1→4；非空=完成 →1） |
| POST `/api/admin/device/scrapped/{deviceId}` | 报废（1/4→5 终态） |
| GET `/api/admin/device/detail/{id}` | 详情（质检报告列表+维修记录列表） |

### 2.5 支付模块（ezu-payment）

| 接口 | 说明 |
|------|------|
| POST `/api/app/payment/pay` | 发起支付 {orderNo} → {paymentNo, status}（mock 渠道受理即回调成功；幂等复用待支付单/已支付返回原单） |
| POST `/api/callback/payment/{channel}` | 渠道回调 {paymentNo, channelTradeNo?}（免登录；条件更新防并发重复回调；biz_type=1 同事务驱动订单 10→20+账单已付+押金冻结流水） |

支付渠道抽象：`PaymentChannel` 接口（channelCode/pay），一期 MockPayChannel(1)，二期支付宝(2)/微信(3) 加实现类不改调用方。押金退款一期只记 t_deposit_record 流水，不动支付记录。

### 2.6 AI 模块
（待补充：SSE 聊天、知识库 CRUD、文档向量化、会话记录 —— M4）

### 2.7 后台系统模块（ezu-admin）

| 接口 | 说明 |
|------|------|
| POST `/api/admin/auth/login` | B 端登录 {username,password}（BCrypt）→ {token,userId,username,realName} |
| POST `/api/admin/auth/logout` | 登出 |
| GET `/api/admin/auth/info` | 当前管理员 {userId,username,realName,avatar,roles[],permissions[]} |
| POST `/api/admin/user/list` | 管理员账号分页（不返回密码） |
| POST `/api/admin/user/save` | 管理员新增/修改（角色绑定全删全插） |
| POST `/api/admin/role/list` | 角色分页/全量（pageSize=-1） |
| POST `/api/admin/role/save` | 角色新增/修改（code 查重） |

配套：启动初始化超管 `admin/admin123` + 角色/权限种子（幂等）；写接口 `@OperateLog(module,operation)` AOP 落 t_operate_log（查询接口 M5）；RBAC 细粒度鉴权（@SaCheckPermission）M3b/M5 落地。
