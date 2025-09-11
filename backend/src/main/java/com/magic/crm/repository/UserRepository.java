package com.magic.crm.repository;

import com.magic.crm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByUsername(String username);

        Optional<User> findByEmail(String email);

        Optional<User> findByUsernameOrEmail(String username, String email);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        @Modifying
        @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
        void updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

        @Modifying
        @Query("UPDATE User u SET u.lastLoginAt = :loginTime, u.lastLoginIp = :clientIp, u.lastLoginUserAgent = :userAgent WHERE u.id = :userId")
        void updateLastLoginInfo(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime,
                        @Param("clientIp") String clientIp, @Param("userAgent") String userAgent);

        // 分页查询，支持按用户名、邮箱、全名、拼音、部门、搜索
        @Query("SELECT u FROM User u LEFT JOIN FETCH u.department d WHERE " +
                        "(d.id = :departmentId OR :departmentId IS NULL) AND " +
                        "(:keyword IS NULL OR :keyword = '' OR " +
                        "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(u.namePinyin) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<User> findByKeyword(@Param("keyword") String keyword, Integer departmentId, Pageable pageable);
}