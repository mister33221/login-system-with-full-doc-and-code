# SA 認證與授權 API 規格（草案）

> 統一錯誤格式：`{ code, message, data? }`；常見錯誤碼：  
> - 400 Bad Request（格式/參數錯）  
> - 401 Unauthorized（未驗證或 token 無效/過期/撤銷）  
> - 403 Forbidden（權限不足）  
> - 404 Not Found（資源不存在）  
> - 409 Conflict（重複/狀態衝突）  
> - 423 Locked（帳號鎖定）

## /api/auth/login
| 欄位 | 說明 |
|------|------|
| API 名稱 | Login |
| 功能說明 | 使用帳號/密碼登入並簽發 access/refresh token，同時寫入 Session 與稽核 |
| Method | POST |
| 權限需求 | 公開 |
| Request Body | `{ "username": "string", "password": "string" }` |
| Response Body | `{ "accessToken": "string", "refreshToken": "string", "user": { "id": "uuid", "username": "string", "roles": ["ADMIN"], "permissions": ["auth:login"] } }` |
| 錯誤碼 | 400（格式錯）; 401（憑證錯誤）; 423（鎖定） |
| 錯誤 Response Body | `{ "code": 401, "message": "invalid credentials", "data": null }`（示例） |

## /api/auth/logout
| 欄位 | 說明 |
|------|------|
| API 名稱 | Logout |
| 功能說明 | 登出並撤銷當前 access/refresh，跨裝置立即失效，寫稽核 |
| Method | POST |
| 權限需求 | auth:logout |
| Request Body | 空（使用 Authorization header） |
| Response Body | `{ "success": true }` |
| 錯誤碼 | 401（未驗證）; 403（權限不足） |
| 錯誤 Response Body | `{ "code": 401, "message": "unauthorized", "data": null }` |

## /api/auth/refresh
| 欄位 | 說明 |
|------|------|
| API 名稱 | Refresh Token |
| 功能說明 | 以有效 refresh 簽發新 access（可選擇滾動 refresh） |
| Method | POST |
| 權限需求 | 需有效 refresh token |
| Request Body | `{ "refreshToken": "string" }` |
| Response Body | `{ "accessToken": "string", "refreshToken": "string" }`（若不滾動 refresh，可省略 refreshToken） |
| 錯誤碼 | 400（格式錯）; 401（refresh 無效/過期/撤銷） |
| 錯誤 Response Body | `{ "code": 401, "message": "invalid refresh token", "data": null }` |

## /api/auth/me
| 欄位 | 說明 |
|------|------|
| API 名稱 | Current User |
| 功能說明 | 取得目前使用者資訊、角色與權限摘要 |
| Method | GET |
| 權限需求 | 已登入 |
| Request Body | 無 |
| Response Body | `{ "user": { "id": "uuid", "username": "string", "email": "string?", "roles": ["ADMIN"], "permissions": ["auth:login"] } }` |
| 錯誤碼 | 401（未驗證） |
| 錯誤 Response Body | `{ "code": 401, "message": "unauthorized", "data": null }` |

## /api/auth/sessions
| 欄位 | 說明 |
|------|------|
| API 名稱 | List Sessions |
| 功能說明 | 查詢當前使用者的會話列表（支援裝置/IP/建立時間） |
| Method | GET |
| 權限需求 | auth:session:list |
| Request Body | 無 |
| Response Body | `{ "sessions": [ { "id": "uuid", "createdAt": "ISO8601", "ip": "string", "userAgent": "string", "expiresAt": "ISO8601", "revokedAt": null } ] }` |
| 錯誤碼 | 401（未驗證）; 403（權限不足） |
| 錯誤 Response Body | `{ "code": 403, "message": "forbidden", "data": null }` |

## /api/auth/sessions/{id}
| 欄位 | 說明 |
|------|------|
| API 名稱 | Revoke Session |
| 功能說明 | 撤銷單一會話（含跨裝置），後續請求立即阻擋 |
| Method | DELETE |
| 權限需求 | auth:session:revoke |
| Request Body | 無（路徑參數 session id） |
| Response Body | `{ "success": true }` |
| 錯誤碼 | 401（未驗證）; 403（權限不足）; 404（會話不存在） |
| 錯誤 Response Body | `{ "code": 404, "message": "session not found", "data": null }` |

## /api/roles
| 欄位 | 說明 |
|------|------|
| API 名稱 | Roles CRUD |
| 功能說明 | 角色列表/建立/更新/刪除 |
| Method | GET / POST / PUT / DELETE |
| 權限需求 | role:view / role:create / role:update / role:delete |
| Request Body | GET 無；POST/PUT: `{ "code": "string", "name": "string", "description": "string", "permissions": ["perm_code"] }` |
| Response Body | GET: `{ "roles": [...] }`; POST/PUT: `{ "role": { ... } }`; DELETE: `{ "success": true }` |
| 錯誤碼 | 400（格式錯/重複 code）; 401; 403; 404（更新/刪除不存在）; 409（衝突） |
| 錯誤 Response Body | `{ "code": 400, "message": "duplicate role code", "data": null }`（示例） |

## /api/permissions
| 欄位 | 說明 |
|------|------|
| API 名稱 | List Permissions |
| 功能說明 | 列出所有權限 |
| Method | GET |
| 權限需求 | permission:view |
| Request Body | 無 |
| Response Body | `{ "permissions": [ { "code": "auth:session:revoke", "resource": "auth:session", "action": "revoke" } ] }` |
| 錯誤碼 | 401; 403 |
| 錯誤 Response Body | `{ "code": 403, "message": "forbidden", "data": null }` |

## /api/users/{id}/roles
| 欄位 | 說明 |
|------|------|
| API 名稱 | Assign Roles to User |
| 功能說明 | 為指定使用者指派角色（可多角色），即時生效 |
| Method | POST |
| 權限需求 | user:assign-role |
| Request Body | `{ "roleIds": ["uuid"] }` |
| Response Body | `{ "success": true }` |
| 錯誤碼 | 400（格式錯）; 401; 403; 404（使用者或角色不存在） |
| 錯誤 Response Body | `{ "code": 404, "message": "user not found", "data": null }` |

## /api/audit
| 欄位 | 說明 |
|------|------|
| API 名稱 | Audit Query |
| 功能說明 | 查詢稽核事件，支援 user/resource/action/decision/time 篩選 |
| Method | GET |
| 權限需求 | audit:view |
| Request Body | 無（可用 query 參數，例如 ?userId=&action=&from=&to=） |
| Response Body | `{ "events": [ { "id": "uuid", "userId": "uuid?", "resource": "string", "action": "string", "decision": "allow|deny", "reason": "string", "ip": "string", "userAgent": "string", "createdAt": "ISO8601" } ] }` |
| 錯誤碼 | 401; 403 |
| 錯誤 Response Body | `{ "code": 403, "message": "forbidden", "data": null }` |
