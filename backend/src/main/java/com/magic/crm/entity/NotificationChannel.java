package com.magic.crm.entity;

/**
 * 通知渠道枚举
 */
public enum NotificationChannel {
    /**
     * 站内信
     */
    IN_SITE("站内信"),
    
    /**
     * 邮件
     */
    EMAIL("邮件"),
    
    /**
     * 短信
     */
    SMS("短信"),
    
    /**
     * 微信
     */
    WECHAT("微信"),
    
    /**
     * 钉钉
     */
    DINGTALK("钉钉"),
    
    /**
     * 推送通知
     */
    PUSH("推送通知"),
    
    /**
     * 系统消息
     */
    SYSTEM("系统消息");
    
    private final String description;
    
    NotificationChannel(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
