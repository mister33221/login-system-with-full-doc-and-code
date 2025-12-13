package com.example.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hashes for common passwords
        String[] passwords = {"password", "admin123", "test123"};
        
        System.out.println("=== BCrypt Password Hashes ===\n");
        
        for (String pwd : passwords) {
            String hash = encoder.encode(pwd);
            System.out.println("Password: " + pwd);
            System.out.println("Hash: " + hash);
            System.out.println("Length: " + hash.length());
            System.out.println("Verification: " + encoder.matches(pwd, hash));
            System.out.println();
        }
        
        // SQL statements
        System.out.println("\n=== SQL UPDATE Statements ===\n");
        String adminHash = encoder.encode("admin123");
        String testHash = encoder.encode("test123");
        
        System.out.println("-- For admin (password: admin123)");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "', failed_attempts = 0, status = 'active', locked_until = NULL WHERE username = 'admin';");
        System.out.println();
        
        System.out.println("-- For testuser (password: test123)");
        System.out.println("UPDATE users SET password_hash = '" + testHash + "', failed_attempts = 0, status = 'active', locked_until = NULL WHERE username = 'testuser';");
    }
}
