package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 线索搜索条件DTO
 */
public class LeadSearchDTO {
    
    private String name; // 线索名称
    
    private String code; // 线索编号
    
    private String companyName; // 公司名称
    
    private String contactName; // 联系人姓名
    
    private String contactPhone; // 联系人电话
    
    private String contactEmail; // 联系人邮箱
    
    private String industry; // 行业
    
    private String region; // 地区
    
    private String source; // 来源
    
    private String status; // 状态
    
    private String priority; // 优先级
    
    private UUID ownerId; // 所属销售
    
    private String keyword; // 关键词搜索
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStartTime; // 创建开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEndTime; // 创建结束时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastContactStartTime; // 最后联系开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastContactEndTime; // 最后联系结束时间
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime estimatedCloseStartDate; // 预估成交开始日期
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime estimatedCloseEndDate; // 预估成交结束日期
    
    private Integer minScore; // 最小评分
    
    private Integer maxScore; // 最大评分
    
    private UUID createdBy; // 创建人
    
    // Getters and Setters
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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
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
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public LocalDateTime getCreatedStartTime() {
        return createdStartTime;
    }
    
    public void setCreatedStartTime(LocalDateTime createdStartTime) {
        this.createdStartTime = createdStartTime;
    }
    
    public LocalDateTime getCreatedEndTime() {
        return createdEndTime;
    }
    
    public void setCreatedEndTime(LocalDateTime createdEndTime) {
        this.createdEndTime = createdEndTime;
    }
    
    public LocalDateTime getLastContactStartTime() {
        return lastContactStartTime;
    }
    
    public void setLastContactStartTime(LocalDateTime lastContactStartTime) {
        this.lastContactStartTime = lastContactStartTime;
    }
    
    public LocalDateTime getLastContactEndTime() {
        return lastContactEndTime;
    }
    
    public void setLastContactEndTime(LocalDateTime lastContactEndTime) {
        this.lastContactEndTime = lastContactEndTime;
    }
    
    public LocalDateTime getEstimatedCloseStartDate() {
        return estimatedCloseStartDate;
    }
    
    public void setEstimatedCloseStartDate(LocalDateTime estimatedCloseStartDate) {
        this.estimatedCloseStartDate = estimatedCloseStartDate;
    }
    
    public LocalDateTime getEstimatedCloseEndDate() {
        return estimatedCloseEndDate;
    }
    
    public void setEstimatedCloseEndDate(LocalDateTime estimatedCloseEndDate) {
        this.estimatedCloseEndDate = estimatedCloseEndDate;
    }
    
    public Integer getMinScore() {
        return minScore;
    }
    
    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }
    
    public Integer getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
}
