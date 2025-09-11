package com.magic.crm.entity;

/**
 * 通知优先级枚举
 */
public enum NotificationPriority {
    /**
     * 低
     */
    LOW("低"),
    
    /**
     * 普通
     */
    NORMAL("普通"),
    
    /**
     * 高
     */
    HIGH("高"),
    
    /**
     * 紧急
     */
    URGENT("紧急");
    
    private final String description;
    
    NotificationPriority(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
