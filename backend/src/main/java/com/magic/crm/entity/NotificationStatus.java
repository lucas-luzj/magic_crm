package com.magic.crm.entity;

/**
 * 通知状态枚举
 */
public enum NotificationStatus {
    /**
     * 未读
     */
    UNREAD("未读"),
    
    /**
     * 已读
     */
    READ("已读"),
    
    /**
     * 已归档
     */
    ARCHIVED("已归档"),
    
    /**
     * 已删除
     */
    DELETED("已删除");
    
    private final String description;
    
    NotificationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
