package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * 表单字段实体
 */
@Entity
@Table(name = "form_field")
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_template_id", nullable = false)
    private Long formTemplateId;

    @Column(name = "field_key", nullable = false, length = 100)
    private String fieldKey;

    @Column(name = "field_name", nullable = false, length = 200)
    private String fieldName;

    @Column(name = "field_type", nullable = false, length = 50)
    private String fieldType;

    @Type(JsonType.class)
    @Column(name = "field_config", columnDefinition = "jsonb")
    private JsonNode fieldConfig;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Column(name = "is_readonly")
    private Boolean isReadonly = false;

    @Column(name = "is_visible")
    private Boolean isVisible = true;

    @Column(name = "column_span")
    private Integer columnSpan = 24;

    @Type(JsonType.class)
    @Column(name = "validation_rules", columnDefinition = "jsonb")
    private JsonNode validationRules;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_template_id", insertable = false, updatable = false)
    @JsonIgnore
    private FormTemplate formTemplate;

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

    public Long getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(Long formTemplateId) {
        this.formTemplateId = formTemplateId;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public JsonNode getFieldConfig() {
        return fieldConfig;
    }

    public void setFieldConfig(JsonNode fieldConfig) {
        this.fieldConfig = fieldConfig;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getIsReadonly() {
        return isReadonly;
    }

    public void setIsReadonly(Boolean isReadonly) {
        this.isReadonly = isReadonly;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(Integer columnSpan) {
        this.columnSpan = columnSpan;
    }

    public JsonNode getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(JsonNode validationRules) {
        this.validationRules = validationRules;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public FormTemplate getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(FormTemplate formTemplate) {
        this.formTemplate = formTemplate;
    }
}