package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 产品搜索条件DTO
 */
public class ProductSearchDTO {
    
    private String name; // 产品名称
    
    private String code; // 产品编码
    
    private String brand; // 品牌
    
    private String model; // 型号
    
    private UUID categoryId; // 产品分类
    
    private String categoryName; // 分类名称
    
    private String status; // 状态
    
    private Boolean isSparePart; // 是否备件
    
    private Boolean isBundle; // 是否套餐
    
    private Boolean isCustomizable; // 是否可定制
    
    private String unitOfMeasure; // 计量单位
    
    private String keyword; // 关键词搜索
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStartTime; // 创建开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEndTime; // 创建结束时间
    
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
    
    public UUID getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }
    
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
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
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
}
