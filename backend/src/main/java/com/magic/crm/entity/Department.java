package com.magic.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.magic.crm.config.LocalDateTimeDeserializer;

/**
 * 部门实体类
 */
@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Department {

    /**
     * 部门ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门名称
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    /**
     * 部门编码
     */
    @Size(max = 50)
    @Column(unique = true)
    private String code;

    /**
     * 父部门ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 部门负责人ID
     */
    @Column(name = "manager_id")
    private Long managerId;

    /**
     * 部门描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 部门状态：true-启用，false-禁用
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 子部门列表（非数据库字段）
     */
    @Transient
    private List<Department> children;

    /**
     * 所属部门（非数据库字段）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private User manager;

    /**
     * 部门负责人姓名（非数据库字段）
     */
    @Transient
    private String managerName;

    /**
     * 父部门名称（非数据库字段）
     */
    @Transient
    private String parentName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PostLoad
    public void postLoad() {
        if (manager != null) {
            managerName = manager.getFullName();
        }
    }
}