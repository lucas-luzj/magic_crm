package com.magic.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 流程定义实体
 * 
 * @author Magic CRM Team
 */
@Entity
@Table(name = "crm_process_definition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Flowable流程定义ID
     */
    @Column(name = "process_definition_id", nullable = false, unique = true)
    private String processDefinitionId;

    /**
     * 流程定义Key
     */
    @Column(name = "process_key", nullable = false)
    private String processKey;

    /**
     * 流程名称
     */
    @Column(name = "process_name", nullable = false)
    private String processName;

    /**
     * 流程描述
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 流程版本
     */
    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * 流程分类
     */
    @Column(name = "category")
    private String category;

    /**
     * 流程状态 (ACTIVE, SUSPENDED)
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * 部署ID
     */
    @Column(name = "deployment_id")
    private String deploymentId;

    /**
     * 资源名称
     */
    @Column(name = "resource_name")
    private String resourceName;

    /**
     * 流程图资源名称
     */
    @Column(name = "diagram_resource_name")
    private String diagramResourceName;

    /**
     * 是否启动表单
     */
    @Column(name = "has_start_form_key")
    @Builder.Default
    private Boolean hasStartFormKey = false;

    /**
     * 是否图形化流程
     */
    @Column(name = "has_graphical_notation")
    @Builder.Default
    private Boolean hasGraphicalNotation = false;

    /**
     * 表单配置数据 (JSON格式)
     */
    @Column(name = "form_config", columnDefinition = "TEXT")
    private String formConfig;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}