package com.magic.crm.controller;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationPriority;
import com.magic.crm.entity.NotificationType;
import com.magic.crm.service.NotificationService;
import com.magic.crm.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知测试控制器
 */
@RestController
@RequestMapping("/api/test/notifications")
@CrossOrigin(origins = "*")
public class NotificationTestController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 发送测试通知
     */
    @PostMapping("/send")
    public ApiResponse<Notification> sendTestNotification(@RequestBody Map<String, Object> request) {
        Long userId = 1L; // 测试用户ID
        
        String title = (String) request.getOrDefault("title", "测试通知");
        String content = (String) request.getOrDefault("content", "这是一个测试通知");
        String typeStr = (String) request.getOrDefault("type", "SYSTEM");
        String channelStr = (String) request.getOrDefault("channel", "IN_SITE");
        String priorityStr = (String) request.getOrDefault("priority", "NORMAL");
        
        NotificationType type = NotificationType.valueOf(typeStr);
        NotificationChannel channel = NotificationChannel.valueOf(channelStr);
        NotificationPriority priority = NotificationPriority.valueOf(priorityStr);
        
        Notification notification = notificationService.sendNotification(
            userId, null, title, content, type, channel, priority, null, null, null);
        
        return ApiResponse.success(notification);
    }
    
    /**
     * 获取用户通知列表
     */
    @GetMapping
    public ApiResponse<List<Notification>> getUserNotifications() {
        Long userId = 1L; // 测试用户ID
        var pageResponse = notificationService.getUserNotifications(userId, 1, 10, null, null, null, null, null);
        return ApiResponse.success(pageResponse.getRecords());
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = 1L; // 测试用户ID
        long count = notificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Boolean> markAsRead(@PathVariable Long id) {
        Long userId = 1L; // 测试用户ID
        boolean success = notificationService.markAsRead(id, userId);
        return ApiResponse.success(success);
    }
    
    /**
     * 批量发送通知
     */
    @PostMapping("/batch-send")
    public ApiResponse<List<Notification>> batchSendNotifications(@RequestBody Map<String, Object> request) {
        List<Long> userIds = (List<Long>) request.get("userIds");
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        String typeStr = (String) request.getOrDefault("type", "SYSTEM");
        String channelStr = (String) request.getOrDefault("channel", "IN_SITE");
        
        NotificationType type = NotificationType.valueOf(typeStr);
        NotificationChannel channel = NotificationChannel.valueOf(channelStr);
        
        List<Notification> notifications = notificationService.sendBatchNotifications(
            userIds, title, content, type, channel);
        
        return ApiResponse.success(notifications);
    }
    
    /**
     * 获取通知统计
     */
    @GetMapping("/stats")
    public ApiResponse<List<Object[]>> getNotificationStats() {
        Long userId = 1L; // 测试用户ID
        List<Object[]> stats = notificationService.getNotificationStats(userId, null);
        return ApiResponse.success(stats);
    }
}
