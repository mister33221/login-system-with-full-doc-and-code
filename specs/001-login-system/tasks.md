---
description: "Task list for login & RBAC system"
---

# Tasks: Login & RBAC System

**Input**: Design documents from `/specs/001-login-system/`  
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: 依憲章，關鍵路徑必須具備單元、契約/整合、端對端測試與必要的效能檢查；僅能在規格明確排除且具理由時省略，否則預設納入。

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 0: SA/SD Docs（必須先完成）

- [X] T000 撰寫 SA/SD 文件初稿（根目錄，全部完成）：
  - `sa-functional-requirements.md`: 功能需求與 Use Case，列出登入/登出/權限所有情境（正常/例外）＋ User Stories（身為 X，我希望 Y，從而 Z）。
  - `sa-rbac-model.md`: RBAC 權限模型，User/Role/Permission/Resource 關係，角色清單與權限對應。
  - `sa-login-logout-flow.md`: 登入/登出全流程（前端→後端→DB），含成功、失敗、鎖定、逾時、撤銷。
  - `sa-authz-flow.md`: 驗證與授權流程，Token/Session 驗證、權限決策與拒絕行為。
  - `sa-auth-api-spec.md`: 認證/授權 API 規格（URL/Method/Request/Response/Error codes）：登入、刷新、登出/撤銷、目前使用者、權限/選單取得。
  - `sa-data-model.md`: 資料表設計與資料字典（User, Role, Permission, User_Role, Role_Permission, Session, AuditEvent...），欄位/型別/關聯/唯一性。
  - `sa-security-policies.md`: 密碼規則、鎖定策略（5 次 15m）、Token/Session 有效時間與撤銷、Cookie/SameSite/CORS/HTTPS 政策。
  - `sa-ux-wireframes.md`: Login、未授權、登出流程的 Wireframe/原型，含錯誤/空狀態/提示。
  - `sa-test-acceptance.md`: 測試案例與驗收條件（成功登入、密碼錯誤、鎖定、權限不足、Token 過期/撤銷、多裝置、逾時等）。

## Phase 1: Setup（Shared Infrastructure）

