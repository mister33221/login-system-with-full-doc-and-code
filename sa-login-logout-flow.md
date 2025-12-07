# SA 登入 / 登出流程（Mermaid）

## 圖 1：登入成功/失敗
<div style="display: flex; gap: 12px; align-items: flex-start;">
  <div style="flex: 1; min-width: 240px;">
    使用者送出帳密，若通過則簽發 tokens 並建 Session；失敗累計達 5 次會鎖定 15 分鐘並記錄稽核。
  </div>
  <div style="flex: 1; min-width: 320px;">
  
```mermaid
flowchart TD
    L1["前端送帳號/密碼 (TLS)"] --> L2{"驗證憑證 & 鎖定狀態？"}
    L2 -->|通過| L3["簽發 Access(15m)+Refresh(12h)，建 Session，寫 Audit"]
    L2 -->|失敗| L4["計數+1，回非敏感錯誤；達 5 次鎖 15m，寫 Audit"]
    L3 --> L5["回傳 tokens + 使用者/角色/權限摘要"]
    L5 --> L6["前端安全儲存 tokens，啟動 idle 30m 計時"]
```
  </div>
</div>

## 圖 2：閒置逾時 / Token 過期
<div style="display: flex; gap: 12px; align-items: flex-start;">
  <div style="flex: 1; min-width: 240px;">
    閒置 30 分鐘或 token 過期時，前端提示並發出登出，後端拒絕逾時請求並記錄稽核。
  </div>
  <div style="flex: 1; min-width: 320px;">
  
```mermaid
flowchart TD
    T1["Idle 30m 或 token 過期"] --> T2["前端提示並觸發登出請求"]
    T2 --> T3["後端確認逾時/過期，拒絕後續請求並寫 Audit"]
```
  </div>
</div>

## 圖 3：登出 / 撤銷（含跨裝置）
<div style="display: flex; gap: 12px; align-items: flex-start;">
  <div style="flex: 1; min-width: 240px;">
    登出或管理員撤銷會讓 access/refresh 立即失效，跨裝置阻擋後續請求，並寫稽核後清除本機 tokens。
  </div>
  <div style="flex: 1; min-width: 320px;">
  
```mermaid
flowchart TD
    R1["前端登出 / 管理員撤銷"] --> R2["後端標記 access/refresh 撤銷，跨裝置即時阻擋"]
    R2 --> R3["寫 Audit：user/resource/action/result/ip/ua"]
    R3 --> R4["前端清除 tokens，導向登入頁或提示完成"]
```
  </div>
</div>

## 圖 4：特殊情境
<div style="display: flex; gap: 12px; align-items: flex-start;">
  <div style="flex: 1; min-width: 240px;">
    Token 已被撤銷時請求會被拒絕；角色/權限變更對新請求立即生效，必要時可同步撤銷既有會話。
  </div>
  <div style="flex: 1; min-width: 320px;">
  
```mermaid
flowchart TD
    S1["既有 token 被撤銷"] --> S2["後端拒絕 401/403，提示重新登入"]
    S3["角色/權限變更"] --> S4["新請求立即套用；必要時同步撤銷既有會話"]
```
  </div>
</div>

## 說明
- 驗證：登入時檢查憑證與鎖定，通過才簽發 token 並記錄稽核；失敗計數達 5 次鎖 15 分鐘。
- 逾時：前端 30 分鐘閒置觸發登出；逾時或撤銷的 token 會被後端拒絕並記錄。
- 撤銷：登出/管理員撤銷會讓 access/refresh 立即失效，跨裝置阻擋後續請求並寫稽核。
- 角色變更：新請求套用最新權限；如需強制即時生效，可撤銷舊會話。
