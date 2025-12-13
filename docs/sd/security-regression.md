# SD 稽核/安全回歸檢查（草案）

必驗項目：
- Token 有效期與撤銷：刷新後舊 access 不再使用；logout/revoke 後 refresh 不可再用。
- 鎖定策略：5 次失敗鎖 15 分鐘，訊息含剩餘/鎖定提示。
- RBAC：僅 ADMIN 可管理角色/權限；ADMIN/AUDITOR 可查詢稽核；一般 USER 無法存取 `/api/roles`、`/api/audit`。
- Session：多裝置刷新/登出需寫入 sessions 表，revoked_at 設定正確。
- Audit：登入/登出/拒絕/角色變更需記錄（resource/action/decision/ip/user-agent）。
- CORS/HTTPS：Nginx 反向代理保留 Host/real-ip；Cookie/Authorization header 正確轉發。

建議腳本/手動：
1) 以 Swagger `/swagger-ui.html` 驗證角色/稽核 API 權限；未授權應回 403。
2) 手動連續 5 次錯密碼，確認鎖定與解鎖時間戳。
3) 以兩組 refresh token 交叉刷新，驗證撤銷後拒絕。
4) 查詢 `/api/audit?user=admin&action=login` 應可看到 allow/deny 紀錄。
5) 前端守衛：未登入導向 /login；無權限導向 /403。
