package com.example.login.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Login & RBAC API",
                version = "v1",
                description = "認證、授權、會話與角色權限相關 API 文件"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local dev")
        })
public class OpenApiConfig {
    // Springdoc 會自動掃描 REST 控制器並提供 /v3/api-docs 與 /swagger-ui.html
}
