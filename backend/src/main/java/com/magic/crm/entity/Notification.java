package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.magic.crm.config.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 接收用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 发送用户ID（可选，系统通知时为空）
     */
    @Column(name = "sender_id")
    private Long senderId;
    
    /**
     * 通知标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    /**
     * 通知内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 通知类型
     * SYSTEM: 系统通知
     * WORKFLOW: 工作流通知
     * TASK: 任务通知
     * MESSAGE: 消息通知
     * REMINDER: 提醒通知
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    /**
     * 通知渠道
     * IN_SITE: 站内信
     * EMAIL: 邮件
     * SMS: 短信
     * WECHAT: 微信
     * DINGTALK: 钉钉
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;
    
    /**
     * 通知状态
     * UNREAD: 未读
     * READ: 已读
     * ARCHIVED: 已归档
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    /**
     * 优先级
     * LOW: 低
     * NORMAL: 普通
     * HIGH: 高
     * URGENT: 紧急
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private NotificationPriority priority = NotificationPriority.NORMAL;
    
    /**
     * 关联的业务ID（如流程实例ID、任务ID等）
     */
    @Column(name = "business_id")
    private String businessId;
    
    /**
     * 关联的业务类型
     */
    @Column(name = "business_type")
    private String businessType;
    
    /**
     * 跳转链接
     */
    @Column(name = "action_url")
    private String actionUrl;
    
    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expireTime;
    
    /**
     * 是否已发送
     */
    @Column(name = "is_sent")
    private Boolean isSent = false;
    
    /**
     * 发送时间
     */
    @Column(name = "sent_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sentTime;
    
    /**
     * 发送结果
     */
    @Column(name = "send_result")
    private String sendResult;
    
    /**
     * 重试次数
     */
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    /**
     * 扩展数据（JSON格式）
     */
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;
    
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
    
    /**
     * 已读时间
     */
    @Column(name = "read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime readTime;
    
    // 构造函数
    public Notification() {}
    
    public Notification(Long userId, String title, String content, NotificationType type, NotificationChannel channel) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.channel = channel;
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
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationChannel getChannel() {
        return channel;
    }
    
    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public NotificationPriority getPriority() {
        return priority;
    }
    
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    
    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public Boolean getIsSent() {
        return isSent;
    }
    
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }
    
    public LocalDateTime getSentTime() {
        return sentTime;
    }
    
    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }
    
    public String getSendResult() {
        return sendResult;
    }
    
    public void setSendResult(String sendResult) {
        this.sendResult = sendResult;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
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
    
    public LocalDateTime getReadTime() {
        return readTime;
    }
    
    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }
    
    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readTime = LocalDateTime.now();
    }
    
    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
}
