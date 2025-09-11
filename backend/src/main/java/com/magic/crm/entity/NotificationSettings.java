package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.magic.crm.config.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 通知设置实体类
 */
@Entity
@Table(name = "notification_settings")
public class NotificationSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    /**
     * 是否启用站内信通知
     */
    @Column(name = "enable_in_site")
    private Boolean enableInSite = true;
    
    /**
     * 是否启用邮件通知
     */
    @Column(name = "enable_email")
    private Boolean enableEmail = true;
    
    /**
     * 是否启用短信通知
     */
    @Column(name = "enable_sms")
    private Boolean enableSms = false;
    
    /**
     * 是否启用微信通知
     */
    @Column(name = "enable_wechat")
    private Boolean enableWechat = false;
    
    /**
     * 是否启用钉钉通知
     */
    @Column(name = "enable_dingtalk")
    private Boolean enableDingtalk = false;
    
    /**
     * 是否启用推送通知
     */
    @Column(name = "enable_push")
    private Boolean enablePush = true;
    
    /**
     * 工作流通知设置
     */
    @Column(name = "workflow_notifications", columnDefinition = "TEXT")
    private String workflowNotifications;
    
    /**
     * 任务通知设置
     */
    @Column(name = "task_notifications", columnDefinition = "TEXT")
    private String taskNotifications;
    
    /**
     * 系统通知设置
     */
    @Column(name = "system_notifications", columnDefinition = "TEXT")
    private String systemNotifications;
    
    /**
     * 邮件通知时间（24小时制，如：09:00-18:00）
     */
    @Column(name = "email_notification_time")
    private String emailNotificationTime = "09:00-18:00";
    
    /**
     * 短信通知时间
     */
    @Column(name = "sms_notification_time")
    private String smsNotificationTime = "09:00-18:00";
    
    /**
     * 免打扰时间开始
     */
    @Column(name = "quiet_hours_start")
    private String quietHoursStart = "22:00";
    
    /**
     * 免打扰时间结束
     */
    @Column(name = "quiet_hours_end")
    private String quietHoursEnd = "08:00";
    
    /**
     * 是否在周末接收通知
     */
    @Column(name = "enable_weekend_notifications")
    private Boolean enableWeekendNotifications = false;
    
    /**
     * 是否在节假日接收通知
     */
    @Column(name = "enable_holiday_notifications")
    private Boolean enableHolidayNotifications = false;
    
    /**
     * 通知频率限制（分钟）
     */
    @Column(name = "frequency_limit_minutes")
    private Integer frequencyLimitMinutes = 5;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    
    // 构造函数
    public NotificationSettings() {}
    
    public NotificationSettings(Long userId) {
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Boolean getEnableInSite() {
        return enableInSite;
    }
    
    public void setEnableInSite(Boolean enableInSite) {
        this.enableInSite = enableInSite;
    }
    
    public Boolean getEnableEmail() {
        return enableEmail;
    }
    
    public void setEnableEmail(Boolean enableEmail) {
        this.enableEmail = enableEmail;
    }
    
    public Boolean getEnableSms() {
        return enableSms;
    }
    
    public void setEnableSms(Boolean enableSms) {
        this.enableSms = enableSms;
    }
    
    public Boolean getEnableWechat() {
        return enableWechat;
    }
    
    public void setEnableWechat(Boolean enableWechat) {
        this.enableWechat = enableWechat;
    }
    
    public Boolean getEnableDingtalk() {
        return enableDingtalk;
    }
    
    public void setEnableDingtalk(Boolean enableDingtalk) {
        this.enableDingtalk = enableDingtalk;
    }
    
    public Boolean getEnablePush() {
        return enablePush;
    }
    
    public void setEnablePush(Boolean enablePush) {
        this.enablePush = enablePush;
    }
    
    public String getWorkflowNotifications() {
        return workflowNotifications;
    }
    
    public void setWorkflowNotifications(String workflowNotifications) {
        this.workflowNotifications = workflowNotifications;
    }
    
    public String getTaskNotifications() {
        return taskNotifications;
    }
    
    public void setTaskNotifications(String taskNotifications) {
        this.taskNotifications = taskNotifications;
    }
    
    public String getSystemNotifications() {
        return systemNotifications;
    }
    
    public void setSystemNotifications(String systemNotifications) {
        this.systemNotifications = systemNotifications;
    }
    
    public String getEmailNotificationTime() {
        return emailNotificationTime;
    }
    
    public void setEmailNotificationTime(String emailNotificationTime) {
        this.emailNotificationTime = emailNotificationTime;
    }
    
    public String getSmsNotificationTime() {
        return smsNotificationTime;
    }
    
    public void setSmsNotificationTime(String smsNotificationTime) {
        this.smsNotificationTime = smsNotificationTime;
    }
    
    public String getQuietHoursStart() {
        return quietHoursStart;
    }
    
    public void setQuietHoursStart(String quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }
    
    public String getQuietHoursEnd() {
        return quietHoursEnd;
    }
    
    public void setQuietHoursEnd(String quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }
    
    public Boolean getEnableWeekendNotifications() {
        return enableWeekendNotifications;
    }
    
    public void setEnableWeekendNotifications(Boolean enableWeekendNotifications) {
        this.enableWeekendNotifications = enableWeekendNotifications;
    }
    
    public Boolean getEnableHolidayNotifications() {
        return enableHolidayNotifications;
    }
    
    public void setEnableHolidayNotifications(Boolean enableHolidayNotifications) {
        this.enableHolidayNotifications = enableHolidayNotifications;
    }
    
    public Integer getFrequencyLimitMinutes() {
        return frequencyLimitMinutes;
    }
    
    public void setFrequencyLimitMinutes(Integer frequencyLimitMinutes) {
        this.frequencyLimitMinutes = frequencyLimitMinutes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
