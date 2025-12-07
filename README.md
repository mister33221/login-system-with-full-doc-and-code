# Login & RBAC System (Practice)

一個練習用的前後端登入與 RBAC 系統，包含 Angular 前端、Spring Boot/Security 後端、PostgreSQL、Nginx 反向代理。功能涵蓋登入/登出、角色與權限、會話管理、Token 撤銷與稽核記錄。文件整理在 `docs/sa`（需求/分析）與 `docs/sd`（設計/架構），API 文件透過 Swagger UI 提供。

## 目的與功能
- 使用帳號密碼登入/登出，支援閒置逾時與 Token 撤銷（跨裝置）。
- 基於角色/權限的存取控制（RBAC），支援角色與權限的配置與查詢。
- 會話管理：列出/撤銷特定會話。
- 稽核記錄：登入、拒絕、角色/權限變更等事件。
- API 文件：Springdoc OpenAPI `/swagger-ui.html`。

## 主要技術
- 前端：Angular 17 + Tailwind、Router Guard、HTTP Interceptor。
- 後端：Spring Boot 3.x、Spring Security（JWT Resource Server）、Spring Data JPA、Flyway。
- DB：PostgreSQL。
- Proxy：Nginx（開發代理前端/後端）。
- API 文件：Springdoc OpenAPI + Swagger UI。

## 環境需求
- Node.js (npm)
- Java 17、Maven
- Docker（如需啟動 Postgres/Nginx via compose）

## 如何啟動
### 1) 啟動資料庫（PostgreSQL）
- 使用 docker-compose 只啟動 db 服務：
  ```bash
  docker compose up -d db
  ```
  資料庫預設：`jdbc:postgresql://localhost:5432/loginapp`，帳密 `appuser` / `apppass`。

### 2) 啟動後端（Spring Boot, dev profile）
```bash
cd backend
mvn spring-boot:run -Pdev
# 或
# mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
啟動後：
- 健康檢查：`http://localhost:8080/api/health`
- Swagger UI：`http://localhost:8080/swagger-ui.html`

### 3) 啟動前端（Angular dev server）
```bash
cd frontend
npm install
npm run start
```
- 前端 dev server 預設 `http://localhost:4200/login`
- 若有 Nginx 代理（docker-compose），可由 `http://localhost` 透過 Nginx 轉發。

> 註：目前後端功能尚未完成（登入/刷新/登出/角色/會話 API 未實作，前端仍為樣板存假 token）。需完成 API 並串接後再進行實測。

## 測試
- 後端：計畫使用 JUnit/AssertJ 寫單元/整合測試（尚未實作）。啟動 DB 後可執行：
  ```bash
  cd backend
  mvn test -Pdev
  ```
- 前端：可使用 Angular 測試工具（尚未撰寫）。預計在實作完成後加入單元/端對端測試。

## 文件
- 需求/分析（SA）：`docs/sa/`（Use Case、RBAC 模型、流程、API 草案、資料模型、安全政策、線框、測試案例等）。
- 設計/架構（SD）：`docs/sd/`（架構圖、部署/流程圖、API 文件說明）。
- API 文件：啟動後端後至 `/swagger-ui.html`。

## 注意事項
- 後端 dev profile 預設使用 PostgreSQL，啟動前請先啟動 DB。
- Flyway migrations 使用 PostgreSQL 專用型別/擴展（uuid/inet），確保 DB 為 Postgres。
- JWT secret 請在 `application-dev.yml` / `application-prod.yml` 設定安全值。
- 目前 admin 密碼種子為佔位 hash，實務需更新為真實 bcrypt。
