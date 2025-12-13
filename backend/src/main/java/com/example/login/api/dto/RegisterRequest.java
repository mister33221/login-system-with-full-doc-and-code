package com.example.login.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "帳號不可為空")
    @Size(min = 3, max = 64, message = "帳號長度必須在 3-64 之間")
    private String username;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, message = "密碼長度至少為 6")
    private String password;

    @Email(message = "Email 格式不正確")
    private String email;
}
