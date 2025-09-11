package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 供应商实体
 */
@Entity
@Table(name = "suppliers")
@Where(clause = "deleted = false")
public class Supplier {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "code", length = 50, unique = true, nullable = false)
    private String code; // 供应商编码
    
    @Column(name = "name", length = 200, nullable = false)
    private String name; // 供应商名称
    
    @Column(name = "short_name", length = 100)
    private String shortName; // 简称
    
    @Column(name = "uscc", length = 50)
    private String uscc; // 统一社会信用代码
    
    @Column(name = "industry", length = 100)
    private String industry; // 行业
    
    @Column(name = "region", length = 100)
    private String region; // 地区
    
    @Column(name = "address", length = 500)
    private String address; // 地址
    
    @Column(name = "website", length = 200)
    private String website; // 官网
    
    @Column(name = "company_size", length = 50)
    private String companySize; // 公司规模
    
    @Column(name = "supplier_level", length = 50)
    private String supplierLevel; // 供应商等级
    
    @Column(name = "supplier_type", length = 50)
    private String supplierType; // 供应商类型
    
    @Column(name = "source", length = 50)
    private String source; // 来源
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // 状态: ACTIVE, INACTIVE, SUSPENDED
    
    @Column(name = "is_blacklist")
    private Boolean isBlacklist = false; // 是否黑名单
    
    @Column(name = "is_key_supplier")
    private Boolean isKeySupplier = false; // 是否重点供应商
    
    @Column(name = "payment_terms", length = 200)
    private String paymentTerms; // 付款条件
    
    @Column(name = "delivery_terms", length = 200)
    private String deliveryTerms; // 交货条件
    
    @Column(name = "quality_rating")
    private Integer qualityRating; // 质量评级 (1-5)
    
    @Column(name = "service_rating")
    private Integer serviceRating; // 服务评级 (1-5)
    
    @Column(name = "price_rating")
    private Integer priceRating; // 价格评级 (1-5)
    
    @Column(name = "overall_rating")
    private Integer overallRating; // 综合评级 (1-5)
    
    @Column(name = "last_evaluation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastEvaluationDate; // 最后评价日期
    
    @Column(name = "contract_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractStartDate; // 合同开始日期
    
    @Column(name = "contract_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractEndDate; // 合同结束日期
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark; // 备注
    
    @Type(JsonBinaryType.class)
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<Map<String, Object>> attachments; // 附件
    
    @Type(JsonBinaryType.class)
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
    
    // 关联关系将在后续实现
    // @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<SupplierContact> contacts;
    
    // @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<SupplierProduct> products;
    
    // @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<SupplierEvaluation> evaluations;
    
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
        if (isBlacklist == null) {
            isBlacklist = false;
        }
        if (isKeySupplier == null) {
            isKeySupplier = false;
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
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    // 关联关系的getter和setter将在后续实现
    // public List<SupplierContact> getContacts() {
    //     return contacts;
    // }
    
    // public void setContacts(List<SupplierContact> contacts) {
    //     this.contacts = contacts;
    // }
    
    // public List<SupplierProduct> getProducts() {
    //     return products;
    // }
    
    // public void setProducts(List<SupplierProduct> products) {
    //     this.products = products;
    // }
    
    // public List<SupplierEvaluation> getEvaluations() {
    //     return evaluations;
    // }
    
    // public void setEvaluations(List<SupplierEvaluation> evaluations) {
    //     this.evaluations = evaluations;
    // }
}
