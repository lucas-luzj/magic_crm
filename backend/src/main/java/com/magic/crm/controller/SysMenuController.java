package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.SysMenu;
import com.magic.crm.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys/menus")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 获取菜单列表（分页）
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('sys:menu:list')")
    public ApiResponse<PageResponse<SysMenu>> getMenus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysMenu> menuPage = sysMenuService.getMenus(keyword, pageable);
        return ApiResponse.success(new PageResponse<>(menuPage));
    }

    /**
     * 获取所有菜单（树形结构）
     */
    @GetMapping("/tree")
    // @PreAuthorize("hasAuthority('sys:menu:list')")
    public ApiResponse<List<SysMenu>> getMenuTree() {
        List<SysMenu> menuTree = sysMenuService.getMenuTree();
        return ApiResponse.success(menuTree);
    }

    /**
     * 获取根级菜单
     */
    @GetMapping("/root")
    // @PreAuthorize("hasAuthority('sys:menu:list')")
    public ApiResponse<List<SysMenu>> getRootMenus() {
        List<SysMenu> rootMenus = sysMenuService.getRootMenus();
        return ApiResponse.success(rootMenus);
    }

    /**
     * 根据父菜单ID获取子菜单
     */
    @GetMapping("/parent/{parentId}")
    // @PreAuthorize("hasAuthority('sys:menu:list')")
    public ApiResponse<List<SysMenu>> getMenusByParentId(@PathVariable Long parentId) {
        List<SysMenu> menus = sysMenuService.getMenusByParentId(parentId);
        return ApiResponse.success(menus);
    }

    /**
     * 根据ID获取菜单详情
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:menu:view')")
    public ApiResponse<SysMenu> getMenuById(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getMenuById(id);
        return ApiResponse.success(menu);
    }

    /**
     * 创建新菜单
     */
    @PostMapping
    // @PreAuthorize("hasAuthority('sys:menu:add')")
    public ApiResponse<SysMenu> createMenu(@RequestBody SysMenu menu) {
        SysMenu createdMenu = sysMenuService.createMenu(menu);
        return ApiResponse.success(createdMenu);
    }

    /**
     * 更新菜单信息
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:menu:edit')")
    public ApiResponse<SysMenu> updateMenu(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        SysMenu updatedMenu = sysMenuService.updateMenu(menu);
        return ApiResponse.success(updatedMenu);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:menu:delete')")
    public ApiResponse<Void> deleteMenu(@PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 获取所有可见菜单（用于前端路由生成）
     */
    @GetMapping("/visible")
    public ApiResponse<List<SysMenu>> getVisibleMenus() {
        List<SysMenu> visibleMenus = sysMenuService.getVisibleMenus();
        return ApiResponse.success(visibleMenus);
    }

    /**
     * 根据类型获取菜单
     */
    @GetMapping("/type/{type}")
    // @PreAuthorize("hasAuthority('sys:menu:list')")
    public ApiResponse<List<SysMenu>> getMenusByType(@PathVariable String type) {
        List<SysMenu> menus = sysMenuService.getMenusByType(type);
        return ApiResponse.success(menus);
    }
}