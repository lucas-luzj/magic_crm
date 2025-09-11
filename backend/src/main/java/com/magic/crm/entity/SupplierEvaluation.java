package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 供应商评价实体
 */
@Entity
@Table(name = "supplier_evaluations")
@Where(clause = "deleted = false")
public class SupplierEvaluation {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "supplier_id", columnDefinition = "uuid", nullable = false)
    private UUID supplierId; // 供应商ID
    
    @Column(name = "evaluation_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime evaluationDate; // 评价日期
    
    @Column(name = "evaluator_id", columnDefinition = "uuid", nullable = false)
    private UUID evaluatorId; // 评价人ID
    
    @Column(name = "quality_rating", nullable = false)
    private Integer qualityRating; // 质量评级 (1-5)
    
    @Column(name = "service_rating", nullable = false)
    private Integer serviceRating; // 服务评级 (1-5)
    
    @Column(name = "price_rating", nullable = false)
    private Integer priceRating; // 价格评级 (1-5)
    
    @Column(name = "delivery_rating", nullable = false)
    private Integer deliveryRating; // 交付评级 (1-5)
    
    @Column(name = "communication_rating", nullable = false)
    private Integer communicationRating; // 沟通评级 (1-5)
    
    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating; // 综合评级 (1-5)
    
    @Column(name = "evaluation_period", length = 50)
    private String evaluationPeriod; // 评价期间
    
    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths; // 优势
    
    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses; // 劣势
    
    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions; // 建议
    
    @Column(name = "is_approved")
    private Boolean isApproved = false; // 是否已审批
    
    @Column(name = "approved_by", columnDefinition = "uuid")
    private UUID approvedBy; // 审批人ID
    
    @Column(name = "approved_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt; // 审批时间
    
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
        if (isApproved == null) {
            isApproved = false;
        }
        if (evaluationDate == null) {
            evaluationDate = LocalDateTime.now();
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
    
    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }
    
    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }
    
    public UUID getEvaluatorId() {
        return evaluatorId;
    }
    
    public void setEvaluatorId(UUID evaluatorId) {
        this.evaluatorId = evaluatorId;
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
    
    public Integer getDeliveryRating() {
        return deliveryRating;
    }
    
    public void setDeliveryRating(Integer deliveryRating) {
        this.deliveryRating = deliveryRating;
    }
    
    public Integer getCommunicationRating() {
        return communicationRating;
    }
    
    public void setCommunicationRating(Integer communicationRating) {
        this.communicationRating = communicationRating;
    }
    
    public Integer getOverallRating() {
        return overallRating;
    }
    
    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }
    
    public String getEvaluationPeriod() {
        return evaluationPeriod;
    }
    
    public void setEvaluationPeriod(String evaluationPeriod) {
        this.evaluationPeriod = evaluationPeriod;
    }
    
    public String getStrengths() {
        return strengths;
    }
    
    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }
    
    public String getWeaknesses() {
        return weaknesses;
    }
    
    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }
    
    public String getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }
    
    public Boolean getIsApproved() {
        return isApproved;
    }
    
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
    
    public UUID getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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
}
