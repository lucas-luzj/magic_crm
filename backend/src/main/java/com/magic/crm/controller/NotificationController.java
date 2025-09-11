package com.magic.crm.controller;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationSettings;
import com.magic.crm.entity.NotificationStatus;
import com.magic.crm.entity.NotificationType;
import com.magic.crm.entity.User;
import com.magic.crm.service.NotificationService;
import com.magic.crm.service.NotificationSettingsService;
import com.magic.crm.util.ApiResponse;
import com.magic.crm.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationSettingsService settingsService;
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            // 根据JwtAuthenticationFilter的实现，principal就是User实体
            if (principal instanceof User) {
                return ((User) principal).getId();
            }
        }
        
        // 如果无法获取用户信息，抛出异常
        throw new RuntimeException("未找到当前登录用户");
    }
    
    /**
     * 获取通知列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Notification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Long userId = getCurrentUserId();
        
        PageResponse<Notification> notifications = notificationService.getUserNotifications(
                userId, page, size, type, status, keyword, startTime, endTime);
        
        return ApiResponse.success(notifications);
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        long count = notificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 获取未读通知列表
     */
    @GetMapping("/unread")
    public ApiResponse<List<Notification>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ApiResponse.success(notifications);
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Boolean> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        boolean success = notificationService.markAsRead(id, userId);
        return ApiResponse.success(success);
    }
    
    /**
     * 批量标记通知为已读
     */
    @PutMapping("/batch-read")
    public ApiResponse<Integer> batchMarkAsRead(@RequestBody Map<String, List<Long>> request) {
        Long userId = getCurrentUserId();
        List<Long> ids = request.get("ids");
        int count = notificationService.batchMarkAsRead(ids, userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 标记所有通知为已读
     */
    @PutMapping("/mark-all-read")
    public ApiResponse<Integer> markAllAsRead() {
        Long userId = getCurrentUserId();
        int count = notificationService.markAllAsRead(userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteNotification(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        boolean success = notificationService.deleteNotification(id, userId);
        return ApiResponse.success(success);
    }
    
    /**
     * 批量删除通知
     */
    @DeleteMapping("/batch-delete")
    public ApiResponse<Integer> batchDeleteNotifications(@RequestBody Map<String, List<Long>> request) {
        Long userId = getCurrentUserId();
        List<Long> ids = request.get("ids");
        int count = notificationService.batchDeleteNotifications(ids, userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Notification> getNotificationById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return notificationService.getNotificationById(id, userId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("通知不存在"));
    }
    
    /**
     * 获取通知设置
     */
    @GetMapping("/settings")
    public ApiResponse<NotificationSettings> getNotificationSettings() {
        Long userId = getCurrentUserId();
        NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
        return ApiResponse.success(settings);
    }
    
    /**
     * 更新通知设置
     */
    @PutMapping("/settings")
    public ApiResponse<NotificationSettings> updateNotificationSettings(@RequestBody NotificationSettings settings) {
        Long userId = getCurrentUserId();
        NotificationSettings updatedSettings = settingsService.updateUserSettings(userId, settings);
        return ApiResponse.success(updatedSettings);
    }
    
    /**
     * 发送测试通知
     */
    @PostMapping("/test")
    public ApiResponse<Notification> sendTestNotification(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        String typeStr = (String) request.get("type");
        String channelStr = (String) request.get("channel");
        
        NotificationType type = typeStr != null ? NotificationType.valueOf(typeStr) : NotificationType.SYSTEM;
        NotificationChannel channel = channelStr != null ? NotificationChannel.valueOf(channelStr) : NotificationChannel.IN_SITE;
        
        Notification notification = notificationService.sendNotification(
                userId, title, content, type, channel);
        
        return ApiResponse.success(notification);
    }
    
    /**
     * 获取通知统计
     */
    @GetMapping("/stats")
    public ApiResponse<List<Object[]>> getNotificationStats(
            @RequestParam(required = false) NotificationStatus status) {
        Long userId = getCurrentUserId();
        NotificationStatus queryStatus = status != null ? status : NotificationStatus.UNREAD;
        List<Object[]> stats = notificationService.getNotificationStats(userId, queryStatus);
        return ApiResponse.success(stats);
    }
    
    /**
     * 清理过期通知
     */
    @PostMapping("/clean-expired")
    public ApiResponse<Integer> cleanExpiredNotifications() {
        int count = notificationService.cleanExpiredNotifications();
        return ApiResponse.success(count);
    }
}
