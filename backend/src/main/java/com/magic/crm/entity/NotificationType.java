package com.magic.crm.entity;

/**
 * 通知类型枚举
 */
public enum NotificationType {
    /**
     * 系统通知
     */
    SYSTEM("系统通知"),
    
    /**
     * 工作流通知
     */
    WORKFLOW("工作流通知"),
    
    /**
     * 任务通知
     */
    TASK("任务通知"),
    
    /**
     * 消息通知
     */
    MESSAGE("消息通知"),
    
    /**
     * 提醒通知
     */
    REMINDER("提醒通知"),
    
    /**
     * 审批通知
     */
    APPROVAL("审批通知"),
    
    /**
     * 流程通知
     */
    PROCESS("流程通知"),
    
    /**
     * 客户通知
     */
    CUSTOMER("客户通知"),
    
    /**
     * 销售通知
     */
    SALES("销售通知"),
    
    /**
     * 营销通知
     */
    MARKETING("营销通知");
    
    private final String description;
    
    NotificationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
