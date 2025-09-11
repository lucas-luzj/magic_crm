package com.magic.crm.service;

import com.magic.crm.entity.SysRole;
import com.magic.crm.repository.SysRoleRepository;
import com.magic.crm.repository.SysUserRoleRepository;
import com.magic.crm.repository.SysRoleMenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SysRoleService {

    private final SysRoleRepository sysRoleRepository;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;

    public SysRoleService(SysRoleRepository sysRoleRepository,
            SysUserRoleRepository sysUserRoleRepository,
            SysRoleMenuRepository sysRoleMenuRepository) {
        this.sysRoleRepository = sysRoleRepository;
        this.sysUserRoleRepository = sysUserRoleRepository;
        this.sysRoleMenuRepository = sysRoleMenuRepository;
    }

    public Page<SysRole> findAll(Pageable pageable) {
        return sysRoleRepository.findAll(pageable);
    }

    public List<SysRole> findAll() {
        return sysRoleRepository.findAll();
    }

    public Page<SysRole> findByKeyword(String keyword, Pageable pageable) {
        return sysRoleRepository.findByKeyword(keyword, pageable);
    }

    public Optional<SysRole> findById(Long id) {
        return sysRoleRepository.findById(id);
    }

    public Optional<SysRole> findByRoleName(String roleName) {
        return sysRoleRepository.findByRoleName(roleName);
    }

    public Optional<SysRole> findByRoleCode(String roleCode) {
        return sysRoleRepository.findByRoleCode(roleCode);
    }

    @Transactional
    public SysRole create(SysRole role) {
        if (sysRoleRepository.existsByRoleName(role.getRoleName())) {
            throw new RuntimeException("角色名称已存在");
        }
        if (sysRoleRepository.existsByRoleCode(role.getRoleCode())) {
            throw new RuntimeException("角色编码已存在");
        }

        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        return sysRoleRepository.save(role);
    }

    @Transactional
    public SysRole update(Long id, SysRole roleDetails) {
        SysRole role = sysRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));

        if (!role.getRoleName().equals(roleDetails.getRoleName()) &&
                sysRoleRepository.existsByRoleName(roleDetails.getRoleName())) {
            throw new RuntimeException("角色名称已存在");
        }

        if (!role.getRoleCode().equals(roleDetails.getRoleCode()) &&
                sysRoleRepository.existsByRoleCode(roleDetails.getRoleCode())) {
            throw new RuntimeException("角色编码已存在");
        }

        role.setRoleName(roleDetails.getRoleName());
        role.setRoleCode(roleDetails.getRoleCode());
        role.setDescription(roleDetails.getDescription());
        role.setUpdateTime(LocalDateTime.now());

        return sysRoleRepository.save(role);
    }

    @Transactional
    public void delete(Long id) {
        SysRole role = sysRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));

        // 检查是否有用户关联此角色
        long userCount = sysUserRoleRepository.findByRoleId(id).size();
        if (userCount > 0) {
            throw new RuntimeException("该角色已被用户使用，无法删除");
        }

        // 删除角色菜单关联
        sysRoleMenuRepository.deleteByRoleId(id);

        sysRoleRepository.delete(role);
    }

    public boolean existsByRoleName(String roleName) {
        return sysRoleRepository.existsByRoleName(roleName);
    }

    public boolean existsByRoleCode(String roleCode) {
        return sysRoleRepository.existsByRoleCode(roleCode);
    }

    /**
     * 为角色分配权限
     */
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> menuIds) {
        // 删除角色原有的权限
        sysRoleMenuRepository.deleteByRoleId(roleId);

        // 添加新的权限
        if (menuIds != null && !menuIds.isEmpty()) {
            Long[] menuIdsArray = menuIds.toArray(new Long[0]);
            sysRoleMenuRepository.insertRoleMenus(roleId, menuIdsArray);
        }
    }

    /**
     * 获取角色关联的菜单权限ID列表
     */
    public List<Long> getRoleMenuIds(Long roleId) {
        return sysRoleMenuRepository.findMenuIdsByRoleId(roleId);
    }

    /**
     * 根据多个角色ID获取菜单权限ID列表
     */
    public List<Long> getMenuIdsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return sysRoleMenuRepository.findMenuIdsByRoleIds(roleIds);
    }
}