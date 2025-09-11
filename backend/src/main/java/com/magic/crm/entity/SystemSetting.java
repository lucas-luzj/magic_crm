package com.magic.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 系统设置实体
 */
@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 设置键
     */
    @Column(nullable = false, unique = true, length = 100)
    private String settingKey;
    
    /**
     * 设置值
     */
    @Column(columnDefinition = "TEXT")
    private String settingValue;
    
    /**
     * 设置名称
     */
    @Column(nullable = false, length = 200)
    private String settingName;
    
    /**
     * 设置描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 设置类型：STRING, NUMBER, BOOLEAN, JSON, EMAIL, URL等
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettingType settingType;
    
    /**
     * 设置分组
     */
    @Column(nullable = false, length = 50)
    private String settingGroup;
    
    /**
     * 是否敏感信息（需要加密存储）
     */
    @Column(nullable = false)
    private Boolean isSensitive = false;
    
    /**
     * 是否可编辑
     */
    @Column(nullable = false)
    private Boolean isEditable = true;
    
    /**
     * 默认值
     */
    @Column(columnDefinition = "TEXT")
    private String defaultValue;
    
    /**
     * 验证规则（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String validationRule;
    
    /**
     * 排序号
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 创建人
     */
    @Column(nullable = false)
    private Long createdBy;
    
    /**
     * 更新人
     */
    @Column(nullable = false)
    private Long updatedBy;
    
    /**
     * 设置类型枚举
     */
    public enum SettingType {
        STRING("字符串"),
        NUMBER("数字"),
        BOOLEAN("布尔值"),
        JSON("JSON对象"),
        EMAIL("邮箱"),
        URL("链接"),
        PASSWORD("密码"),
        TEXT("长文本");
        
        private final String description;
        
        SettingType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
