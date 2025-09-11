package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.magic.crm.config.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 通知模板实体类
 */
@Entity
@Table(name = "notification_templates")
public class NotificationTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 模板名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 模板代码（唯一标识）
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    /**
     * 模板描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 通知类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    /**
     * 通知渠道
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;
    
    /**
     * 模板标题
     */
    @Column(name = "title_template", nullable = false, length = 200)
    private String titleTemplate;
    
    /**
     * 模板内容
     */
    @Column(name = "content_template", columnDefinition = "TEXT")
    private String contentTemplate;
    
    /**
     * 模板变量（JSON格式）
     */
    @Column(name = "variables", columnDefinition = "TEXT")
    private String variables;
    
    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
    
    /**
     * 是否系统模板
     */
    @Column(name = "is_system")
    private Boolean isSystem = false;
    
    /**
     * 创建者ID
     */
    @Column(name = "created_by")
    private Long createdBy;
    
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
    public NotificationTemplate() {}
    
    public NotificationTemplate(String name, String code, NotificationType type, NotificationChannel channel) {
        this.name = name;
        this.code = code;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getTitleTemplate() {
        return titleTemplate;
    }
    
    public void setTitleTemplate(String titleTemplate) {
        this.titleTemplate = titleTemplate;
    }
    
    public String getContentTemplate() {
        return contentTemplate;
    }
    
    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }
    
    public String getVariables() {
        return variables;
    }
    
    public void setVariables(String variables) {
        this.variables = variables;
    }
    
    public Boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public Boolean getIsSystem() {
        return isSystem;
    }
    
    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
