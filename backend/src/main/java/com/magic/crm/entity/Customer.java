package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 客户实体
 */
@Entity
@Table(name = "customers")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Customer {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "code", length = 50, unique = true, nullable = false)
    private String code; // 客户编号
    
    @Column(name = "name", length = 200, nullable = false)
    private String name; // 客户名称
    
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
    
    @Column(name = "customer_level", length = 50)
    private String customerLevel; // 客户等级
    
    @Column(name = "customer_type", length = 50)
    private String customerType; // 客户类型
    
    @Column(name = "source", length = 50)
    private String source; // 来源
    
    @Column(name = "owner_id", columnDefinition = "uuid")
    private UUID ownerId; // 所属销售
    
    @Type(type = "jsonb")
    @Column(name = "collaborator_ids", columnDefinition = "jsonb")
    private List<UUID> collaboratorIds; // 协作人
    
    @Column(name = "parent_customer_id", columnDefinition = "uuid")
    private UUID parentCustomerId; // 上级客户
    
    @Column(name = "is_key_customer")
    private Boolean isKeyCustomer = false; // 是否重点客户
    
    @Column(name = "is_blacklist")
    private Boolean isBlacklist = false; // 是否黑名单
    
    @Column(name = "is_public_pool")
    private Boolean isPublicPool = false; // 是否在公海池
    
    @Column(name = "pool_entry_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime poolEntryTime; // 进入公海时间
    
    @Column(name = "pool_entry_reason", length = 500)
    private String poolEntryReason; // 进入公海原因
    
    @Column(name = "last_follow_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFollowTime; // 最后跟进时间
    
    @Column(name = "last_order_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOrderTime; // 最后成单时间
    
    @Column(name = "org_unit_id", columnDefinition = "uuid")
    private UUID orgUnitId; // 所属组织
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // 状态
    
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
    @JoinColumn(name = "parent_customer_id", insertable = false, updatable = false)
    private Customer parentCustomer;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contact> contacts;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Activity> activities;
    
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
        if (isKeyCustomer == null) {
            isKeyCustomer = false;
        }
        if (isBlacklist == null) {
            isBlacklist = false;
        }
        if (isPublicPool == null) {
            isPublicPool = false;
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
    
    public String getCustomerLevel() {
        return customerLevel;
    }
    
    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    
    public List<UUID> getCollaboratorIds() {
        return collaboratorIds;
    }
    
    public void setCollaboratorIds(List<UUID> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }
    
    public UUID getParentCustomerId() {
        return parentCustomerId;
    }
    
    public void setParentCustomerId(UUID parentCustomerId) {
        this.parentCustomerId = parentCustomerId;
    }
    
    public Boolean getIsKeyCustomer() {
        return isKeyCustomer;
    }
    
    public void setIsKeyCustomer(Boolean isKeyCustomer) {
        this.isKeyCustomer = isKeyCustomer;
    }
    
    public Boolean getIsBlacklist() {
        return isBlacklist;
    }
    
    public void setIsBlacklist(Boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
    }
    
    public Boolean getIsPublicPool() {
        return isPublicPool;
    }
    
    public void setIsPublicPool(Boolean isPublicPool) {
        this.isPublicPool = isPublicPool;
    }
    
    public LocalDateTime getPoolEntryTime() {
        return poolEntryTime;
    }
    
    public void setPoolEntryTime(LocalDateTime poolEntryTime) {
        this.poolEntryTime = poolEntryTime;
    }
    
    public String getPoolEntryReason() {
        return poolEntryReason;
    }
    
    public void setPoolEntryReason(String poolEntryReason) {
        this.poolEntryReason = poolEntryReason;
    }
    
    public LocalDateTime getLastFollowTime() {
        return lastFollowTime;
    }
    
    public void setLastFollowTime(LocalDateTime lastFollowTime) {
        this.lastFollowTime = lastFollowTime;
    }
    
    public LocalDateTime getLastOrderTime() {
        return lastOrderTime;
    }
    
    public void setLastOrderTime(LocalDateTime lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }
    
    public UUID getOrgUnitId() {
        return orgUnitId;
    }
    
    public void setOrgUnitId(UUID orgUnitId) {
        this.orgUnitId = orgUnitId;
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
    
    public Customer getParentCustomer() {
        return parentCustomer;
    }
    
    public void setParentCustomer(Customer parentCustomer) {
        this.parentCustomer = parentCustomer;
    }
    
    public List<Contact> getContacts() {
        return contacts;
    }
    
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
    
    public List<Activity> getActivities() {
        return activities;
    }
    
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}