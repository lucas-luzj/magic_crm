package com.magic.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 任务实体
 * 
 * @author Magic CRM Team
 */
@Entity
@Table(name = "crm_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Flowable任务ID
     */
    @Column(name = "task_id", nullable = false, unique = true)
    private String taskId;

    /**
     * 任务名称
     */
    @Column(name = "task_name", nullable = false)
    private String taskName;

    /**
     * 任务描述
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 任务定义Key
     */
    @Column(name = "task_definition_key")
    private String taskDefinitionKey;

    /**
     * 流程实例ID
     */
    @Column(name = "process_instance_id", nullable = false)
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    @Column(name = "process_definition_id", nullable = false)
    private String processDefinitionId;

    /**
     * 执行ID
     */
    @Column(name = "execution_id")
    private String executionId;

    /**
     * 分配人ID
     */
    @Column(name = "assignee")
    private Long assignee;

    /**
     * 候选用户组
     */
    @Column(name = "candidate_groups")
    private String candidateGroups;

    /**
     * 候选用户
     */
    @Column(name = "candidate_users")
    private String candidateUsers;

    /**
     * 任务优先级
     */
    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 50;

    /**
     * 任务状态 (CREATED, ASSIGNED, COMPLETED, DELEGATED, PENDING)
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "CREATED";

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 到期时间
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * 跟进时间
     */
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    /**
     * 委派人ID
     */
    @Column(name = "owner")
    private Long owner;

    /**
     * 父任务ID
     */
    @Column(name = "parent_task_id")
    private String parentTaskId;

    /**
     * 租户ID
     */
    @Column(name = "tenant_id")
    private String tenantId;

    /**
     * 表单Key
     */
    @Column(name = "form_key")
    private String formKey;

    /**
     * 任务类型
     */
    @Column(name = "task_type")
    private String taskType;

    /**
     * 业务数据
     */
    @Column(name = "business_data", length = 2000)
    private String businessData;

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
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}