- [X] T001 準備 docker-compose 骨架與網路：`docker-compose.yml` (nginx/backend/db)
- [X] T002 建立 Nginx 基礎設定：`nginx/nginx.conf`（轉發 / → 前端 dev 伺服器、/api/** → backend，預留 TLS）
- [X] T003 設定 PostgreSQL service（官方 image、環境變數、volume）：`docker-compose.yml`
- [X] T004 建立 Flyway 初始 migration：`backend/src/main/resources/db/migration/V1__init.sql`
- [X] T005 初始化 Spring Boot 專案框架與模組目錄（api/application/domain/infrastructure）：`backend/`
- [X] T006 初始化 Angular 專案與 Tailwind、ESLint/Prettier：`frontend/`
  - 已建立基本路由/守衛/攔截器與樣板頁面（Login、Dashboard、Roles/Audit/Sessions、403/404）
- [X] T007 繪製系統架構圖（前端/後端/Nginx/PostgreSQL/docker-compose 網路），放置於 `docs/sd/sd-architecture.md`（或同層圖片引用）
- [X] T008 繪製部署與流程圖（含開發/本地環境：ng serve → Nginx → backend → db），放置於 `docs/sd/sd-deployment.md`

## Phase 2: Foundational（Blocking Prerequisites）

- [X] T009 [P] 設定 Spring Security/JWT 基礎配置（密鑰、過期時間、Filter）：`backend/src/main/java/.../infrastructure/security`
- [X] T010 定義 Domain/Entity/Repository 介面：User, Role, Permission, Session, AuditEvent：`backend/src/main/java/.../domain`
- [X] T011 [P] 實作 Repository（PostgreSQL + JPA/SQL）：`backend/src/main/java/.../infrastructure/repository`
- [X] T012 [P] 設定 OpenAPI/Swagger（Springdoc）：`/v3/api-docs`, `/swagger-ui.html`，並補充 README/文件說明
- [X] T013 統一錯誤回應格式與全域例外處理：`backend/src/main/java/.../api` & `.../infrastructure`
- [X] T014 設定應用組態分環境（dev/prod）：`backend/src/main/resources/application-dev.yml`, `application-prod.yml`
- [X] T015 [P] 前端 HTTP Interceptor（附帶/刷新 Token、處理 401/403）：`frontend/src/app/core/auth`
- [X] T016 Route Guard 保護受控頁面：`frontend/src/app/core/auth`
- [X] T017 建立 Tailwind 設定與共用 UI 樣式：`frontend/tailwind.config.js`, `frontend/src/styles.css`

## Phase 3: User Story 1 - 安全登入與登出體驗（P1） ✅ MVP

### Tests for User Story 1
- [ ] T016 [P] 後端單元測試：登入服務（成功/失敗/鎖定）：`backend/src/test/java/.../application`
- [ ] T017 [P] 後端整合測試：登入/登出/逾時/撤銷 Token：`backend/src/test/java/.../api`
- [ ] T018 前端 E2E/整合（可選）：登入成功/失敗流程：`frontend/`（若時程允許）

### Implementation for User Story 1
- [ ] T019 登入 API（發行 access/refresh，寫入 Session/Audit）：`backend/src/main/java/.../api/AuthController`
- [ ] T020 登出/撤銷 API（失效 access+refresh，跨裝置阻擋）：`backend/src/main/java/.../api/AuthController`
- [ ] T021 閒置逾時與 token 過期處理（後端/前端互動）：`backend/...`, `frontend/.../core/auth`
- [ ] T022 鎖定策略（5 次失敗鎖 15m）與提示：`backend/src/main/java/.../application/AuthService`
- [ ] T023 記錄登入/登出/拒絕事件至 Audit：`backend/.../application` & `.../infrastructure`
- [ ] T024 前端登入頁與狀態呈現：`frontend/src/app/features/login`
- [ ] T025 前端錯誤頁（403/404）與未授權導引：`frontend/src/app/pages/error`

## Phase 4: User Story 2 - 角色與權限配置（P1）

### Tests for User Story 2
- [ ] T026 後端單元測試：角色/權限服務：`backend/src/test/java/.../application`
- [ ] T027 後端整合測試：角色/權限 CRUD 與授權檢查：`backend/src/test/java/.../api`
- [ ] T028 前端測試（可選）：角色/權限 UI 驗證：`frontend/`

### Implementation for User Story 2
- [ ] T029 角色/權限 CRUD API：`backend/src/main/java/.../api/RoleController`
- [ ] T030 角色與使用者關聯操作（多角色）：`backend/src/main/java/.../application/RoleService`
- [ ] T031 受保護資源授權檢查（基於 RBAC）：`backend/src/main/java/.../infrastructure/security`
- [ ] T032 前端角色/權限管理介面（最小可用）：`frontend/src/app/features/admin/roles`
- [ ] T033 目前使用者資訊 API 及前端顯示：`backend/src/main/java/.../api/UserController`, `frontend/src/app/core/auth`

## Phase 5: User Story 3 - 存取管控與稽核可追溯性（P2）

### Tests for User Story 3
- [ ] T034 後端整合測試：稽核查詢/過濾：`backend/src/test/java/.../api`
- [ ] T035 前端測試（可選）：稽核列表/篩選：`frontend/`

### Implementation for User Story 3
- [ ] T036 稽核查詢 API（登入/登出/拒絕/角色變更）：`backend/src/main/java/.../api/AuditController`
- [ ] T037 稽核儲存策略與查詢索引：`backend/src/main/java/.../infrastructure/repository`
- [ ] T038 前端稽核檢視（管理員可查詢篩選）：`frontend/src/app/features/admin/audit`

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T039 前端/後端日誌/指標/告警最小化設定（登入/授權/錯誤事件）：`backend/.../infrastructure`, `frontend/...`
- [ ] T040 效能檢查：API p95 < 300ms、首屏互動 < 2.5s（如需可測腳本/指標）  
- [ ] T041 文件收斂（更新 SA/SD/操作手冊、README/quickstart）：`/`
- [ ] T042 稽核/安全回歸：確認 token 時效、鎖定策略、多裝置撤銷行為
- [ ] T043 清理 TODO、最終回歸測試與驗收
