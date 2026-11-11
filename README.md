# 易租（ezu）

自营设备租赁平台：电脑 / 摄像机 / 打印机等 3C 设备租赁，微信 + 支付宝双小程序 + Web 管理后台。

## 项目结构

| 目录 | 说明 | 技术 |
|------|------|------|
| `ezu-common` 等 8 个 Maven 模块 | 后端（ezu-startup 启动） | JDK21 / Spring Boot 4.1 / MyBatis-Plus / Sa-Token / Redis |
| `ezu-miniapp/` | 用户端小程序（微信+支付宝） | uni-app + Vue3 + TS |
| `ezu-console/` | 管理后台 | soybean-admin（Vue3 + Element Plus + pnpm） |
| `docs/` | 需求/架构/数据库/API/计划文档 | - |
| `sql/` | 建表与数据脚本 | MySQL 8 |

## 快速开始

### 后端

```bash
# 1. 起基础设施（MySQL 8 + Redis 7）
docker compose up -d

# 2. 初始化数据库（执行建表脚本）
docker exec -i ezu-mysql mysql -uroot -pezu123456 ezu < sql/建表/01_用户域.sql
# （依次执行 01~08）

# 3. 启动后端（JDK 21）
JAVA_HOME="D:\tool\jdk21" mvn -pl ezu-startup -am spring-boot:run

# 4. 健康检查
curl http://localhost:9000/_health
```

### 管理后台

```bash
cd ezu-console && pnpm install && pnpm dev
```

### 小程序

```bash
cd ezu-miniapp && npm install
npm run dev:mp-weixin   # 微信开发者工具导入 dist/dev/mp-weixin
npm run dev:mp-alipay   # 支付宝开发者工具导入 dist/dev/mp-alipay
```

## 文档索引

- [01-PRD-产品需求文档](docs/01-PRD-产品需求文档.md)
- [02-架构设计](docs/02-架构设计.md)
- [03-数据库设计](docs/03-数据库设计.md)
- [04-API规范](docs/04-API规范.md)
- [05-开发计划](docs/05-开发计划.md)
