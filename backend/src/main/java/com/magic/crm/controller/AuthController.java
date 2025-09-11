package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.entity.SysMenu;
import com.magic.crm.service.SysMenuService;
import com.magic.crm.service.SysRoleService;
import com.magic.crm.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 获取当前用户的权限信息
     */
    @GetMapping("/permissions")
    public ApiResponse<List<Long>> getCurrentUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.success(List.of());
        }

        Long userId = getUserIdFromAuthentication(authentication);
        if (userId == null) {
            return ApiResponse.success(List.of());
        }

        // 获取用户的角色ID
        List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        // 获取这些角色对应的权限（菜单ID）
        List<Long> menuIds = sysRoleService.getMenuIdsByRoleIds(roleIds);
        return ApiResponse.success(menuIds);
    }

    /**
     * 获取当前用户的菜单权限（用于前端动态菜单）
     */
    @GetMapping("/menus")
    public ApiResponse<List<SysMenu>> getCurrentUserMenus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.success(List.of());
        }

        Long userId = getUserIdFromAuthentication(authentication);
        if (userId == null) {
            return ApiResponse.success(List.of());
        }

        // 获取用户的角色ID
        List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        // 根据角色ID获取对应的菜单权限ID
        List<Long> menuIds = sysRoleService.getMenuIdsByRoleIds(roleIds);
        if (menuIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        // 获取菜单详情并构建树形结构
        List<SysMenu> menus = sysMenuService.getMenuTreeByIds(menuIds);
        return ApiResponse.success(menus);
    }

    /**
     * 获取当前用户的菜单权限（用于前端动态菜单）- 兼容旧接口
     */
    @GetMapping("/user-menus")
    public ApiResponse<List<SysMenu>> getCurrentUserMenusCompatible() {
        return getCurrentUserMenus();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        try {
            // 从认证信息中获取User实体
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.magic.crm.entity.User) {
                com.magic.crm.entity.User user = (com.magic.crm.entity.User) principal;
                return user.getId();
            }
        } catch (Exception e) {
            // 记录错误日志，但不抛出异常，返回null表示无法获取用户ID
            System.err.println("无法从认证信息中获取用户ID: " + e.getMessage());
        }
        return null;
    }
}