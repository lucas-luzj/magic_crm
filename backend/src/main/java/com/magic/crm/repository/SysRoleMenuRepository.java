package com.magic.crm.repository;

import com.magic.crm.entity.SysRoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleMenuRepository extends JpaRepository<SysRoleMenu, Long> {

    List<SysRoleMenu> findByRoleId(Long roleId);

    @Query("SELECT rm.menuId FROM SysRoleMenu rm WHERE rm.roleId = :roleId")
    List<Long> findMenuIdsByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM SysRoleMenu rm WHERE rm.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM SysRoleMenu rm WHERE rm.roleId = :roleId AND rm.menuId = :menuId")
    void deleteByRoleIdAndMenuId(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    boolean existsByRoleIdAndMenuId(Long roleId, Long menuId);

    List<SysRoleMenu> findByMenuId(Long menuId);

    @Modifying
    @Query(value = "INSERT INTO sys_role_menu (role_id, menu_id, create_time, update_time, deleted) " +
            "SELECT :roleId, unnest(:menuIds), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false", 
            nativeQuery = true)
    void insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);

    @Query("SELECT DISTINCT rm.menuId FROM SysRoleMenu rm WHERE rm.roleId IN :roleIds")
    List<Long> findMenuIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}