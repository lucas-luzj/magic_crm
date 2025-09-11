package com.magic.crm.controller;

import com.magic.crm.entity.NotificationSettings;
import com.magic.crm.entity.User;
import com.magic.crm.service.NotificationSettingsService;
import com.magic.crm.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知设置控制器
 */
@RestController
@RequestMapping("/api/notification-settings")
@CrossOrigin(origins = "*")
public class NotificationSettingsController {
    
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
     * 获取用户通知设置
     */
    @GetMapping
    public ApiResponse<NotificationSettings> getUserSettings() {
        Long userId = getCurrentUserId();
        NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
        return ApiResponse.success(settings);
    }
    
    /**
     * 更新用户通知设置
     */
    @PutMapping
    public ApiResponse<NotificationSettings> updateUserSettings(@RequestBody NotificationSettings settings) {
        try {
            Long userId = getCurrentUserId();
            NotificationSettings updatedSettings = settingsService.updateUserSettings(userId, settings);
            return ApiResponse.success(updatedSettings);
        } catch (Exception e) {
            return ApiResponse.error("更新设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置为默认设置
     */
    @PostMapping("/reset")
    public ApiResponse<NotificationSettings> resetToDefault() {
        try {
            Long userId = getCurrentUserId();
            NotificationSettings defaultSettings = settingsService.resetToDefault(userId);
            return ApiResponse.success(defaultSettings);
        } catch (Exception e) {
            return ApiResponse.error("重置设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查渠道是否启用
     */
    @GetMapping("/channel/{channel}/enabled")
    public ApiResponse<Boolean> isChannelEnabled(@PathVariable String channel) {
        Long userId = getCurrentUserId();
        boolean enabled = settingsService.isChannelEnabled(userId, channel);
        return ApiResponse.success(enabled);
    }
    
    /**
     * 检查是否在免打扰时间
     */
    @GetMapping("/quiet-hours")
    public ApiResponse<Boolean> isInQuietHours() {
        Long userId = getCurrentUserId();
        boolean inQuietHours = settingsService.isInQuietHours(userId);
        return ApiResponse.success(inQuietHours);
    }
    
    /**
     * 检查是否应该发送通知
     */
    @GetMapping("/should-send/{channel}")
    public ApiResponse<Boolean> shouldSendNotification(@PathVariable String channel) {
        Long userId = getCurrentUserId();
        boolean shouldSend = settingsService.shouldSendNotification(userId, channel);
        return ApiResponse.success(shouldSend);
    }
    
    /**
     * 批量更新渠道设置
     */
    @PutMapping("/channels")
    public ApiResponse<NotificationSettings> updateChannelSettings(@RequestBody Map<String, Boolean> channelSettings) {
        try {
            Long userId = getCurrentUserId();
            NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
            
            // 更新渠道设置
            if (channelSettings.containsKey("inSite")) {
                settings.setEnableInSite(channelSettings.get("inSite"));
            }
            if (channelSettings.containsKey("email")) {
                settings.setEnableEmail(channelSettings.get("email"));
            }
            if (channelSettings.containsKey("sms")) {
                settings.setEnableSms(channelSettings.get("sms"));
            }
            if (channelSettings.containsKey("wechat")) {
                settings.setEnableWechat(channelSettings.get("wechat"));
            }
            if (channelSettings.containsKey("dingtalk")) {
                settings.setEnableDingtalk(channelSettings.get("dingtalk"));
            }
            if (channelSettings.containsKey("push")) {
                settings.setEnablePush(channelSettings.get("push"));
            }
            
            NotificationSettings updatedSettings = settingsService.updateUserSettings(userId, settings);
            return ApiResponse.success(updatedSettings);
        } catch (Exception e) {
            return ApiResponse.error("更新渠道设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新时间设置
     */
    @PutMapping("/time")
    public ApiResponse<NotificationSettings> updateTimeSettings(@RequestBody Map<String, String> timeSettings) {
        try {
            Long userId = getCurrentUserId();
            NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
            
            // 更新时间设置
            if (timeSettings.containsKey("emailNotificationTime")) {
                settings.setEmailNotificationTime(timeSettings.get("emailNotificationTime"));
            }
            if (timeSettings.containsKey("smsNotificationTime")) {
                settings.setSmsNotificationTime(timeSettings.get("smsNotificationTime"));
            }
            if (timeSettings.containsKey("quietHoursStart")) {
                settings.setQuietHoursStart(timeSettings.get("quietHoursStart"));
            }
            if (timeSettings.containsKey("quietHoursEnd")) {
                settings.setQuietHoursEnd(timeSettings.get("quietHoursEnd"));
            }
            
            NotificationSettings updatedSettings = settingsService.updateUserSettings(userId, settings);
            return ApiResponse.success(updatedSettings);
        } catch (Exception e) {
            return ApiResponse.error("更新时间设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新通知类型设置
     */
    @PutMapping("/types")
    public ApiResponse<NotificationSettings> updateTypeSettings(@RequestBody Map<String, String> typeSettings) {
        try {
            Long userId = getCurrentUserId();
            NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
            
            // 更新类型设置
            if (typeSettings.containsKey("workflowNotifications")) {
                settings.setWorkflowNotifications(typeSettings.get("workflowNotifications"));
            }
            if (typeSettings.containsKey("taskNotifications")) {
                settings.setTaskNotifications(typeSettings.get("taskNotifications"));
            }
            if (typeSettings.containsKey("systemNotifications")) {
                settings.setSystemNotifications(typeSettings.get("systemNotifications"));
            }
            
            NotificationSettings updatedSettings = settingsService.updateUserSettings(userId, settings);
            return ApiResponse.success(updatedSettings);
        } catch (Exception e) {
            return ApiResponse.error("更新类型设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新其他设置
     */
    @PutMapping("/other")
    public ApiResponse<NotificationSettings> updateOtherSettings(@RequestBody Map<String, Object> otherSettings) {
        try {
            Long userId = getCurrentUserId();
            NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
            
            // 更新其他设置
            if (otherSettings.containsKey("enableWeekendNotifications")) {
                settings.setEnableWeekendNotifications((Boolean) otherSettings.get("enableWeekendNotifications"));
            }
            if (otherSettings.containsKey("enableHolidayNotifications")) {
                settings.setEnableHolidayNotifications((Boolean) otherSettings.get("enableHolidayNotifications"));
            }
            if (otherSettings.containsKey("frequencyLimitMinutes")) {
                settings.setFrequencyLimitMinutes((Integer) otherSettings.get("frequencyLimitMinutes"));
            }
            
            NotificationSettings updatedSettings = settingsService.updateUserSettings(userId, settings);
            return ApiResponse.success(updatedSettings);
        } catch (Exception e) {
            return ApiResponse.error("更新其他设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取默认设置
     */
    @GetMapping("/default")
    public ApiResponse<NotificationSettings> getDefaultSettings() {
        NotificationSettings defaultSettings = new NotificationSettings(1L);
        return ApiResponse.success(defaultSettings);
    }
    
    /**
     * 导出设置
     */
    @GetMapping("/export")
    public ApiResponse<Map<String, Object>> exportSettings() {
        Long userId = getCurrentUserId();
        NotificationSettings settings = settingsService.getOrCreateUserSettings(userId);
        
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("userId", settings.getUserId());
        exportData.put("enableInSite", settings.getEnableInSite());
        exportData.put("enableEmail", settings.getEnableEmail());
        exportData.put("enableSms", settings.getEnableSms());
        exportData.put("enableWechat", settings.getEnableWechat());
        exportData.put("enableDingtalk", settings.getEnableDingtalk());
        exportData.put("enablePush", settings.getEnablePush());
        exportData.put("emailNotificationTime", settings.getEmailNotificationTime());
        exportData.put("smsNotificationTime", settings.getSmsNotificationTime());
        exportData.put("quietHoursStart", settings.getQuietHoursStart());
        exportData.put("quietHoursEnd", settings.getQuietHoursEnd());
        exportData.put("enableWeekendNotifications", settings.getEnableWeekendNotifications());
        exportData.put("enableHolidayNotifications", settings.getEnableHolidayNotifications());
        exportData.put("frequencyLimitMinutes", settings.getFrequencyLimitMinutes());
        exportData.put("workflowNotifications", settings.getWorkflowNotifications());
        exportData.put("taskNotifications", settings.getTaskNotifications());
        exportData.put("systemNotifications", settings.getSystemNotifications());
        
        return ApiResponse.success(exportData);
    }
}
