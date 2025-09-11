package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 供应商产品数据传输对象
 */
public class SupplierProductDTO {
    
    private UUID id;
    
    private UUID supplierId;
    
    private UUID productId;
    
    @Size(max = 100, message = "供应商产品编码长度不能超过100个字符")
    private String supplierProductCode;
    
    @Size(max = 200, message = "供应商产品名称长度不能超过200个字符")
    private String supplierProductName;
    
    private BigDecimal price;
    
    @Size(max = 10, message = "币种长度不能超过10个字符")
    private String currency = "CNY";
    
    private Integer leadTime;
    
    private Integer minOrderQuantity;
    
    @Size(max = 50, message = "包装单位长度不能超过50个字符")
    private String packagingUnit;
    
    private Boolean isPrimarySupplier = false;
    
    private Integer priority = 0;
    
    private String status = "ACTIVE";
    
    private String remark;
    
    private UUID createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private UUID updatedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 关联信息
    private String productName;
    private String productCode;
    private String supplierName;
    private String supplierCode;
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getSupplierProductCode() {
        return supplierProductCode;
    }
    
    public void setSupplierProductCode(String supplierProductCode) {
        this.supplierProductCode = supplierProductCode;
    }
    
    public String getSupplierProductName() {
        return supplierProductName;
    }
    
    public void setSupplierProductName(String supplierProductName) {
        this.supplierProductName = supplierProductName;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Integer getLeadTime() {
        return leadTime;
    }
    
    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }
    
    public Integer getMinOrderQuantity() {
        return minOrderQuantity;
    }
    
    public void setMinOrderQuantity(Integer minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }
    
    public String getPackagingUnit() {
        return packagingUnit;
    }
    
    public void setPackagingUnit(String packagingUnit) {
        this.packagingUnit = packagingUnit;
    }
    
    public Boolean getIsPrimarySupplier() {
        return isPrimarySupplier;
    }
    
    public void setIsPrimarySupplier(Boolean isPrimarySupplier) {
        this.isPrimarySupplier = isPrimarySupplier;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
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
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getSupplierCode() {
        return supplierCode;
    }
    
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
}
