package com.magic.crm.repository;

import com.magic.crm.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, Long> {

    List<SysUserRole> findByUserId(Long userId);

    @Query("SELECT ur.roleId FROM SysUserRole ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM SysUserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM SysUserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM SysUserRole ur WHERE ur.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    List<SysUserRole> findByRoleId(Long roleId);
}