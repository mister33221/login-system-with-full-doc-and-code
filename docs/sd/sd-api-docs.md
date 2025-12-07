# API 文件（Springdoc / Swagger UI）

- Swagger UI：`/swagger-ui.html`
- OpenAPI JSON：`/v3/api-docs`
- OpenAPI YAML：`/v3/api-docs.yaml`（springdoc 自動提供）

## 使用方式
1. 啟動後端（Spring Boot）。
2. 瀏覽器開啟 `http://localhost:8080/swagger-ui.html`。
3. 可直接在 UI 內試呼叫 API，並與前端/QA 對齊參數與回應格式。

## 備註
- 目前伺服器 URL 預設為 `http://localhost:8080`（見 OpenApiConfig）。
- 安全機制採 JWT Bearer，未來若需要可在 UI 中加入 Authorize header（springdoc 支援 SecuritySchemes 配置）。
