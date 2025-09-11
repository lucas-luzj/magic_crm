package com.magic.crm.service;

import com.magic.crm.entity.NotificationSettings;
import com.magic.crm.repository.NotificationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 通知设置服务类
 */
@Service
@Transactional
public class NotificationSettingsService {
    
    @Autowired
    private NotificationSettingsRepository settingsRepository;
    
    /**
     * 获取用户通知设置
     */
    public Optional<NotificationSettings> getUserSettings(Long userId) {
        return settingsRepository.findByUserId(userId);
    }
    
    /**
     * 获取或创建用户通知设置
     */
    public NotificationSettings getOrCreateUserSettings(Long userId) {
        return settingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationSettings settings = new NotificationSettings(userId);
                    return settingsRepository.save(settings);
                });
    }
    
    /**
     * 更新用户通知设置
     */
    public NotificationSettings updateUserSettings(Long userId, NotificationSettings settings) {
        NotificationSettings existingSettings = getOrCreateUserSettings(userId);
        
        // 更新设置
        if (settings.getEnableInSite() != null) {
            existingSettings.setEnableInSite(settings.getEnableInSite());
        }
        if (settings.getEnableEmail() != null) {
            existingSettings.setEnableEmail(settings.getEnableEmail());
        }
        if (settings.getEnableSms() != null) {
            existingSettings.setEnableSms(settings.getEnableSms());
        }
        if (settings.getEnableWechat() != null) {
            existingSettings.setEnableWechat(settings.getEnableWechat());
        }
        if (settings.getEnableDingtalk() != null) {
            existingSettings.setEnableDingtalk(settings.getEnableDingtalk());
        }
        if (settings.getEnablePush() != null) {
            existingSettings.setEnablePush(settings.getEnablePush());
        }
        if (settings.getWorkflowNotifications() != null) {
            existingSettings.setWorkflowNotifications(settings.getWorkflowNotifications());
        }
        if (settings.getTaskNotifications() != null) {
            existingSettings.setTaskNotifications(settings.getTaskNotifications());
        }
        if (settings.getSystemNotifications() != null) {
            existingSettings.setSystemNotifications(settings.getSystemNotifications());
        }
        if (settings.getEmailNotificationTime() != null) {
            existingSettings.setEmailNotificationTime(settings.getEmailNotificationTime());
        }
        if (settings.getSmsNotificationTime() != null) {
            existingSettings.setSmsNotificationTime(settings.getSmsNotificationTime());
        }
        if (settings.getQuietHoursStart() != null) {
            existingSettings.setQuietHoursStart(settings.getQuietHoursStart());
        }
        if (settings.getQuietHoursEnd() != null) {
            existingSettings.setQuietHoursEnd(settings.getQuietHoursEnd());
        }
        if (settings.getEnableWeekendNotifications() != null) {
            existingSettings.setEnableWeekendNotifications(settings.getEnableWeekendNotifications());
        }
        if (settings.getEnableHolidayNotifications() != null) {
            existingSettings.setEnableHolidayNotifications(settings.getEnableHolidayNotifications());
        }
        if (settings.getFrequencyLimitMinutes() != null) {
            existingSettings.setFrequencyLimitMinutes(settings.getFrequencyLimitMinutes());
        }
        
        return settingsRepository.save(existingSettings);
    }
    
    /**
     * 检查用户是否启用特定渠道的通知
     */
    public boolean isChannelEnabled(Long userId, String channel) {
        NotificationSettings settings = getOrCreateUserSettings(userId);
        
        return switch (channel.toLowerCase()) {
            case "in_site" -> settings.getEnableInSite();
            case "email" -> settings.getEnableEmail();
            case "sms" -> settings.getEnableSms();
            case "wechat" -> settings.getEnableWechat();
            case "dingtalk" -> settings.getEnableDingtalk();
            case "push" -> settings.getEnablePush();
            default -> false;
        };
    }
    
    /**
     * 检查是否在免打扰时间内
     */
    public boolean isInQuietHours(Long userId) {
        NotificationSettings settings = getOrCreateUserSettings(userId);
        // 这里可以实现更复杂的免打扰时间逻辑
        // 暂时返回false，表示不在免打扰时间
        return false;
    }
    
    /**
     * 检查是否应该发送通知（考虑频率限制）
     */
    public boolean shouldSendNotification(Long userId, String channel) {
        NotificationSettings settings = getOrCreateUserSettings(userId);
        
        // 检查渠道是否启用
        if (!isChannelEnabled(userId, channel)) {
            return false;
        }
        
        // 检查是否在免打扰时间
        if (isInQuietHours(userId)) {
            return false;
        }
        
        // 这里可以添加频率限制检查逻辑
        // 暂时返回true
        return true;
    }
    
    /**
     * 重置用户通知设置为默认值
     */
    public NotificationSettings resetToDefault(Long userId) {
        NotificationSettings settings = new NotificationSettings(userId);
        return settingsRepository.save(settings);
    }
}
