package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.SysRole;
import com.magic.crm.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys/roles")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('sys:role:list')")
    public ApiResponse<PageResponse<SysRole>> getRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysRole> rolePage = sysRoleService.findByKeyword(keyword, pageable);
        return ApiResponse.success(new PageResponse<>(rolePage));
    }

    /**
     * 获取所有角色（用于下拉选择）
     */
    @GetMapping("/all")
    // @PreAuthorize("hasAuthority('sys:role:list')")
    public ApiResponse<List<SysRole>> getAllRoles() {
        List<SysRole> roles = sysRoleService.findAll();
        return ApiResponse.success(roles);
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:role:view')")
    public ApiResponse<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = sysRoleService.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        return ApiResponse.success(role);
    }

    /**
     * 创建新角色
     */
    @PostMapping
    // @PreAuthorize("hasAuthority('sys:role:add')")
    public ApiResponse<SysRole> createRole(@RequestBody SysRole role) {
        SysRole createdRole = sysRoleService.create(role);
        return ApiResponse.success(createdRole);
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:role:edit')")
    public ApiResponse<SysRole> updateRole(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        SysRole updatedRole = sysRoleService.update(id, role);
        return ApiResponse.success(updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:role:delete')")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        sysRoleService.delete(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 获取角色关联的菜单权限ID列表
     */
    @GetMapping("/{id}/menu-ids")
    // @PreAuthorize("hasAuthority('sys:role:view')")
    public ApiResponse<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        List<Long> menuIds = sysRoleService.getRoleMenuIds(id);
        return ApiResponse.success(menuIds);
    }

    /**
     * 更新角色菜单权限
     */
    @PutMapping("/{id}/menus")
    // @PreAuthorize("hasAuthority('sys:role:edit')")
    public ApiResponse<Void> updateRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignPermissionsToRole(id, menuIds);
        return ApiResponse.success("删除成功");
    }
}