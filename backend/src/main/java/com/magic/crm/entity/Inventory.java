package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 库存实体
 */
@Entity
@Table(name = "inventory")
@Where(clause = "deleted = false")
public class Inventory {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "product_id", columnDefinition = "uuid", nullable = false)
    private UUID productId; // 产品ID
    
    @Column(name = "warehouse_code", length = 50)
    private String warehouseCode = "MAIN"; // 仓库编码
    
    @Column(name = "available_quantity")
    private Integer availableQuantity = 0; // 可用库存
    
    @Column(name = "in_transit_quantity")
    private Integer inTransitQuantity = 0; // 在途库存
    
    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0; // 预留库存
    
    @Column(name = "total_quantity")
    private Integer totalQuantity = 0; // 总库存
    
    @Column(name = "min_stock_level")
    private Integer minStockLevel; // 最小库存
    
    @Column(name = "max_stock_level")
    private Integer maxStockLevel; // 最大库存
    
    @Column(name = "reorder_point")
    private Integer reorderPoint; // 补货点
    
    @Column(name = "last_inbound_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastInboundDate; // 最后入库时间
    
    @Column(name = "last_outbound_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOutboundDate; // 最后出库时间
    
    @Column(name = "status", length = 20)
    private String status = "NORMAL"; // 状态: NORMAL, LOW, OUT_OF_STOCK
    
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
        if (availableQuantity == null) {
            availableQuantity = 0;
        }
        if (inTransitQuantity == null) {
            inTransitQuantity = 0;
        }
        if (reservedQuantity == null) {
            reservedQuantity = 0;
        }
        if (totalQuantity == null) {
            totalQuantity = 0;
        }
        if (warehouseCode == null) {
            warehouseCode = "MAIN";
        }
        if (status == null) {
            status = "NORMAL";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // 更新总库存
        if (availableQuantity != null && inTransitQuantity != null && reservedQuantity != null) {
            totalQuantity = availableQuantity + inTransitQuantity + reservedQuantity;
        }
        // 更新库存状态
        updateStockStatus();
    }
    
    private void updateStockStatus() {
        if (availableQuantity == null || minStockLevel == null) {
            return;
        }
        
        if (availableQuantity <= 0) {
            status = "OUT_OF_STOCK";
        } else if (minStockLevel != null && availableQuantity <= minStockLevel) {
            status = "LOW";
        } else {
            status = "NORMAL";
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getWarehouseCode() {
        return warehouseCode;
    }
    
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    
    public Integer getInTransitQuantity() {
        return inTransitQuantity;
    }
    
    public void setInTransitQuantity(Integer inTransitQuantity) {
        this.inTransitQuantity = inTransitQuantity;
    }
    
    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
    
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public Integer getMinStockLevel() {
        return minStockLevel;
    }
    
    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }
    
    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }
    
    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }
    
    public Integer getReorderPoint() {
        return reorderPoint;
    }
    
    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }
    
    public LocalDateTime getLastInboundDate() {
        return lastInboundDate;
    }
    
    public void setLastInboundDate(LocalDateTime lastInboundDate) {
        this.lastInboundDate = lastInboundDate;
    }
    
    public LocalDateTime getLastOutboundDate() {
        return lastOutboundDate;
    }
    
    public void setLastOutboundDate(LocalDateTime lastOutboundDate) {
        this.lastOutboundDate = lastOutboundDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}
