package com.magic.crm.service;

import com.magic.crm.entity.SysMenu;
import com.magic.crm.repository.SysMenuRepository;
import com.magic.crm.repository.SysRoleMenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuService {

    private final SysMenuRepository sysMenuRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;

    public SysMenuService(SysMenuRepository sysMenuRepository,
            SysRoleMenuRepository sysRoleMenuRepository) {
        this.sysMenuRepository = sysMenuRepository;
        this.sysRoleMenuRepository = sysRoleMenuRepository;
    }

    public Page<SysMenu> findAll(Pageable pageable) {
        return sysMenuRepository.findAll(pageable);
    }

    public Page<SysMenu> getMenus(String keyword, Pageable pageable) {
        return sysMenuRepository.findByKeyword(keyword, pageable);
    }

    public SysMenu getMenuById(Long id) {
        return sysMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜单不存在"));
    }

    public List<SysMenu> getMenusByParentId(Long parentId) {
        return sysMenuRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }

    public List<SysMenu> getMenusByType(String menuType) {
        return sysMenuRepository.findByMenuTypeOrderBySortOrderAsc(menuType);
    }

    public List<SysMenu> getVisibleMenus() {
        return sysMenuRepository.findByStatusTrueOrderBySortOrderAsc();
    }

    public List<SysMenu> getRootMenus() {
        return sysMenuRepository.findRootMenus();
    }

    /**
     * 获取菜单树形结构
     */
    public List<SysMenu> getMenuTree() {
        List<SysMenu> rootMenus = sysMenuRepository.findRootMenus();
        return buildMenuTree(rootMenus);
    }

    /**
     * 递归构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        for (SysMenu menu : menus) {
            List<SysMenu> children = sysMenuRepository.findByParentIdOrderBySortOrderAsc(menu.getId());
            if (!children.isEmpty()) {
                menu.setChildren(buildMenuTree(children));
            }
        }
        return menus;
    }

    public List<SysMenu> findVisibleMenusByParentId(Long parentId) {
        return sysMenuRepository.findByParentIdAndStatusTrueOrderBySortOrderAsc(parentId);
    }

    @Transactional
    public SysMenu createMenu(SysMenu menu) {
        if (menu.getPermissionCode() != null && !menu.getPermissionCode().isEmpty() &&
                sysMenuRepository.existsByPermissionCode(menu.getPermissionCode())) {
            throw new RuntimeException("权限标识已存在");
        }

        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        return sysMenuRepository.save(menu);
    }

    @Transactional
    public SysMenu updateMenu(SysMenu menuDetails) {
        SysMenu menu = sysMenuRepository.findById(menuDetails.getId())
                .orElseThrow(() -> new RuntimeException("菜单不存在"));

        if (menuDetails.getPermissionCode() != null && !menuDetails.getPermissionCode().isEmpty() &&
                !menuDetails.getPermissionCode().equals(menu.getPermissionCode()) &&
                sysMenuRepository.existsByPermissionCode(menuDetails.getPermissionCode())) {
            throw new RuntimeException("权限标识已存在");
        }

        menu.setMenuName(menuDetails.getMenuName());
        menu.setPath(menuDetails.getPath());
        menu.setComponent(menuDetails.getComponent());
        menu.setPermissionCode(menuDetails.getPermissionCode());
        menu.setMenuType(menuDetails.getMenuType());
        menu.setIcon(menuDetails.getIcon());
        menu.setSortOrder(menuDetails.getSortOrder());
        menu.setStatus(menuDetails.getStatus());
        menu.setParentId(menuDetails.getParentId());
        menu.setUpdateTime(LocalDateTime.now());

        return sysMenuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(Long id) {
        SysMenu menu = sysMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜单不存在"));

        // 检查是否有子菜单
        List<SysMenu> children = sysMenuRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("该菜单存在子菜单，请先删除子菜单");
        }

        // 检查是否有角色关联此菜单
        long roleCount = sysRoleMenuRepository.findByMenuId(id).size();
        if (roleCount > 0) {
            throw new RuntimeException("该菜单已被角色使用，无法删除");
        }

        sysMenuRepository.delete(menu);
    }

    public boolean existsByPermissionCode(String permissionCode) {
        return sysMenuRepository.existsByPermissionCode(permissionCode);
    }

    /**
     * 根据菜单ID列表获取菜单详情
     */
    public List<SysMenu> getMenusByIds(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return List.of();
        }
        return sysMenuRepository.findAllById(menuIds);
    }

    /**
     * 根据菜单ID列表获取菜单树形结构
     */
    public List<SysMenu> getMenuTreeByIds(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return List.of();
        }
        
        // 获取所有菜单详情
        List<SysMenu> menus = sysMenuRepository.findAllById(menuIds);
        
        // 构建树形结构
        return buildMenuTreeFromList(menus);
    }

    /**
     * 从菜单列表构建树形结构
     */
    private List<SysMenu> buildMenuTreeFromList(List<SysMenu> menus) {
        // 创建菜单映射
        Map<Long, SysMenu> menuMap = menus.stream()
                .collect(Collectors.toMap(SysMenu::getId, menu -> menu));
        
        // 创建根菜单列表
        List<SysMenu> rootMenus = new ArrayList<>();
        
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                // 根菜单
                rootMenus.add(menu);
            } else {
                // 子菜单，添加到父菜单下
                SysMenu parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }
        
        // 对每个层级的菜单进行排序
        sortMenus(rootMenus);
        
        return rootMenus;
    }

    /**
     * 递归排序菜单
     */
    private void sortMenus(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        
        // 按排序字段排序
        menus.sort(Comparator.comparing(SysMenu::getSortOrder));
        
        // 递归排序子菜单
        for (SysMenu menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                sortMenus(menu.getChildren());
            }
        }
    }
}