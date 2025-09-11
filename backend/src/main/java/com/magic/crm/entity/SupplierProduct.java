package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 供应商产品关联实体
 */
@Entity
@Table(name = "supplier_products")
@Where(clause = "deleted = false")
public class SupplierProduct {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "supplier_id", columnDefinition = "uuid", nullable = false)
    private UUID supplierId; // 供应商ID
    
    @Column(name = "product_id", columnDefinition = "uuid", nullable = false)
    private UUID productId; // 产品ID
    
    @Column(name = "supplier_product_code", length = 100)
    private String supplierProductCode; // 供应商产品编码
    
    @Column(name = "supplier_product_name", length = 200)
    private String supplierProductName; // 供应商产品名称
    
    @Column(name = "price", precision = 18, scale = 2)
    private BigDecimal price; // 供货价格
    
    @Column(name = "currency", length = 10)
    private String currency = "CNY"; // 币种
    
    @Column(name = "lead_time")
    private Integer leadTime; // 交付周期(天)
    
    @Column(name = "min_order_quantity")
    private Integer minOrderQuantity; // 最小起订量
    
    @Column(name = "packaging_unit", length = 50)
    private String packagingUnit; // 包装单位
    
    @Column(name = "is_primary_supplier")
    private Boolean isPrimarySupplier = false; // 是否主供应商
    
    @Column(name = "priority")
    private Integer priority = 0; // 优先级
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // 状态
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark; // 备注
    
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
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private Supplier supplier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
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
        if (isPrimarySupplier == null) {
            isPrimarySupplier = false;
        }
        if (priority == null) {
            priority = 0;
        }
        if (currency == null) {
            currency = "CNY";
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
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}
