package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 供应商数据传输对象
 */
public class SupplierDTO {
    
    private UUID id;
    
    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 200, message = "供应商名称长度不能超过200个字符")
    private String name;
    
    private String code;
    
    @Size(max = 100, message = "简称长度不能超过100个字符")
    private String shortName;
    
    @Size(max = 50, message = "统一社会信用代码长度不能超过50个字符")
    private String uscc;
    
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
    
    @Size(max = 50, message = "供应商等级长度不能超过50个字符")
    private String supplierLevel;
    
    @Size(max = 50, message = "供应商类型长度不能超过50个字符")
    private String supplierType;
    
    @Size(max = 50, message = "来源长度不能超过50个字符")
    private String source;
    
    private String status = "ACTIVE";
    
    private Boolean isBlacklist = false;
    
    private Boolean isKeySupplier = false;
    
    @Size(max = 200, message = "付款条件长度不能超过200个字符")
    private String paymentTerms;
    
    @Size(max = 200, message = "交货条件长度不能超过200个字符")
    private String deliveryTerms;
    
    private Integer qualityRating;
    
    private Integer serviceRating;
    
    private Integer priceRating;
    
    private Integer overallRating;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastEvaluationDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractStartDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractEndDate;
    
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
    private List<SupplierContactDTO> contacts;
    private List<SupplierProductDTO> products;
    
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
    
    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
    
    public String getDeliveryTerms() {
        return deliveryTerms;
    }
    
    public void setDeliveryTerms(String deliveryTerms) {
        this.deliveryTerms = deliveryTerms;
    }
    
    public Integer getQualityRating() {
        return qualityRating;
    }
    
    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }
    
    public Integer getServiceRating() {
        return serviceRating;
    }
    
    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }
    
    public Integer getPriceRating() {
        return priceRating;
    }
    
    public void setPriceRating(Integer priceRating) {
        this.priceRating = priceRating;
    }
    
    public Integer getOverallRating() {
        return overallRating;
    }
    
    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }
    
    public LocalDateTime getLastEvaluationDate() {
        return lastEvaluationDate;
    }
    
    public void setLastEvaluationDate(LocalDateTime lastEvaluationDate) {
        this.lastEvaluationDate = lastEvaluationDate;
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
    
    public List<SupplierContactDTO> getContacts() {
        return contacts;
    }
    
    public void setContacts(List<SupplierContactDTO> contacts) {
        this.contacts = contacts;
    }
    
    public List<SupplierProductDTO> getProducts() {
        return products;
    }
    
    public void setProducts(List<SupplierProductDTO> products) {
        this.products = products;
    }
}
