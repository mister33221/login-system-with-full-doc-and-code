<!--
Sync Impact Report
- Version change: 1.0.1 -> 1.0.2
- Modified principles: None
- Added sections: None
- Removed sections: None
- Templates requiring updates: .specify/templates/plan-template.md (updated), .specify/templates/spec-template.md (無需更新), .specify/templates/tasks-template.md (updated)
- Follow-up TODOs: 無
-->

# Login System Constitution

## Core Principles

### 0. 語言
全程使用繁體中文撰寫文件與程式碼註解（除非既有規範或程式語言要求英文）。

### I. 工程品質一致性
前端（Angular）、後端（Spring Boot）、Nginx 配置遵循一致的工程規範與分層。遵守框架最佳實務：Angular 嚴格模式 + ESLint、Spring Boot 分層與 Bean 驗證、Nginx 配置版本控管。程式碼須可維護、可閱讀、可追蹤變更；抽象重複但避免過度設計；變更需同行審查與風險說明。

### II. 測試驅動與品質門檻
風險優先撰寫測試並以測試作為交付門檻。前後端關鍵模組單元測試覆蓋率 ≥80%；後端對外 API 需契約/整合測試，前端關鍵流程有端對端驗證。失敗測試禁止合併；缺測試的變更視為未完成；安全敏感區域優先自動化回歸。

### III. 使用者體驗一致性
UI 行為、文案、狀態呈現保持一致且可預期。共用設計令牌（色彩/間距/字體）與元件庫；表單/錯誤/空狀態一致並滿足可存取性（鍵盤、ARIA、對比度）。跨裝置響應式需確保主要流程可用；任何破壞一致性的變更需設計理由與回退策略。

### IV. 效能與穩定性預算
設定並驗證效能/可靠性目標：後端 API p95 < 300ms；前端首屏可互動 < 2.5s（中等裝置/網路）；Nginx 啟用 TLS、壓縮、適當快取/限流。效能回歸測試與指標（延遲、錯誤率、資源使用）需在 CI/CD 或驗收階段驗證。

### V. 精實敏捷交付
以 MVP 拆分需求，避免過度設計。每個 User Story 可獨立開發/測試/部署；任務以最小可驗證增量拆分並排序。流程遵循 Phase 0 研究 → Phase 1 設計/計畫 → Phase 2 實作 → Phase 3 驗證/回饋，持續收集用戶與效能數據微調。

## 技術堆疊與品質標準

- Angular：嚴格型別，預設 OnPush（除非有明確理由）；ESLint + Prettier；共用 UI 需示例與可存取性校驗；HTTP 呼叫型別安全並包裝錯誤/重試。
- Spring Boot：分層與依賴反轉，DTO/領域模型分離；Bean Validation；Flyway/Liquibase 版本化遷移；結構化日誌（遮罩敏感資訊）；健康檢查與指標。
- Nginx：配置版本化，安全基線（TLS 1.2+、嚴格頭）；gzip/Brotli、快取、限流，上游超時與重試策略一致；日誌可追蹤請求與效能。
- 品質檢查：CI 必跑 lint、型別、單元與整合/端對端測試；主幹保持可部署；產出文件（架構決策、API 契約、操作手冊）與變更同步。
- 安全與資料：禁止明碼/硬編碼秘密；使用環境變數/密鑰管理；敏感資料傳輸/儲存加密，最小化收集。

## 開發流程與審查

- 需求分解：以 User Story 驅動，明確驗收標準與驗證方式；先確立 MVP，再按優先級擴展。
- 規劃節奏：Phase 0（研究/風險），Phase 1（架構/資料模型/合約），Phase 2（實作與測試），Phase 3（驗證/回饋/效能檢查）；每階段須通過對應品質檢查。
- 審查標準：檢查原則遵循度、測試覆蓋、效能/安全影響；重大設計需記錄決策與取捨。
- 驗收與交付：Definition of Done = 原則符合 + 測試通過 + 文件更新 + 監控/告警（如適用）；部署前需回歸與效能驗證清單；完成任務須立即更新 `specs/.../tasks.md` 勾選狀態，避免遺漏。

## Governance

此憲章為開發與交付的強制準則；如有衝突，以本憲章為準。修訂需附變更說明、影響分析、版本評估與過渡方案，並由產品與技術負責人共同核准。版本採語意化：新增/擴充原則為 MINOR，文字澄清為 PATCH，重大調整/移除原則為 MAJOR。至少每季度稽核一次（程式碼品質、測試、UX 一致性、效能指標），結果納入迭代規劃。開發、審查、交付須遵循 `docs/sa`（需求/分析）與 `docs/sd`（設計/架構）文件的要求，確保需求與設計對齊。

**Version**: 1.0.2 | **Ratified**: 2025-12-07 | **Last Amended**: 2025-12-07
