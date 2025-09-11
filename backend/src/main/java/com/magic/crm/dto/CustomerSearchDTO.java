package com.magic.crm.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 客户搜索条件DTO
 */
public class CustomerSearchDTO {
    
    private String keyword; // 关键词（名称、简称、联系人）
    
    private String code; // 客户编号
    
    private String name; // 客户名称
    
    private String uscc; // 统一社会信用代码
    
    private String industry; // 行业
    
    private String region; // 地区
    
    private String customerLevel; // 客户等级
    
    private String customerType; // 客户类型
    
    private String source; // 来源
    
    private UUID ownerId; // 所属销售
    
    private UUID orgUnitId; // 所属组织
    
    private Boolean isKeyCustomer; // 是否重点客户
    
    private Boolean isBlacklist; // 是否黑名单
    
    private Boolean isPublicPool; // 是否公海池
    
    private LocalDateTime createdAtStart; // 创建时间开始
    
    private LocalDateTime createdAtEnd; // 创建时间结束
    
    private LocalDateTime lastFollowTimeStart; // 最后跟进时间开始
    
    private LocalDateTime lastFollowTimeEnd; // 最后跟进时间结束
    
    private LocalDateTime lastOrderTimeStart; // 最后成单时间开始
    
    private LocalDateTime lastOrderTimeEnd; // 最后成单时间结束
    
    private String status; // 状态
    
    private List<UUID> customerIds; // 客户ID列表
    
    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUscc() {
        return uscc;
    }
    
    public void setUscc(String uscc) {
        this.uscc = uscc;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getCustomerLevel() {
        return customerLevel;
    }
    
    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    
    public UUID getOrgUnitId() {
        return orgUnitId;
    }
    
    public void setOrgUnitId(UUID orgUnitId) {
        this.orgUnitId = orgUnitId;
    }
    
    public Boolean getIsKeyCustomer() {
        return isKeyCustomer;
    }
    
    public void setIsKeyCustomer(Boolean isKeyCustomer) {
        this.isKeyCustomer = isKeyCustomer;
    }
    
    public Boolean getIsBlacklist() {
        return isBlacklist;
    }
    
    public void setIsBlacklist(Boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
    }
    
    public Boolean getIsPublicPool() {
        return isPublicPool;
    }
    
    public void setIsPublicPool(Boolean isPublicPool) {
        this.isPublicPool = isPublicPool;
    }
    
    public LocalDateTime getCreatedAtStart() {
        return createdAtStart;
    }
    
    public void setCreatedAtStart(LocalDateTime createdAtStart) {
        this.createdAtStart = createdAtStart;
    }
    
    public LocalDateTime getCreatedAtEnd() {
        return createdAtEnd;
    }
    
    public void setCreatedAtEnd(LocalDateTime createdAtEnd) {
        this.createdAtEnd = createdAtEnd;
    }
    
    public LocalDateTime getLastFollowTimeStart() {
        return lastFollowTimeStart;
    }
    
    public void setLastFollowTimeStart(LocalDateTime lastFollowTimeStart) {
        this.lastFollowTimeStart = lastFollowTimeStart;
    }
    
    public LocalDateTime getLastFollowTimeEnd() {
        return lastFollowTimeEnd;
    }
    
    public void setLastFollowTimeEnd(LocalDateTime lastFollowTimeEnd) {
        this.lastFollowTimeEnd = lastFollowTimeEnd;
    }
    
    public LocalDateTime getLastOrderTimeStart() {
        return lastOrderTimeStart;
    }
    
    public void setLastOrderTimeStart(LocalDateTime lastOrderTimeStart) {
        this.lastOrderTimeStart = lastOrderTimeStart;
    }
    
    public LocalDateTime getLastOrderTimeEnd() {
        return lastOrderTimeEnd;
    }
    
    public void setLastOrderTimeEnd(LocalDateTime lastOrderTimeEnd) {
        this.lastOrderTimeEnd = lastOrderTimeEnd;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<UUID> getCustomerIds() {
        return customerIds;
    }
    
    public void setCustomerIds(List<UUID> customerIds) {
        this.customerIds = customerIds;
    }
}