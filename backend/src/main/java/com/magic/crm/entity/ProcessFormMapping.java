package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 流程表单映射实体
 * 定义流程节点与表单的关联关系
 */
@Entity
@Table(name = "process_form_mapping")
public class ProcessFormMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_definition_key", nullable = false, length = 255)
    private String processDefinitionKey;

    @Column(name = "task_definition_key", length = 255)
    private String taskDefinitionKey;

    @Column(name = "form_template_id", nullable = false)
    private Long formTemplateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mapping_type", length = 20)
    private MappingType mappingType = MappingType.TASK;

    @Column(name = "form_version")
    private Integer formVersion = 1;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "form_position", length = 20)
    private FormPosition formPosition = FormPosition.BEFORE;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_template_id", insertable = false, updatable = false)
    private FormTemplate formTemplate;

    @PrePersist
    protected void onCreate() {
        if (createdTime == null) {
            createdTime = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public Long getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(Long formTemplateId) {
        this.formTemplateId = formTemplateId;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    public Integer getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Integer formVersion) {
        this.formVersion = formVersion;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public FormPosition getFormPosition() {
        return formPosition;
    }

    public void setFormPosition(FormPosition formPosition) {
        this.formPosition = formPosition;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public FormTemplate getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(FormTemplate formTemplate) {
        this.formTemplate = formTemplate;
    }

    /**
     * 映射类型枚举
     */
    public enum MappingType {
        START,  // 开始节点
        TASK,   // 任务节点
        END     // 结束节点
    }

    /**
     * 表单位置枚举
     */
    public enum FormPosition {
        BEFORE,   // 任务前
        AFTER,    // 任务后
        REPLACE   // 替换任务
    }
}
