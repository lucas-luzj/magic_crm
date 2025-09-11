package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 线索数据传输对象
 */
public class LeadDTO {
    
    private UUID id;
    
    @NotBlank(message = "线索名称不能为空")
    @Size(max = 200, message = "线索名称长度不能超过200个字符")
    private String name;
    
    private String code;
    
    @Size(max = 200, message = "公司名称长度不能超过200个字符")
    private String companyName;
    
    @Size(max = 100, message = "联系人姓名长度不能超过100个字符")
    private String contactName;
    
    @Size(max = 20, message = "联系人电话长度不能超过20个字符")
    private String contactPhone;
    
    @Size(max = 100, message = "联系人邮箱长度不能超过100个字符")
    private String contactEmail;
    
    @Size(max = 100, message = "联系人职位长度不能超过100个字符")
    private String contactPosition;
    
    @Size(max = 100, message = "行业长度不能超过100个字符")
    private String industry;
    
    @Size(max = 100, message = "地区长度不能超过100个字符")
    private String region;
    
    @Size(max = 500, message = "地址长度不能超过500个字符")
    private String address;
    
    @Size(max = 200, message = "官网长度不能超过200个字符")
    private String website;
    
    @Size(max = 50, message = "公司规模长度不能超过50个字符")
    private String companySize;
    
    @Size(max = 50, message = "来源长度不能超过50个字符")
    private String source;
    
    @Size(max = 200, message = "来源详情长度不能超过200个字符")
    private String sourceDetail;
    
    private String status = "NEW";
    
    private String priority = "MEDIUM";
    
    private Integer score = 0;
    
    private BigDecimal estimatedValue;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime estimatedCloseDate;
    
    private UUID ownerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastContactTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;
    
    private UUID convertedCustomerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime convertedAt;
    
    private String description;
    
    private String remark;
    
    private List<Map<String, Object>> attachments;
    
    private Map<String, Object> customFields;
    
    private UUID createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private UUID updatedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 关联信息
    private String ownerName;
    private String convertedCustomerName;
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
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
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPosition() {
        return contactPosition;
    }
    
    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceDetail() {
        return sourceDetail;
    }
    
    public void setSourceDetail(String sourceDetail) {
        this.sourceDetail = sourceDetail;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }
    
    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
    
    public LocalDateTime getEstimatedCloseDate() {
        return estimatedCloseDate;
    }
    
    public void setEstimatedCloseDate(LocalDateTime estimatedCloseDate) {
        this.estimatedCloseDate = estimatedCloseDate;
    }
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public LocalDateTime getLastContactTime() {
        return lastContactTime;
    }
    
    public void setLastContactTime(LocalDateTime lastContactTime) {
        this.lastContactTime = lastContactTime;
    }
    
    public LocalDateTime getNextFollowTime() {
        return nextFollowTime;
    }
    
    public void setNextFollowTime(LocalDateTime nextFollowTime) {
        this.nextFollowTime = nextFollowTime;
    }
    
    public UUID getConvertedCustomerId() {
        return convertedCustomerId;
    }
    
    public void setConvertedCustomerId(UUID convertedCustomerId) {
        this.convertedCustomerId = convertedCustomerId;
    }
    
    public LocalDateTime getConvertedAt() {
        return convertedAt;
    }
    
    public void setConvertedAt(LocalDateTime convertedAt) {
        this.convertedAt = convertedAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getConvertedCustomerName() {
        return convertedCustomerName;
    }
    
    public void setConvertedCustomerName(String convertedCustomerName) {
        this.convertedCustomerName = convertedCustomerName;
    }
}
