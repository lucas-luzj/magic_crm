package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 系统角色表实体类
 */
@Entity
@Table(name = "sys_role")
@Data
public class SysRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称
     */
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    /**
     * 角色代码
     */
    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;

    /**
     * 角色描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 状态：true-启用，false-禁用
     */
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 更新人
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 删除标记
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
}