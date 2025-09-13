package com.denwon.crm.module.system.repository;

import com.denwon.crm.module.system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问层
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    
    Optional<Role> findByCode(String code);
    
    boolean existsByCode(String code);
    
    @Query("SELECT r FROM Role r WHERE r.status = :status")
    List<Role> findByStatus(@Param("status") Integer status);
    
    @Query("SELECT r FROM Role r WHERE r.type = :type AND r.status = 1")
    List<Role> findByType(@Param("type") String type);
}