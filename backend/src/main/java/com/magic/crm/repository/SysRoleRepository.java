package com.magic.crm.repository;

import com.magic.crm.entity.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByRoleName(String roleName);

    Optional<SysRole> findByRoleCode(String roleCode);

    boolean existsByRoleName(String roleName);

    boolean existsByRoleCode(String roleCode);

    @Query("SELECT r FROM SysRole r WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.roleCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SysRole> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}