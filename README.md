# Login & RBAC System (Practice)

一個練習用的前後端登入 + RBAC 系統，包含 Angular 前端、Spring Boot/Security 後端、PostgreSQL、Nginx（開發階段反向代理），並附完整 SA/SD 文件。

## 功能概述
- 登入/登出、Access/Refresh Token、鎖定策略（5 次錯誤鎖 15 分鐘）。
- RBAC：角色/權限 CRUD、使用者角色指派、權限檢查。
- 稽核：登入/登出/拒絕/角色變更記錄與查詢。
- API 文件：Swagger UI `/swagger-ui.html`。

## 目錄重點
- `backend/`：Spring Boot 3 + Spring Security (JWT Resource Server) + JPA + Flyway。
- `frontend/`：Angular 17 + Tailwind + Router Guard + HTTP Interceptor。
- `nginx/`、`docker-compose.yml`：開發用 Nginx 反向代理與 PostgreSQL。
- `docs/sa`：SA（Use Case、RBAC 模型、流程、API 規格、資料模型、安全政策、驗收案例）。
- `docs/sd`：SD（架構圖、部署流程、效能/安全回歸指引、日誌/監控）。

## 先決條件
- Java 17、Maven
- Node.js (npm)
- Docker（啟動 Postgres / Nginx）

## 快速啟動
1) 啟動 PostgreSQL（docker-compose，僅 DB）
```bash
docker compose up -d db
# DB: jdbc:postgresql://localhost:5432/loginapp, user/pass: appuser/apppass
```

2) 啟動後端（dev profile）
```bash
cd backend
mvn spring-boot:run -Pdev
# 健康檢查: http://localhost:8080/api/health
# Swagger UI : http://localhost:8080/swagger-ui.html
```

3) 啟動前端（Angular dev server）
```bash
cd frontend
npm install
npm run start
# 前端預設 http://localhost:4200/login
```

4) （可選）啟動 Nginx 作為開發反向代理
```bash
docker compose up -d nginx
# 反向代理 http://localhost → 前端/後端
```

## 測試與覆蓋率
- 後端單元/整合測試：
```bash
cd backend
mvn test
# JaCoCo 報告：backend/target/site/jacoco/index.html
```
- 前端測試：
```bash
cd frontend
npm test
```

## 開發注意事項
- DB 使用 PostgreSQL（Flyway 使用 uuid/inet 型別）；需先啟動 DB。
- JWT secret 請於 `application-dev.yml` / `application-prod.yml` 設定。
- `docs/sd/security-regression.md`：安全/稽核回歸清單；`docs/sd/perf-check.md`：效能檢查指引。
- 程式與文件須符合 `docs/sa` / `docs/sd` 原則；完成任務記得更新 `specs/001-login-system/tasks.md`。

## 主要 API（摘要）
- 認證：`POST /api/auth/login`、`POST /api/auth/refresh`、`POST /api/auth/logout`
- 使用者資訊：`GET /api/me`
- 角色/權限：`GET/POST/PUT/DELETE /api/roles`、`GET /api/permissions`、`PUT /api/users/{username}/roles`
- 稽核：`GET /api/audit`（可依 user/resource/action/decision/from/to 篩選）

## 其他
- 反向代理設定：`nginx/nginx.conf`（預留 TLS）。
- 日誌/監控：`docs/sd/logging-monitoring.md`。
- 效能與安全回歸：`docs/sd/perf-check.md`、`docs/sd/security-regression.md`。
