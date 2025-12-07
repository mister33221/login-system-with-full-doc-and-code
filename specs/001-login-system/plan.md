# Implementation Plan: Login & RBAC System

**Branch**: `001-login-system` | **Date**: 2025-12-07 | **Spec**: specs/001-login-system/spec.md  
**Input**: Feature specification from `/specs/001-login-system/spec.md`

## Summary

交付一個 Angular 前端 + Spring Boot/Spring Security 後端的登入與 RBAC 系統，涵蓋登入/登出、角色與權限配置、多裝置會話管理、閒置逾時、自動與手動撤銷 Token、稽核紀錄，以及本地 PostgreSQL 自管帳號。前端提供登入頁、保護路由與錯誤頁，後端提供 JWT 驗證/授權、鎖定策略、統一錯誤回應，並以 Nginx (docker-compose) 做反向代理。

## Technical Context

**Language/Version**: Angular (最新 LTS)、TypeScript；Spring Boot 3.x + Spring Security；Java 17；Tailwind CSS。  
**Primary Dependencies**: JWT (Bearer) 驗證、Spring Data/Flyway、Tailwind、可能的 Angular Router Guard/HTTP Interceptor。  
**Storage**: PostgreSQL（Docker；Flyway 管理 schema/migrations）。  
**Testing**: 後端 JUnit/AssertJ 單元測試；可加入前端單元/端對端（視時程）。  
**Target Platform**: Docker-compose (dev)；Nginx 反向代理。  
**Project Type**: Web（frontend + backend）。  
**Performance Goals**: 後端 API p95 < 300ms；前端首屏可互動 < 2.5s（中等裝置與網路）；登入成功率 90% 於 5 秒內完成（對應 SC-001）。  
**Constraints**: Access token 15m、Refresh 12h、Idle timeout 30m；鎖定策略 5 次失敗鎖 15m；撤銷需跨裝置即時生效。  
**Scale/Scope**: 練習級，但需具備完整 RBAC、稽核、錯誤處理與基本測試範例。

## Constitution Check

- 確認前端（Angular 嚴格模式、ESLint/Prettier）、後端（分層架構、Bean 驗證）、Nginx（TLS/壓縮/快取/限流）符合品質基線並納入計畫。  
- 定義測試策略與門檻：單元覆蓋率目標 ≥80%，後端契約/整合測試與前端端對端測試範圍明確，並於 CI 阻擋未通過的變更。  
- UX 一致性準則已落實於方案（設計令牌、元件重用、可存取性、錯誤/空狀態處理）。  
- 效能預算與驗證方式已列入（API p95 < 300ms、首屏可互動 < 2.5s、快取/佇列策略、監控指標）。  
- 需求已拆成可獨立交付的 User Story/Task，MVP 範圍明確且避免過度設計。

## Project Structure

```text
backend/
├─ src/main/java/.../api/            # REST controllers (auth, session, user/role)
├─ src/main/java/.../application/    # use cases/services
├─ src/main/java/.../domain/         # aggregates/entities (User, Role, Permission, Session)
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

# Pre-dev documentation (root)
sa-functional-requirements.md      # 功能需求/Use Case + User Stories
sa-rbac-model.md                   # RBAC 權限模型與角色/權限對應
sa-login-logout-flow.md            # 登入/登出流程（成功/失敗/鎖定）
sa-authz-flow.md                   # 驗證與授權流程（Token/Session 檢驗、權限決策）
sa-auth-api-spec.md                # 認證/授權 API 規格
sa-data-model.md                   # 資料表設計/資料字典（User/Role/Permission/Session/Audit 等）
sa-security-policies.md            # 安全與帳號政策（密碼、鎖定、Token/Session、Cookie）
sa-ux-wireframes.md                # 前端登入/未授權/登出流程 Wireframe
sa-test-acceptance.md              # 測試案例與驗收條件
```

**Structure Decision**: 採 Web app（前後端分離）結構，後端 DDD 式分層；前端按 feature/module 拆分並復用 shared/core；Flyway 管理 DB schema；Nginx 作反向代理。

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|--------------------------------------|
| （無） |  |  |

## Implementation Strategy

1) Phase 0/研究：確認 JWT 配置、鎖定策略、撤銷/多裝置會話流程；落實錯誤回應格式與稽核欄位；產出 SA/SD 文件初稿（功能需求/Use Case、RBAC 模型、流程、API 規格、資料模型、安全政策、UX Wireframe、測試案例）。  
2) Phase 1/設計：  
   - 資料模型：User/Role/Permission/Session/AuditEvent；明確關聯與唯一性。  
   - API/合約：登入、刷新、登出/撤銷、目前使用者、角色/權限 CRUD、會話查詢/撤銷。  
   - Nginx 反向代理與 docker-compose 網路/環境變數設計；初始 Flyway migration。  
3) Phase 2/實作與測試（優先交付 User Story 1/2）：  
   - 後端：安全配置、JWT 簽發/驗證、鎖定策略、統一錯誤回應、稽核紀錄；角色/權限與授權檢查；會話查詢與撤銷。  
   - 前端：登入頁與錯誤頁；HTTP interceptor 加入/刷新 Token；Route Guard；角色/權限 UI（最小可用）。  
   - 測試：後端單元/整合測試示例（服務與授權）；可選前端單元/E2E。  
4) Phase 3/驗證與效能：  
   - 驗證 Access 15m、Refresh 12h、Idle 30m 與 5 次鎖 15m 行為；撤銷跨裝置即時阻擋。  
   - 效能與觀測：基本指標/日誌（登入/授權/拒絕/錯誤）；檢查 API p95 與前端體驗目標。  
5) Phase 4/收斂：文件更新（SA/SD/操作手冊），清理 TODO，最終回歸。

## Notes

- 保持 MVP：先完成登入/登出、RBAC、鎖定與稽核，再擴充 UI/體驗。  
- 測試優先：後端單元與授權/會話行為的整合測試須先到位；若時間有限，前端至少保留 Interceptor/Guard 單元測試範例。  
- 部署/本地：docker-compose 啟動 Nginx + backend + PostgreSQL；前端 dev 以 ng serve 透過 Nginx 轉發。
