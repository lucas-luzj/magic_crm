package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 客户数据传输对象
 */
public class CustomerDTO {
    
    private UUID id;
    
    private String code;
    
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200个字符")
    private String name;
    
    @Size(max = 100, message = "简称长度不能超过100个字符")
    private String shortName;
    
    @Size(max = 50, message = "统一社会信用代码长度不能超过50个字符")
    private String uscc;
    
    private String industry;
    
    private String region;
    
    @Size(max = 500, message = "地址长度不能超过500个字符")
    private String address;
    
    private String website;
    
    private String companySize;
    
    private String customerLevel;
    
    private String customerType;
    
    private String source;
    
    private UUID ownerId;
    
    private String ownerName;
    
    private List<UUID> collaboratorIds;
    
    private List<String> collaboratorNames;
    
    private UUID parentCustomerId;
    
    private String parentCustomerName;
    
    private Boolean isKeyCustomer = false;
    
    private Boolean isBlacklist = false;
    
    private Boolean isPublicPool = false;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime poolEntryTime;
    
    private String poolEntryReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFollowTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOrderTime;
    
    private UUID orgUnitId;
    
    private String orgUnitName;
    
    private String status = "ACTIVE";
    
    private String remark;
    
    private List<Map<String, Object>> attachments;
    
    private Map<String, Object> customFields;
    
    private UUID createdBy;
    
    private String createdByName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private UUID updatedBy;
    
    private String updatedByName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 关联数据
    private Integer contactCount;
    
    private Integer opportunityCount;
    
    private Integer contractCount;
    
    private Integer activityCount;
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
    
    public String getShortName() {
        return shortName;
    }
    
    public void setShortName(String shortName) {
        this.shortName = shortName;
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getCompanySize() {
        return companySize;
    }
    
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
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
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public List<UUID> getCollaboratorIds() {
        return collaboratorIds;
    }
    
    public void setCollaboratorIds(List<UUID> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }
    
    public List<String> getCollaboratorNames() {
        return collaboratorNames;
    }
    
    public void setCollaboratorNames(List<String> collaboratorNames) {
        this.collaboratorNames = collaboratorNames;
    }
    
    public UUID getParentCustomerId() {
        return parentCustomerId;
    }
    
    public void setParentCustomerId(UUID parentCustomerId) {
        this.parentCustomerId = parentCustomerId;
    }
    
    public String getParentCustomerName() {
        return parentCustomerName;
    }
    
    public void setParentCustomerName(String parentCustomerName) {
        this.parentCustomerName = parentCustomerName;
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
    
    public LocalDateTime getPoolEntryTime() {
        return poolEntryTime;
    }
    
    public void setPoolEntryTime(LocalDateTime poolEntryTime) {
        this.poolEntryTime = poolEntryTime;
    }
    
    public String getPoolEntryReason() {
        return poolEntryReason;
    }
    
    public void setPoolEntryReason(String poolEntryReason) {
        this.poolEntryReason = poolEntryReason;
    }
    
    public LocalDateTime getLastFollowTime() {
        return lastFollowTime;
    }
    
    public void setLastFollowTime(LocalDateTime lastFollowTime) {
        this.lastFollowTime = lastFollowTime;
    }
    
    public LocalDateTime getLastOrderTime() {
        return lastOrderTime;
    }
    
    public void setLastOrderTime(LocalDateTime lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }
    
    public UUID getOrgUnitId() {
        return orgUnitId;
    }
    
    public void setOrgUnitId(UUID orgUnitId) {
        this.orgUnitId = orgUnitId;
    }
    
    public String getOrgUnitName() {
        return orgUnitName;
    }
    
    public void setOrgUnitName(String orgUnitName) {
        this.orgUnitName = orgUnitName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public List<Map<String, Object>> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Map<String, Object>> attachments) {
        this.attachments = attachments;
    }
    
    public Map<String, Object> getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public UUID getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public String getUpdatedByName() {
        return updatedByName;
    }
    
    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getContactCount() {
        return contactCount;
    }
    
    public void setContactCount(Integer contactCount) {
        this.contactCount = contactCount;
    }
    
    public Integer getOpportunityCount() {
        return opportunityCount;
    }
    
    public void setOpportunityCount(Integer opportunityCount) {
        this.opportunityCount = opportunityCount;
    }
    
    public Integer getContractCount() {
        return contractCount;
    }
    
    public void setContractCount(Integer contractCount) {
        this.contractCount = contractCount;
    }
    
    public Integer getActivityCount() {
        return activityCount;
    }
    
    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }
}