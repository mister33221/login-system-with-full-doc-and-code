# SA 資料模型 / 資料字典（草案）

> 命名為小寫 snake_case；時間欄位皆 UTC。資料庫：PostgreSQL。PK 為 uuid unless noted.

## users
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | uuid | PK |
| username | varchar(64) | NOT NULL, UNIQUE |
| password_hash | varchar(255) | NOT NULL |
| email | varchar(128) | NULL, UNIQUE |
| status | varchar(16) | NOT NULL, CHECK in ('active','locked','suspended') |
| failed_attempts | int | NOT NULL DEFAULT 0 |
| locked_until | timestamptz | NULL |
| created_at | timestamptz | NOT NULL DEFAULT now() |
| updated_at | timestamptz | NOT NULL DEFAULT now() |

Indexes: unique(username), unique(email), idx_users_status, idx_users_locked_until.

## roles
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | uuid | PK |
| code | varchar(64) | NOT NULL, UNIQUE |
| name | varchar(128) | NOT NULL |
| description | varchar(255) | NULL |
| created_at | timestamptz | NOT NULL DEFAULT now() |
| updated_at | timestamptz | NOT NULL DEFAULT now() |

Indexes: unique(code).

## permissions
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | uuid | PK |
| code | varchar(128) | NOT NULL, UNIQUE |
| resource | varchar(128) | NOT NULL |
| action | varchar(64) | NOT NULL |
| description | varchar(255) | NULL |

Indexes: unique(code), idx_permissions_resource_action (resource, action).

## user_roles
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| user_id | uuid | PK part, FK -> users(id) |
| role_id | uuid | PK part, FK -> roles(id) |
| created_at | timestamptz | NOT NULL DEFAULT now() |

Indexes: PK (user_id, role_id).

## role_permissions
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| role_id | uuid | PK part, FK -> roles(id) |
| permission_id | uuid | PK part, FK -> permissions(id) |
| created_at | timestamptz | NOT NULL DEFAULT now() |

Indexes: PK (role_id, permission_id).

## sessions
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | uuid | PK |
| user_id | uuid | FK -> users(id), NOT NULL |
| refresh_token_id | uuid | UNIQUE, NOT NULL |
| device_info | varchar(255) | NULL |
| ip | inet | NULL |
| user_agent | varchar(255) | NULL |
| issued_at | timestamptz | NOT NULL |
| expires_at | timestamptz | NOT NULL |
| revoked_at | timestamptz | NULL |
| revoked_reason | varchar(128) | NULL |

Indexes: unique(refresh_token_id); idx_sessions_user (user_id); idx_sessions_exp (expires_at); idx_sessions_revoked (revoked_at).

## audit_events
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | uuid | PK |
| user_id | uuid | FK -> users(id), NULL (匿名/未登入嘗試) |
| session_id | uuid | FK -> sessions(id), NULL |
| resource | varchar(128) | NOT NULL |
| action | varchar(64) | NOT NULL |
| decision | varchar(16) | NOT NULL, CHECK in ('allow','deny') |
| reason | varchar(255) | NULL |
| ip | inet | NULL |
| user_agent | varchar(255) | NULL |
| created_at | timestamptz | NOT NULL DEFAULT now() |

Indexes: idx_audit_created (created_at), idx_audit_user (user_id), idx_audit_resource_action (resource, action), idx_audit_decision (decision).

## 資料初始化（Flyway V1）
- 建立 `ADMIN` 角色與核心 permissions（auth/login/logout/refresh/session:list/revoke、user:assign-role、role CRUD、audit:view）。
- 建立系統管理員帳號（需強制改密）。

## 其他說明
- 所有時間欄位採 timestamptz；記錄 UTC。  
- 欄位長度可依需求微調，但應保留唯一索引的可行性。  
- 嚴禁儲存明碼密碼與完整 token；refresh_token_id 可為雜湊後標識。  
