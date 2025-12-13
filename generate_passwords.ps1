# Generate BCrypt password hashes using Java
$javaCode = @"
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TempHashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== 新的 BCrypt 密碼 Hash ===\n");
        
        // Admin user
        String adminHash = encoder.encode("admin123");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println("Hash: " + adminHash);
        System.out.println();
        
        // Test user
        String testHash = encoder.encode("test123");
        System.out.println("Username: testuser");
        System.out.println("Password: test123");
        System.out.println("Hash: " + testHash);
        System.out.println();
        
        // SQL 更新語句
        System.out.println("\n=== SQL 更新語句 ===\n");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password_hash = '" + testHash + "' WHERE username = 'testuser';");
    }
}
"@

Write-Host "請在 IntelliJ IDEA 中右鍵點擊 PasswordHashGenerator.java，選擇 'Run PasswordHashGenerator.main()'" -ForegroundColor Green
Write-Host ""
Write-Host "或者使用以下測試帳號（資料庫重建後，這些帳號應該已經在 V1__init.sql 中）：" -ForegroundColor Yellow
Write-Host ""
Write-Host "帳號 1: admin / admin123 (ADMIN 角色)" -ForegroundColor Cyan
Write-Host "帳號 2: testuser / test123 (USER 角色)" -ForegroundColor Cyan
Write-Host ""
Write-Host "測試登入：" -ForegroundColor Yellow
Write-Host '$body = @{username="admin"; password="admin123"} | ConvertTo-Json'
Write-Host 'Invoke-RestMethod -Uri http://localhost:8080/api/auth/login -Method POST -ContentType "application/json" -Body $body'
Write-Host ""
Write-Host "或使用註冊 API 建立新帳號：" -ForegroundColor Yellow
Write-Host '$body = @{username="newuser"; password="password123"; email="new@test.com"} | ConvertTo-Json'
Write-Host 'Invoke-RestMethod -Uri http://localhost:8080/api/auth/register -Method POST -ContentType "application/json" -Body $body'
