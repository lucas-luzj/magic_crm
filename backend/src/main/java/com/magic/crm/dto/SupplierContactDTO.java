package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 供应商联系人数据传输对象
 */
public class SupplierContactDTO {
    
    private UUID id;
    
    private UUID supplierId;
    
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 100, message = "联系人姓名长度不能超过100个字符")
    private String name;
    
    @Size(max = 100, message = "职位长度不能超过100个字符")
    private String title;
    
    @Size(max = 100, message = "部门长度不能超过100个字符")
    private String department;
    
    @Size(max = 20, message = "手机长度不能超过20个字符")
    private String mobile;
    
    @Size(max = 20, message = "座机长度不能超过20个字符")
    private String phone;
    
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Size(max = 100, message = "即时通讯长度不能超过100个字符")
    private String im;
    
    @Size(max = 50, message = "决策角色长度不能超过50个字符")
    private String decisionRole;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    
    private String preference;
    
    private Boolean privacyConsent;
    
    private Boolean isPrimary = false;
    
    private String remark;
    
    private UUID createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private UUID updatedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
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
}
