package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 产品数据传输对象
 */
public class ProductDTO {
    
    private UUID id;
    
    @NotBlank(message = "产品编码不能为空")
    @Size(max = 50, message = "产品编码长度不能超过50个字符")
    private String code;
    
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200个字符")
    private String name;
    
    private UUID categoryId;
    
    @Size(max = 100, message = "品牌长度不能超过100个字符")
    private String brand;
    
    @Size(max = 100, message = "型号长度不能超过100个字符")
    private String model;
    
    private String spec;
    
    @Size(max = 20, message = "计量单位长度不能超过20个字符")
    private String unitOfMeasure;
    
    @PositiveOrZero(message = "税率不能为负数")
    private BigDecimal taxRate;
    
    @PositiveOrZero(message = "标准价格不能为负数")
    private BigDecimal standardPrice;
    
    @PositiveOrZero(message = "成本价格不能为负数")
    private BigDecimal costPrice;
    
    @PositiveOrZero(message = "最低价格不能为负数")
    private BigDecimal minPrice;
    
    private Boolean isSparePart = false;
    
    private Boolean isBundle = false;
    
    private Boolean isCustomizable = false;
    
    @PositiveOrZero(message = "交付周期不能为负数")
    private Integer leadTime;
    
    @PositiveOrZero(message = "质保期不能为负数")
    private Integer warrantyPeriod;
    
    private List<Map<String, Object>> images;
    
    private List<Map<String, Object>> attachments;
    
    private String description;
    
    private String features;
    
    private String status = "ACTIVE";
    
    private Map<String, Object> customFields;
    
    private UUID createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private UUID updatedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 关联信息
    private String categoryName;
    private String categoryCode;
    
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
    
    public UUID getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getSpec() {
        return spec;
    }
    
    public void setSpec(String spec) {
        this.spec = spec;
    }
    
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }
    
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
    
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
    
    public BigDecimal getStandardPrice() {
        return standardPrice;
    }
    
    public void setStandardPrice(BigDecimal standardPrice) {
        this.standardPrice = standardPrice;
    }
    
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    
    public BigDecimal getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }
    
    public Boolean getIsSparePart() {
        return isSparePart;
    }
    
    public void setIsSparePart(Boolean isSparePart) {
        this.isSparePart = isSparePart;
    }
    
    public Boolean getIsBundle() {
        return isBundle;
    }
    
    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }
    
    public Boolean getIsCustomizable() {
        return isCustomizable;
    }
    
    public void setIsCustomizable(Boolean isCustomizable) {
        this.isCustomizable = isCustomizable;
    }
    
    public Integer getLeadTime() {
        return leadTime;
    }
    
    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }
    
    public Integer getWarrantyPeriod() {
        return warrantyPeriod;
    }
    
    public void setWarrantyPeriod(Integer warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }
    
    public List<Map<String, Object>> getImages() {
        return images;
    }
    
    public void setImages(List<Map<String, Object>> images) {
        this.images = images;
    }
    
    public List<Map<String, Object>> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Map<String, Object>> attachments) {
        this.attachments = attachments;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFeatures() {
        return features;
    }
    
    public void setFeatures(String features) {
        this.features = features;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
