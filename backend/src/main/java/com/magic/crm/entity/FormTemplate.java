package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单模板实体
 */
@Entity
@Table(name = "form_template")
public class FormTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_key", unique = true, nullable = false, length = 100)
    private String formKey;

    @Column(name = "form_name", nullable = false, length = 200)
    private String formName;

    @Column(name = "form_description", columnDefinition = "TEXT")
    private String formDescription;

    @Type(JsonType.class)
    @Column(name = "form_config", columnDefinition = "jsonb", nullable = false)
    private JsonNode formConfig;

    @Column(name = "form_version")
    private Integer formVersion = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private FormStatus status = FormStatus.ACTIVE;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "formTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<FormField> formFields;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormDescription() {
        return formDescription;
    }

    public void setFormDescription(String formDescription) {
        this.formDescription = formDescription;
    }

    public JsonNode getFormConfig() {
        return formConfig;
    }

    public void setFormConfig(JsonNode formConfig) {
        this.formConfig = formConfig;
    }

    public Integer getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Integer formVersion) {
        this.formVersion = formVersion;
    }

    public FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormStatus status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<FormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormField> formFields) {
        this.formFields = formFields;
    }

    /**
     * 表单状态枚举
     */
    public enum FormStatus {
        ACTIVE, // 激活
        INACTIVE, // 停用
        DRAFT // 草稿
    }
}