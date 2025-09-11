package com.magic.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门数据传输对象
 */
@Data
public class DepartmentDTO {

    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String name;

    @Size(max = 50, message = "部门编码长度不能超过50个字符")
    private String code;

    private Long parentId;

    private Long managerId;

    @Size(max = 500, message = "部门描述长度不能超过500个字符")
    private String description;

    private Integer sortOrder;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    // 扩展字段
    private List<DepartmentDTO> children;

    private String managerName;

    private String parentName;

    private Long userCount;
}