package com.magic.crm.dto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * 客户公海池操作DTO
 */
public class CustomerPoolDTO {
    
    @NotNull(message = "客户ID不能为空")
    private List<UUID> customerIds;
    
    private String reason; // 进入公海原因
    
    private UUID operatorId; // 操作人ID
    
    private String operationType; // 操作类型: MOVE_TO_POOL, CLAIM_FROM_POOL, ASSIGN, TRANSFER
    
    private UUID targetOwnerId; // 目标所属人ID（认领、分配、转移时使用）
    
    // Getters and Setters
    public List<UUID> getCustomerIds() {
        return customerIds;
    }
    
    public void setCustomerIds(List<UUID> customerIds) {
        this.customerIds = customerIds;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public UUID getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public UUID getTargetOwnerId() {
        return targetOwnerId;
    }
    
    public void setTargetOwnerId(UUID targetOwnerId) {
        this.targetOwnerId = targetOwnerId;
    }
}