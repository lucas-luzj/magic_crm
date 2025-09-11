package com.magic.crm.repository;

import com.magic.crm.entity.Customer;
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
 * 客户数据访问层
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    
    /**
     * 根据客户编号查找
     */
    Optional<Customer> findByCode(String code);
    
    /**
     * 根据客户名称查找
     */
    Optional<Customer> findByName(String name);
    
    /**
     * 根据统一社会信用代码查找
     */
    Optional<Customer> findByUscc(String uscc);
    
    /**
     * 查找公海池客户
     */
    Page<Customer> findByIsPublicPoolTrue(Pageable pageable);
    
    /**
     * 查找私海客户
     */
    Page<Customer> findByIsPublicPoolFalseAndOwnerId(UUID ownerId, Pageable pageable);
    
    /**
     * 查找重点客户
     */
    List<Customer> findByIsKeyCustomerTrue();
    
    /**
     * 查找黑名单客户
     */
    List<Customer> findByIsBlacklistTrue();
    
    /**
     * 根据所属销售查找
     */
    Page<Customer> findByOwnerId(UUID ownerId, Pageable pageable);
    
    /**
     * 根据行业查找
     */
    List<Customer> findByIndustry(String industry);
    
    /**
     * 根据地区查找
     */
    List<Customer> findByRegion(String region);
    
    /**
     * 根据客户等级查找
     */
    List<Customer> findByCustomerLevel(String customerLevel);
    
    /**
     * 检查客户唯一性（名称+统一社会信用代码+地区）
     */
    @Query("SELECT c FROM Customer c WHERE c.name = :name AND c.uscc = :uscc AND c.region = :region AND c.deleted = false")
    Optional<Customer> findByNameAndUsccAndRegion(@Param("name") String name, 
                                                   @Param("uscc") String uscc, 
                                                   @Param("region") String region);
    
    /**
     * 查找需要进入公海的客户（超过指定天数未跟进）
     */
    @Query("SELECT c FROM Customer c WHERE c.isPublicPool = false " +
           "AND c.lastFollowTime < :beforeDate AND c.deleted = false")
    List<Customer> findCustomersToMoveToPool(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 查找需要进入公海的客户（超过指定天数未成单）
     */
    @Query("SELECT c FROM Customer c WHERE c.isPublicPool = false " +
           "AND (c.lastOrderTime IS NULL OR c.lastOrderTime < :beforeDate) " +
           "AND c.deleted = false")
    List<Customer> findCustomersWithoutOrdersToMoveToPool(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 批量更新客户为公海状态
     */
    @Modifying
    @Query("UPDATE Customer c SET c.isPublicPool = true, c.poolEntryTime = :entryTime, " +
           "c.poolEntryReason = :reason, c.ownerId = null WHERE c.id IN :customerIds")
    void moveCustomersToPool(@Param("customerIds") List<UUID> customerIds, 
                            @Param("entryTime") LocalDateTime entryTime,
                            @Param("reason") String reason);
    
    /**
     * 认领公海客户
     */
    @Modifying
    @Query("UPDATE Customer c SET c.isPublicPool = false, c.ownerId = :ownerId, " +
           "c.poolEntryTime = null, c.poolEntryReason = null WHERE c.id = :customerId")
    void claimCustomerFromPool(@Param("customerId") UUID customerId, @Param("ownerId") UUID ownerId);
    
    /**
     * 统计销售人员的客户数量
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.ownerId = :ownerId AND c.isPublicPool = false AND c.deleted = false")
    Long countByOwnerId(@Param("ownerId") UUID ownerId);
    
    /**
     * 搜索客户（支持名称、简称、联系人模糊搜索）
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN c.contacts ct " +
           "WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.shortName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR ct.mobile LIKE CONCAT('%', :keyword, '%')) " +
           "AND c.deleted = false")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取客户的子公司
     */
    List<Customer> findByParentCustomerId(UUID parentCustomerId);
    
    /**
     * 更新最后跟进时间
     */
    @Modifying
    @Query("UPDATE Customer c SET c.lastFollowTime = :followTime WHERE c.id = :customerId")
    void updateLastFollowTime(@Param("customerId") UUID customerId, @Param("followTime") LocalDateTime followTime);
    
    /**
     * 更新最后成单时间
     */
    @Modifying
    @Query("UPDATE Customer c SET c.lastOrderTime = :orderTime WHERE c.id = :customerId")
    void updateLastOrderTime(@Param("customerId") UUID customerId, @Param("orderTime") LocalDateTime orderTime);
    
    /**
     * 软删除客户
     */
    @Modifying
    @Query("UPDATE Customer c SET c.deleted = true, c.updatedAt = :now WHERE c.id = :customerId")
    void softDelete(@Param("customerId") UUID customerId, @Param("now") LocalDateTime now);
    
    /**
     * 批量软删除客户
     */
    @Modifying
    @Query("UPDATE Customer c SET c.deleted = true, c.updatedAt = :now WHERE c.id IN :customerIds")
    void softDeleteBatch(@Param("customerIds") List<UUID> customerIds, @Param("now") LocalDateTime now);
}