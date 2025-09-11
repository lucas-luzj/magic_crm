package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 产品实体
 */
@Entity
@Table(name = "products")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Product {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "code", length = 50, unique = true, nullable = false)
    private String code; // 产品编码
    
    @Column(name = "name", length = 200, nullable = false)
    private String name; // 产品名称
    
    @Column(name = "category_id", columnDefinition = "uuid")
    private UUID categoryId; // 产品分类
    
    @Column(name = "brand", length = 100)
    private String brand; // 品牌
    
    @Column(name = "model", length = 100)
    private String model; // 型号
    
    @Column(name = "spec", columnDefinition = "TEXT")
    private String spec; // 规格参数
    
    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure; // 计量单位
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate; // 税率
    
    @Column(name = "standard_price", precision = 18, scale = 2)
    private BigDecimal standardPrice; // 标准价格
    
    @Column(name = "cost_price", precision = 18, scale = 2)
    private BigDecimal costPrice; // 成本价格
    
    @Column(name = "min_price", precision = 18, scale = 2)
    private BigDecimal minPrice; // 最低价格
    
    @Column(name = "is_spare_part")
    private Boolean isSparePart = false; // 是否备件
    
    @Column(name = "is_bundle")
    private Boolean isBundle = false; // 是否套餐
    
    @Column(name = "is_customizable")
    private Boolean isCustomizable = false; // 是否可定制
    
    @Column(name = "lead_time")
    private Integer leadTime; // 交付周期(天)
    
    @Column(name = "warranty_period")
    private Integer warrantyPeriod; // 质保期(月)
    
    @Type(type = "jsonb")
    @Column(name = "images", columnDefinition = "jsonb")
    private List<Map<String, Object>> images; // 产品图片
    
    @Type(type = "jsonb")
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<Map<String, Object>> attachments; // 附件
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 产品描述
    
    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // 产品特性
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // 状态: ACTIVE, INACTIVE, DISCONTINUED
    
    @Type(type = "jsonb")
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields; // 自定义字段
    
    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "deleted")
    @JsonIgnore
    private Boolean deleted = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private ProductCategory category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PricePolicy> pricePolicies;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (deleted == null) {
            deleted = false;
        }
        if (isSparePart == null) {
            isSparePart = false;
        }
        if (isBundle == null) {
            isBundle = false;
        }
        if (isCustomizable == null) {
            isCustomizable = false;
        }
        if (status == null) {
            status = "ACTIVE";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
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
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public List<Inventory> getInventories() {
        return inventories;
    }
    
    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }
    
    public List<PricePolicy> getPricePolicies() {
        return pricePolicies;
    }
    
    public void setPricePolicies(List<PricePolicy> pricePolicies) {
        this.pricePolicies = pricePolicies;
    }
}
