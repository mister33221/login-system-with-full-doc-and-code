# SD Logging / Monitoring / Alerts（草案）

後端
- Logging：logback（`backend/src/main/resources/logback-spring.xml`），`com.example.login` 預設 INFO，Security INFO，SQL WARN。
- 追蹤重點：Auth/Audit/Role API 成功/失敗；可於控制器/服務層增加 INFO/WARN 以利稽核。
- 指標：若需，建議後續加入 Spring Actuator + Prometheus/Grafana；目前僅 console log。

前端
- 以瀏覽器 console 為主；HTTP 攔截器對 401/403 會導回 /login，後續可串接 Sentry/New Relic。

Alerts 建議
- 最小化：以容器日誌配合 docker logging driver / ELK 報警。
- 事件：登入失敗率 > 閾值、刷新/登出異常、稽核 API 連續 4xx/5xx。

覆蓋率
- 已導入 JaCoCo（`jacoco-maven-plugin`），本機可 `mvn test` 產生 `backend/target/site/jacoco/index.html` 報告。
