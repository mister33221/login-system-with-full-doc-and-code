# SA RBAC 模型

## 核心實體與關係
| Entity | Relationship | 說明 |
|--------|--------------|------|
| User ↔ Role | 多對多（User_Role） | 使用者可有多個角色 |
| Role ↔ Permission | 多對多（Role_Permission） | 角色可有多個權限 |
| Permission | Resource + Action | 例如 `auth:session:revoke` |

## 角色建議（可調整）
| 角色 | 說明 |
|------|------|
| ADMIN | 管理使用者、角色、權限、會話、稽核查詢 |
| USER | 一般登入與使用受授權功能 |
| AUDITOR | 查詢稽核事件，唯讀 |

## 權限分組（示例）
### Auth / Session
| Permission | 說明 |
|------------|------|
| auth:login | 登入 |
| auth:logout | 登出 |
| auth:session:list | 查詢自身會話 |
| auth:session:revoke | 撤銷會話 |

### User / Role
| Permission | 說明 |
|------------|------|
| user:view | 檢視使用者 |
| user:assign-role | 指派角色 |
| role:create | 建立角色 |
| role:update | 更新角色 |
| role:delete | 刪除角色 |
| role:view | 檢視角色 |
| permission:view | 檢視權限列表 |

### Audit
| Permission | 說明 |
|------------|------|
| audit:view | 查詢稽核事件 |

## 授權原則
- 最小權限：預設拒絕，需顯式授權。
- 角色變更即時生效（新請求）；必要時可撤銷既有會話以強制生效。
- 無權請求返回 403 並記錄 AuditEvent（user, resource, action, decision, reason）。

## 一致性要求
- 前端 UI 僅顯示當前角色/權限可用的操作，最終以後端授權結果為準。
- 資源命名規則：`<domain>:<resource>:<action>`，保持可追蹤性與日誌一致。
