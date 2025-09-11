package com.magic.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统菜单权限表实体类
 */
@Entity
@Table(name = "sys_menu")
@Data
public class SysMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 父菜单ID，0表示顶级菜单
     */
    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L;

    /**
     * 菜单名称
     */
    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    /**
     * 菜单类型：MENU-菜单，BUTTON-按钮
     */
    @Column(name = "menu_type", nullable = false, length = 20)
    private String menuType;

    /**
     * 路由路径
     */
    @Column(name = "path", length = 255)
    private String path;

    /**
     * 组件路径
     */
    @Column(name = "component", length = 255)
    private String component;

    /**
     * 权限标识符
     */
    @Column(name = "permission_code", length = 100)
    private String permissionCode;

    /**
     * 菜单图标
     */
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 状态：true-启用，false-禁用
     */
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    /**
     * 是否公共权限
     */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

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

    /**
     * 子菜单列表（非数据库字段，用于树形结构）
     */
    @Transient
    private List<SysMenu> children;
}