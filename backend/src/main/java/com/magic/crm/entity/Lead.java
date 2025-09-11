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
 * 线索实体
 */
@Entity
@Table(name = "leads")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Lead {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "code", length = 50, unique = true, nullable = false)
    private String code; // 线索编号
    
    @Column(name = "name", length = 200, nullable = false)
    private String name; // 线索名称
    
    @Column(name = "company_name", length = 200)
    private String companyName; // 公司名称
    
    @Column(name = "contact_name", length = 100)
    private String contactName; // 联系人姓名
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone; // 联系人电话
    
    @Column(name = "contact_email", length = 100)
    private String contactEmail; // 联系人邮箱
    
    @Column(name = "contact_position", length = 100)
    private String contactPosition; // 联系人职位
    
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
    
    @Column(name = "source", length = 50)
    private String source; // 来源
    
    @Column(name = "source_detail", length = 200)
    private String sourceDetail; // 来源详情
    
    @Column(name = "status", length = 20)
    private String status = "NEW"; // 状态: NEW, CONTACTED, QUALIFIED, UNQUALIFIED, CONVERTED
    
    @Column(name = "priority", length = 20)
    private String priority = "MEDIUM"; // 优先级: LOW, MEDIUM, HIGH, URGENT
    
    @Column(name = "score")
    private Integer score = 0; // 线索评分
    
    @Column(name = "estimated_value", precision = 18, scale = 2)
    private BigDecimal estimatedValue; // 预估价值
    
    @Column(name = "estimated_close_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime estimatedCloseDate; // 预估成交日期
    
    @Column(name = "owner_id", columnDefinition = "uuid")
    private UUID ownerId; // 所属销售
    
    @Column(name = "assigned_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedAt; // 分配时间
    
    @Column(name = "last_contact_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastContactTime; // 最后联系时间
    
    @Column(name = "next_follow_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime; // 下次跟进时间
    
    @Column(name = "converted_customer_id", columnDefinition = "uuid")
    private UUID convertedCustomerId; // 转换的客户ID
    
    @Column(name = "converted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime convertedAt; // 转换时间
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 描述
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark; // 备注
    
    @Type(type = "jsonb")
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<Map<String, Object>> attachments; // 附件
    
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
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_customer_id", insertable = false, updatable = false)
    private Customer convertedCustomer;
    
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeadFollowUp> followUps;
    
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
        if (score == null) {
            score = 0;
        }
        if (status == null) {
            status = "NEW";
        }
        if (priority == null) {
            priority = "MEDIUM";
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
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPosition() {
        return contactPosition;
    }
    
    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceDetail() {
        return sourceDetail;
    }
    
    public void setSourceDetail(String sourceDetail) {
        this.sourceDetail = sourceDetail;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }
    
    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
    
    public LocalDateTime getEstimatedCloseDate() {
        return estimatedCloseDate;
    }
    
    public void setEstimatedCloseDate(LocalDateTime estimatedCloseDate) {
        this.estimatedCloseDate = estimatedCloseDate;
    }
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public LocalDateTime getLastContactTime() {
        return lastContactTime;
    }
    
    public void setLastContactTime(LocalDateTime lastContactTime) {
        this.lastContactTime = lastContactTime;
    }
    
    public LocalDateTime getNextFollowTime() {
        return nextFollowTime;
    }
    
    public void setNextFollowTime(LocalDateTime nextFollowTime) {
        this.nextFollowTime = nextFollowTime;
    }
    
    public UUID getConvertedCustomerId() {
        return convertedCustomerId;
    }
    
    public void setConvertedCustomerId(UUID convertedCustomerId) {
        this.convertedCustomerId = convertedCustomerId;
    }
    
    public LocalDateTime getConvertedAt() {
        return convertedAt;
    }
    
    public void setConvertedAt(LocalDateTime convertedAt) {
        this.convertedAt = convertedAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public Customer getConvertedCustomer() {
        return convertedCustomer;
    }
    
    public void setConvertedCustomer(Customer convertedCustomer) {
        this.convertedCustomer = convertedCustomer;
    }
    
    public List<LeadFollowUp> getFollowUps() {
        return followUps;
    }
    
    public void setFollowUps(List<LeadFollowUp> followUps) {
        this.followUps = followUps;
    }
}
