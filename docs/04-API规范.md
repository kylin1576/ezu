# 易租（ezu）API 规范

> 版本：v0.1（骨架 + 全局约定）
> 日期：2026-11-10
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

## 2. 模块接口清单（随开发补充）

### 2.1 认证模块
（待补充：C 端微信/支付宝登录、手机号绑定、B 端登录）

### 2.2 商品模块
（待补充：品类、SPU/SKU、租金方案、上下架、搜索）

### 2.3 订单模块
（待补充：下单试算、创建、取消、发货、归还、验收）

### 2.4 设备资产模块
（待补充：SN 台账、入库、质检、维修）

### 2.5 支付模块
（待补充：Mock 支付、回调、退款）

### 2.6 AI 模块
（待补充：SSE 聊天、知识库 CRUD、文档向量化、会话记录）

### 2.7 后台系统模块
（待补充：账号、角色、权限、操作日志、仪表盘）
