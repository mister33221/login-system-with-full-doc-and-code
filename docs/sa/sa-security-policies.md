# SA 安全性與帳號政策

## 密碼與身份
- 密碼儲存：bcrypt（含 salt），不可明碼或可逆儲存。
- 密碼規則：長度 ≥ 12，包含大小寫/數字/特殊字元；新帳號需強制改密。
- 鎖定策略：登入連續失敗 5 次鎖 15 分鐘；鎖定期間拒絕登入並提示。
- 停權：可停用帳號，停用時所有 token/會話撤銷。

## Token / Session
- Access token 15 分鐘；Refresh token 12 小時；Idle timeout 30 分鐘。
- 登出/撤銷需同時失效 access/refresh，跨裝置立即阻擋後續請求。
- 會話可查詢並逐一撤銷；支援多裝置。
- 黑名單/撤銷檢查：每次請求驗證 token 是否撤銷/過期。

## 傳輸與 Cookie
- 預留 HTTPS；開發期可 HTTP，但需易於切換。
- 建議前端以安全儲存（非 localStorage）或 HttpOnly Cookie（SameSite=Lax/Strict，Secure 在 HTTPS 啟用）。
- CORS：僅允許受控來源，允許必要方法與標頭，禁止任意通配。

## 資料保護
- 最小權限存取；敏感資料不出現在日誌或錯誤訊息中。
- 日誌遮罩敏感欄位（token、密碼、email 部分遮罩）。
- DB 備份與帳號密鑰以環境變數/秘密管理保存，禁止硬編碼。

## 稽核與監控
- 稽核項目：登入/登出/拒絕、角色/權限變更、會話撤銷、鎖定/解鎖。
- 事件內容：user、resource、action、decision、reason、IP、UA、時間、session。
- 指標：認證/授權成功率、拒絕率、延遲、鎖定次數；必要告警（異常失敗暴增）。
