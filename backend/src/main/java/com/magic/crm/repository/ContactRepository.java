package com.magic.crm.repository;

import com.magic.crm.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 联系人数据访问层
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {
    
    /**
     * 根据客户ID查找联系人
     */
    List<Contact> findByCustomerId(UUID customerId);
    
    /**
     * 根据客户ID分页查找联系人
     */
    Page<Contact> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * 根据手机号查找
     */
    Optional<Contact> findByMobile(String mobile);
    
    /**
     * 根据邮箱查找
     */
    Optional<Contact> findByEmail(String email);
    
    /**
     * 查找客户的主联系人
     */
    Optional<Contact> findByCustomerIdAndIsPrimaryTrue(UUID customerId);
    
    /**
     * 根据姓名模糊查找
     */
    List<Contact> findByNameContaining(String name);
    
    /**
     * 检查联系人唯一性（同一客户下）
     */
    @Query("SELECT c FROM Contact c WHERE c.customerId = :customerId " +
           "AND c.name = :name AND c.mobile = :mobile AND c.deleted = false")
    Optional<Contact> findByCustomerIdAndNameAndMobile(@Param("customerId") UUID customerId,
                                                       @Param("name") String name,
                                                       @Param("mobile") String mobile);
    
    /**
     * 根据决策角色查找
     */
    List<Contact> findByDecisionRole(String decisionRole);
    
    /**
     * 查找今天生日的联系人
     */
    @Query("SELECT c FROM Contact c WHERE EXTRACT(MONTH FROM c.birthday) = :month " +
           "AND EXTRACT(DAY FROM c.birthday) = :day AND c.deleted = false")
    List<Contact> findTodayBirthdays(@Param("month") int month, @Param("day") int day);
    
    /**
     * 设置主联系人（先清除其他主联系人）
     */
    @Modifying
    @Query("UPDATE Contact c SET c.isPrimary = false WHERE c.customerId = :customerId AND c.id != :contactId")
    void clearOtherPrimaryContacts(@Param("customerId") UUID customerId, @Param("contactId") UUID contactId);
    
    /**
     * 批量更新客户ID（用于客户合并）
     */
    @Modifying
    @Query("UPDATE Contact c SET c.customerId = :newCustomerId WHERE c.customerId = :oldCustomerId")
    void updateCustomerId(@Param("oldCustomerId") UUID oldCustomerId, @Param("newCustomerId") UUID newCustomerId);
    
    /**
     * 软删除联系人
     */
    @Modifying
    @Query("UPDATE Contact c SET c.deleted = true, c.updatedAt = :now WHERE c.id = :contactId")
    void softDelete(@Param("contactId") UUID contactId, @Param("now") LocalDateTime now);
    
    /**
     * 批量软删除联系人
     */
    @Modifying
    @Query("UPDATE Contact c SET c.deleted = true, c.updatedAt = :now WHERE c.id IN :contactIds")
    void softDeleteBatch(@Param("contactIds") List<UUID> contactIds, @Param("now") LocalDateTime now);
    
    /**
     * 根据客户ID批量软删除联系人
     */
    @Modifying
    @Query("UPDATE Contact c SET c.deleted = true, c.updatedAt = :now WHERE c.customerId = :customerId")
    void softDeleteByCustomerId(@Param("customerId") UUID customerId, @Param("now") LocalDateTime now);
    
    /**
     * 统计客户的联系人数量
     */
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.customerId = :customerId AND c.deleted = false")
    Long countByCustomerId(@Param("customerId") UUID customerId);
    
    /**
     * 搜索联系人
     */
    @Query("SELECT c FROM Contact c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR c.mobile LIKE CONCAT('%', :keyword, '%') " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND c.deleted = false")
    Page<Contact> searchContacts(@Param("keyword") String keyword, Pageable pageable);
}