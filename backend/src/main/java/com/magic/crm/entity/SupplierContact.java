package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 供应商联系人实体
 */
@Entity
@Table(name = "supplier_contacts")
@Where(clause = "deleted = false")
public class SupplierContact {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "supplier_id", columnDefinition = "uuid", nullable = false)
    private UUID supplierId; // 所属供应商
    
    @Column(name = "name", length = 100, nullable = false)
    private String name; // 姓名
    
    @Column(name = "title", length = 100)
    private String title; // 职位
    
    @Column(name = "department", length = 100)
    private String department; // 部门
    
    @Column(name = "mobile", length = 20)
    private String mobile; // 手机
    
    @Column(name = "phone", length = 20)
    private String phone; // 座机
    
    @Column(name = "email", length = 100)
    private String email; // 邮箱
    
    @Column(name = "im", length = 100)
    private String im; // 即时通讯
    
    @Column(name = "decision_role", length = 50)
    private String decisionRole; // 决策角色
    
    @Column(name = "birthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday; // 生日
    
    @Column(name = "preference", columnDefinition = "TEXT")
    private String preference; // 偏好
    
    @Column(name = "privacy_consent")
    private Boolean privacyConsent; // 隐私同意
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false; // 是否主要联系人
    
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
        if (isPrimary == null) {
            isPrimary = false;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getIm() {
        return im;
    }
    
    public void setIm(String im) {
        this.im = im;
    }
    
    public String getDecisionRole() {
        return decisionRole;
    }
    
    public void setDecisionRole(String decisionRole) {
        this.decisionRole = decisionRole;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public String getPreference() {
        return preference;
    }
    
    public void setPreference(String preference) {
        this.preference = preference;
    }
    
    public Boolean getPrivacyConsent() {
        return privacyConsent;
    }
    
    public void setPrivacyConsent(Boolean privacyConsent) {
        this.privacyConsent = privacyConsent;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
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
