package com.magic.crm.controller;

import com.magic.crm.entity.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 测试控制器
 * 用于测试日期时间反序列化功能
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    /**
     * 测试日期时间反序列化
     */
    @PostMapping("/datetime")
    public String testDateTimeDeserialization(@RequestBody User user) {
        StringBuilder result = new StringBuilder();
        result.append("=== 日期反序列化测试成功 ===\n");
        result.append("用户名: ").append(user.getUsername()).append("\n");
        result.append("全名: ").append(user.getFullName()).append("\n");
        result.append("邮箱: ").append(user.getEmail()).append("\n");
        result.append("电话: ").append(user.getPhoneNumber()).append("\n");
        result.append("生日: ").append(user.getBirthday()).append(" (LocalDate)\n");
        result.append("入职日期: ").append(user.getHireDate()).append(" (LocalDate)\n");
        result.append("创建时间: ").append(user.getCreatedAt()).append(" (LocalDateTime)\n");
        result.append("更新时间: ").append(user.getUpdatedAt()).append(" (LocalDateTime)\n");
        result.append("地址: ").append(user.getAddress()).append("\n");
        result.append("性别: ").append(user.getGender()).append("\n");
        result.append("部门ID: ").append(user.getDepartmentId()).append("\n");
        result.append("是否激活: ").append(user.getIsActive()).append("\n");
        result.append("=============================");
        
        return result.toString();
    }

    /**
     * 测试纯日期反序列化
     */
    @PostMapping("/date")
    public String testDateDeserialization(@RequestBody DateTestRequest request) {
        return "日期解析成功: " + request.getDate() + 
               ", 日期时间解析成功: " + request.getDateTime();
    }

    /**
     * 测试请求类
     */
    public static class DateTestRequest {
        private LocalDate date;
        private LocalDateTime dateTime;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }
}
