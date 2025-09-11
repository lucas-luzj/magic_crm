package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 供应商搜索条件DTO
 */
public class SupplierSearchDTO {
    
    private String name; // 供应商名称
    
    private String code; // 供应商编码
    
    private String shortName; // 简称
    
    private String uscc; // 统一社会信用代码
    
    private String industry; // 行业
    
    private String region; // 地区
    
    private String supplierLevel; // 供应商等级
    
    private String supplierType; // 供应商类型
    
    private String source; // 来源
    
    private String status; // 状态
    
    private Boolean isBlacklist; // 是否黑名单
    
    private Boolean isKeySupplier; // 是否重点供应商
    
    private String keyword; // 关键词搜索
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStartTime; // 创建开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEndTime; // 创建结束时间
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractStartDate; // 合同开始日期
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractEndDate; // 合同结束日期
    
    private Integer minRating; // 最小评级
    
    private Integer maxRating; // 最大评级
    
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
    
    public String getSupplierLevel() {
        return supplierLevel;
    }
    
    public void setSupplierLevel(String supplierLevel) {
        this.supplierLevel = supplierLevel;
    }
    
    public String getSupplierType() {
        return supplierType;
    }
    
    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
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
    
    public Boolean getIsBlacklist() {
        return isBlacklist;
    }
    
    public void setIsBlacklist(Boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
    }
    
    public Boolean getIsKeySupplier() {
        return isKeySupplier;
    }
    
    public void setIsKeySupplier(Boolean isKeySupplier) {
        this.isKeySupplier = isKeySupplier;
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
    
    public LocalDateTime getContractStartDate() {
        return contractStartDate;
    }
    
    public void setContractStartDate(LocalDateTime contractStartDate) {
        this.contractStartDate = contractStartDate;
    }
    
    public LocalDateTime getContractEndDate() {
        return contractEndDate;
    }
    
    public void setContractEndDate(LocalDateTime contractEndDate) {
        this.contractEndDate = contractEndDate;
    }
    
    public Integer getMinRating() {
        return minRating;
    }
    
    public void setMinRating(Integer minRating) {
        this.minRating = minRating;
    }
    
    public Integer getMaxRating() {
        return maxRating;
    }
    
    public void setMaxRating(Integer maxRating) {
        this.maxRating = maxRating;
    }
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
}
