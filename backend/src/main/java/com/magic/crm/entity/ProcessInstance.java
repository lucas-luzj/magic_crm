package com.magic.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 流程实例实体
 * 
 * @author Magic CRM Team
 */
@Entity
@Table(name = "crm_process_instance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Flowable流程实例ID
     */
    @Column(name = "process_instance_id", nullable = false, unique = true)
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    @Column(name = "process_definition_id", nullable = false)
    private String processDefinitionId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_definition_id", referencedColumnName = "process_definition_id", insertable = false, updatable = false)
    private ProcessDefinition processDefinition;

    @Transient
    private String processName;
    /**
     * 流程定义Key
     */
    @Column(name = "process_definition_key", nullable = false)
    private String processDefinitionKey;

    /**
     * 流程实例名称
     */
    @Column(name = "process_instance_name")
    private String processInstanceName;

    /**
     * 业务Key
     */
    @Column(name = "business_key")
    private String businessKey;

    /**
     * 流程状态 (ACTIVE, SUSPENDED, COMPLETED)
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * 启动人ID
     */
    @Column(name = "started_by", nullable = false)
    private Long startedBy;

    /**
     * 启动人
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "started_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User startedByUser;

    /**
     * 启动人姓名 (通过关联查询获取，不存储在数据库中)
     */
    @Transient
    private String startedByName;

    /**
     * 启动时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 持续时间（毫秒）
     */
    @Column(name = "duration")
    private Long duration;

    /**
     * 删除原因
     */
    @Column(name = "delete_reason")
    private String deleteReason;

    /**
     * 父流程实例ID
     */
    @Column(name = "super_process_instance_id")
    private String superProcessInstanceId;

    /**
     * 租户ID
     */
    @Column(name = "tenant_id")
    private String tenantId;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PostLoad
    public void postLoad() {
        if (processDefinition != null) {
            processName = processDefinition.getProcessName();
        }
        if (startedByUser != null) {
            startedByName = startedByUser.getFullName();
        }
    }
}