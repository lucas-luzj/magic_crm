package com.denwon.crm.module.system.repository;

import com.denwon.crm.module.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 * 
 * @author Denwon Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobile(String mobile);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobile(String mobile);
    
    @Query("SELECT u FROM User u WHERE u.orgUnit.id = :orgUnitId AND u.deleted = false")
    List<User> findByOrgUnitId(@Param("orgUnitId") Long orgUnitId);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.code = :roleCode AND u.deleted = false")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.deleted = false")
    List<User> findByStatus(@Param("status") Integer status);
}