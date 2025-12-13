# SD 效能檢查指引（草案）

目標：
- API p95 < 300ms（登入、刷新、角色查詢、稽核查詢）。
- 首屏互動 < 2.5s（Angular 開發/本地模式）。

建議流程：
1. 後端基準：以 Postman/K6 壓測 `/api/auth/login`, `/api/auth/refresh`, `/api/roles`, `/api/audit`，50~100 RPS、1~2 分鐘，觀察 p95。
2. DB/索引確認：`audit_events` 已建索引（created_at、user_id、resource/action、decision）；`sessions` 已建索引（user_id、expires_at、revoked_at）。
3. 前端：`npm run build` 後使用 Chrome Lighthouse（Performance 指標），確保 LCP/TTI < 2.5s（開發機 HTTP 模式即可）。
4. 觀察：`backend` 啟動時可於 console 留意慢查詢；必要時提高 `spring.jpa.properties.hibernate.jdbc.batch_size` / 調整查詢。
5. 報告：記錄環境（CPU/RAM/DB 版本）、指標結果與改善項目，存放於 docs/sd/。
