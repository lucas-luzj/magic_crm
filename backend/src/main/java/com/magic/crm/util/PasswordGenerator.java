package com.magic.crm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成admin123的BCrypt哈希
        String adminPassword = encoder.encode("admin123");
        System.out.println("admin123 的BCrypt哈希值: " + adminPassword);

        // 生成user123的BCrypt哈希
        String userPassword = encoder.encode("user123");
        System.out.println("user123 的BCrypt哈希值: " + userPassword);

        // 验证测试
        System.out.println("验证admin123: " + encoder.matches("admin123", adminPassword));
        System.out.println("验证user123: " + encoder.matches("user123", userPassword));
    }
}