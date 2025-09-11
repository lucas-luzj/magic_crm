package com.magic.crm.service;

import com.magic.crm.entity.SysUserRole;
import com.magic.crm.entity.SysRole;
import com.magic.crm.repository.SysUserRoleRepository;
import com.magic.crm.repository.SysRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserRoleService {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleRepository sysRoleRepository;

    public SysUserRoleService(SysUserRoleRepository sysUserRoleRepository, SysRoleRepository sysRoleRepository) {
        this.sysUserRoleRepository = sysUserRoleRepository;
        this.sysRoleRepository = sysRoleRepository;
    }

    /**
     * 获取用户的角色ID列表
     */
    public List<Long> getRoleIdsByUserId(Long userId) {
        return sysUserRoleRepository.findByUserId(userId)
                .stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    /**
     * 更新用户的角色
     */
    @Transactional
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // 删除用户现有的所有角色
        sysUserRoleRepository.deleteByUserId(userId);

        // 添加新的角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            sysUserRoleRepository.saveAll(userRoles);
        }
    }

    /**
     * 检查用户是否拥有某个角色
     */
    public boolean userHasRole(Long userId, Long roleId) {
        return sysUserRoleRepository.existsByUserIdAndRoleId(userId, roleId);
    }

    /**
     * 删除用户的所有角色
     */
    @Transactional
    public void deleteByUserId(Long userId) {
        sysUserRoleRepository.deleteByUserId(userId);
    }

    /**
     * 删除角色的所有用户关联
     */
    @Transactional
    public void deleteByRoleId(Long roleId) {
        sysUserRoleRepository.deleteByRoleId(roleId);
    }

    /**
     * 获取用户的角色列表
     */
    public List<SysRole> getUserRoles(Long userId) {
        List<Long> roleIds = sysUserRoleRepository.findRoleIdsByUserId(userId);
        return sysRoleRepository.findAllById(roleIds);
    }

    /**
     * 获取用户的角色ID列表
     */
    public List<Long> getUserRoleIds(Long userId) {
        return sysUserRoleRepository.findRoleIdsByUserId(userId);
    }

    /**
     * 获取角色关联的用户数量
     */
    public long getRoleUserCount(Long roleId) {
        return sysUserRoleRepository.findByRoleId(roleId).size();
    }

    /**
     * 检查用户是否拥有指定权限
     */
    public boolean userHasPermission(Long userId, String permission) {
        // 这里需要根据实际的权限检查逻辑来实现
        // 暂时返回false，需要根据具体的权限表结构来实现
        return false;
    }

    /**
     * 获取当前登录用户的菜单权限
     */
    public List<Object> getCurrentUserMenus() {
        // 这里需要根据实际的菜单权限逻辑来实现
        // 暂时返回空列表，需要根据具体的菜单表结构来实现
        return List.of();
    }

    /**
     * 获取当前登录用户的权限列表
     */
    public List<String> getCurrentUserPermissions() {
        // 这里需要根据实际的权限逻辑来实现
        // 暂时返回空列表，需要根据具体的权限表结构来实现
        return List.of();
    }
}