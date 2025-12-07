# Implementation Plan: Login & RBAC System

**Branch**: `001-login-system` | **Date**: 2025-12-07 | **Spec**: specs/001-login-system/spec.md  
**Input**: Feature specification from `/specs/001-login-system/spec.md`

## Summary

交付 Angular 前端 + Spring Boot/Security 後端的登入與 RBAC 系統：登入/登出、角色/權限配置、多裝置會話、閒置逾時、Token 撤銷、稽核記錄，本地 PostgreSQL 自管帳號；Nginx (docker-compose) 為反向代理。

## Technical Context

**Language/Version**: Angular (LTS) + TypeScript；Spring Boot 3.x + Spring Security；Java 17；Tailwind CSS。  
**Primary Dependencies**: JWT (Bearer) 驗證、Spring Data/Flyway、Springdoc OpenAPI + Swagger UI（/swagger-ui.html）、Tailwind、Angular Router Guard/HTTP Interceptor。  
**Storage**: PostgreSQL（Docker；Flyway 管理 schema/migrations）。  
**Testing**: 後端 JUnit/AssertJ；可加入前端單元/E2E（視時程）。  
**Target Platform**: Docker-compose (dev)；Nginx 反向代理。  
**Project Type**: Web（frontend + backend）。  
**Performance Goals**: 後端 API p95 < 300ms；前端首屏可互動 < 2.5s；登入 90% 於 5 秒內完成。  
**Constraints**: Access 15m、Refresh 12h、Idle 30m；鎖定 5 次失敗鎖 15m；撤銷需跨裝置即時生效。  
**Scale/Scope**: 練習級，但需完整 RBAC、稽核、錯誤處理與基本測試。

## Constitution Check

- 前端（嚴格模式、ESLint/Prettier）、後端（分層、Bean 驗證）、Nginx（TLS/壓縮/限流預留）符合品質基線。  
- 測試門檻：單元覆蓋 ≥80%；後端契約/整合、前端 E2E 範圍明確；CI 阻擋未通過。  
- UX 一致：設計令牌、元件重用、可存取性、錯誤/空狀態處理。  
- 效能：API p95 < 300ms、首屏 < 2.5s、快取/佇列策略、監控指標。  
- MVP 拆分完成，避免過度設計。

## Project Structure

```text
backend/
├─ src/main/java/.../api/            # REST controllers (auth, session, user/role)
├─ src/main/java/.../application/    # use cases/services
├─ src/main/java/.../domain/         # entities (User, Role, Permission, Session, Audit)
├─ src/main/java/.../infrastructure/ # repositories, jwt, security config, logging
├─ src/main/resources/db/migration/  # Flyway migrations (V1__init.sql)
├─ src/main/resources/application-dev.yml
├─ src/main/resources/application-prod.yml
└─ src/test/java/...                 # unit / integration samples

frontend/
├─ src/app/
│  ├─ core/auth/                     # services/interceptors/guards
│  ├─ features/login/                # login page/module
│  ├─ features/admin/roles/          # role/permission UI
│  ├─ shared/components/             # shared UI components
│  └─ pages/error/                   # 403/404
├─ tailwind.config.js
└─ angular.json

nginx/
└─ nginx.conf

docker-compose.yml

# Documentation
docs/sa/sa-functional-requirements.md
docs/sa/sa-rbac-model.md
docs/sa/sa-login-logout-flow.md
docs/sa/sa-authz-flow.md
docs/sa/sa-auth-api-spec.md
docs/sa/sa-data-model.md
docs/sa/sa-security-policies.md
docs/sa/sa-ux-wireframes.md
docs/sa/sa-test-acceptance.md
docs/sd/sd-architecture.md
docs/sd/sd-deployment.md
```

**Structure Decision**: 前後端分離；後端 DDD 式分層；前端 feature/module 拆分 + shared/core；Flyway 管理 schema；Nginx 反向代理；OpenAPI/Swagger 提供 API 文件。

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|--------------------------------------|
| （無） |  |  |

## Implementation Strategy

1) Phase 0 研究：確認 JWT 配置、鎖定策略、撤銷/多裝置流程；定義錯誤回應格式與稽核欄位；產出 SA/SD 文件。  
2) Phase 1 設計：資料模型、API/合約（登入/刷新/登出/撤銷/目前使用者/角色/權限/會話）、Nginx 代理、docker-compose、初始 Flyway migration。  
3) Phase 2 實作與測試（優先 User Story 1/2）：後端安全配置（JWT）、統一錯誤回應、稽核；角色/權限/授權檢查；會話查詢/撤銷。前端登入頁、HTTP interceptor、Route Guard、角色/權限 UI。  
4) Phase 3 驗證與效能：驗證 token/逾時/鎖定/撤銷行為；效能與觀測指標；回歸。  
5) Phase 4 收斂：文件（SA/SD/操作手冊）更新、清理 TODO、最終回歸。

## Notes

- MVP 先完成登入/登出、RBAC、鎖定與稽核，再擴充 UI/體驗。  
- 測試優先：後端單元與授權/會話行為整合測試先到位；前端至少 Interceptor/Guard 單元測試範例。  
- 部署/本地：docker-compose 啟動 Nginx + backend + PostgreSQL；前端 dev 由 ng serve 透過 Nginx 轉發。  
- API 文件：使用 Springdoc OpenAPI，自動生成 /v3/api-docs 與 /swagger-ui.html，供前後端與 QA 對齊。 
