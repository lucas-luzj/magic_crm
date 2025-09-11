package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * 表单数据实例实体
 * 存储用户提交的表单数据
 */
@Entity
@Table(name = "form_data_instance")
public class FormDataInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_template_id", nullable = false)
    private Long formTemplateId;

    @Column(name = "process_instance_id", length = 64)
    private String processInstanceId;

    @Column(name = "task_id", length = 64)
    private String taskId;

    @Column(name = "business_key", length = 200)
    private String businessKey;

    @Type(JsonType.class)
    @Column(name = "form_data", columnDefinition = "jsonb", nullable = false)
    private JsonNode formData;

    @Column(name = "submit_user", length = 100)
    private String submitUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private FormDataStatus status = FormDataStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_template_id", insertable = false, updatable = false)
    private FormTemplate formTemplate;

    @PrePersist
    protected void onCreate() {
        if (submitTime == null) {
            submitTime = LocalDateTime.now();
        }
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

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public JsonNode getFormData() {
        return formData;
    }

    public void setFormData(JsonNode formData) {
        this.formData = formData;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public FormDataStatus getStatus() {
        return status;
    }

    public void setStatus(FormDataStatus status) {
        this.status = status;
    }

    public FormTemplate getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(FormTemplate formTemplate) {
        this.formTemplate = formTemplate;
    }

    /**
     * 表单数据状态枚举
     */
    public enum FormDataStatus {
        DRAFT,      // 草稿
        SUBMITTED,  // 已提交
        APPROVED,   // 已审批
        REJECTED    // 已拒绝
    }
}
