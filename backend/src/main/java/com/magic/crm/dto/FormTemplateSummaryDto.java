package com.magic.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.magic.crm.entity.FormTemplate;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;

/**
 * 表单模板摘要DTO（用于分页列表）
 */
public class FormTemplateSummaryDto {

    private Long id;
    private String formKey;
    private String formName;
    private String formDescription;
    private Integer formVersion;
    private FormTemplate.FormStatus status;
    private String category;
    private String tags;
    private String createdBy;
    private JsonNode formConfig;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    // 构造函数
    public FormTemplateSummaryDto() {
    }

    public FormTemplateSummaryDto(FormTemplate formTemplate) {
        this.id = formTemplate.getId();
        this.formKey = formTemplate.getFormKey();
        this.formName = formTemplate.getFormName();
        this.formDescription = formTemplate.getFormDescription();
        this.formVersion = formTemplate.getFormVersion();
        this.status = formTemplate.getStatus();
        this.category = formTemplate.getCategory();
        this.tags = formTemplate.getTags();
        this.createdBy = formTemplate.getCreatedBy();
        this.createdTime = formTemplate.getCreatedTime();
        this.updatedBy = formTemplate.getUpdatedBy();
        this.updatedTime = formTemplate.getUpdatedTime();
        this.formConfig = formTemplate.getFormConfig();
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

    public Integer getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Integer formVersion) {
        this.formVersion = formVersion;
    }

    public FormTemplate.FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormTemplate.FormStatus status) {
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

    public JsonNode getFormConfig() {
        return this.formConfig;
    }

    public void setFormConfig(JsonNode formConfig) {
        this.formConfig = formConfig;
    }

}
