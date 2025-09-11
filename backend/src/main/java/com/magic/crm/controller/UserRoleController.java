package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.entity.SysRole;
import com.magic.crm.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys/user-roles")
public class UserRoleController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 获取用户关联的角色列表
     */
    @GetMapping("/user/{userId}")
    // @PreAuthorize("hasAuthority('sys:user:view')")
    public ApiResponse<List<SysRole>> getUserRoles(@PathVariable Long userId) {
        List<SysRole> roles = sysUserRoleService.getUserRoles(userId);
        return ApiResponse.success(roles);
    }

    /**
     * 获取用户关联的角色ID列表
     */
    @GetMapping("/user/{userId}/role-ids")
    // @PreAuthorize("hasAuthority('sys:user:view')")
    public ApiResponse<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        List<Long> roleIds = sysUserRoleService.getUserRoleIds(userId);
        return ApiResponse.success(roleIds);
    }

    /**
     * 更新用户角色关联
     */
    @PutMapping("/user/{userId}")
    // @PreAuthorize("hasAuthority('sys:user:edit')")
    public ApiResponse<Void> updateUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        sysUserRoleService.updateUserRoles(userId, roleIds);
        return ApiResponse.success("操作成功");
    }

    /**
     * 获取角色关联的用户数量
     */
    @GetMapping("/role/{roleId}/user-count")
    // @PreAuthorize("hasAuthority('sys:role:view')")
    public ApiResponse<Long> getRoleUserCount(@PathVariable Long roleId) {
        long userCount = sysUserRoleService.getRoleUserCount(roleId);
        return ApiResponse.success(userCount);
    }

    /**
     * 检查用户是否拥有指定角色
     */
    @GetMapping("/user/{userId}/has-role/{roleId}")
    // @PreAuthorize("hasAuthority('sys:user:view')")
    public ApiResponse<Boolean> userHasRole(@PathVariable Long userId, @PathVariable Long roleId) {
        boolean hasRole = sysUserRoleService.userHasRole(userId, roleId);
        return ApiResponse.success(hasRole);
    }

    /**
     * 检查用户是否拥有指定权限
     */
    @GetMapping("/user/{userId}/has-permission/{permission}")
    // @PreAuthorize("hasAuthority('sys:user:view')")
    public ApiResponse<Boolean> userHasPermission(@PathVariable Long userId, @PathVariable String permission) {
        boolean hasPermission = sysUserRoleService.userHasPermission(userId, permission);
        return ApiResponse.success(hasPermission);
    }

    /**
     * 获取当前登录用户的菜单权限
     */
    @GetMapping("/current-user/menus")
    public ApiResponse<List<Object>> getCurrentUserMenus() {
        List<Object> menus = sysUserRoleService.getCurrentUserMenus();
        return ApiResponse.success(menus);
    }

    /**
     * 获取当前登录用户的权限列表
     */
    @GetMapping("/current-user/permissions")
    public ApiResponse<List<String>> getCurrentUserPermissions() {
        List<String> permissions = sysUserRoleService.getCurrentUserPermissions();
        return ApiResponse.success(permissions);
    }
}