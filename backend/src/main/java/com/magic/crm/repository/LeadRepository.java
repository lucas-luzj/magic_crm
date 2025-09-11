package com.magic.crm.repository;

import com.magic.crm.entity.Lead;
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
 * 线索数据访问层
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID>, JpaSpecificationExecutor<Lead> {
    
    /**
     * 根据线索编号查找
     */
    Optional<Lead> findByCode(String code);
    
    /**
     * 根据线索名称查找
     */
    Optional<Lead> findByName(String name);
    
    /**
     * 根据公司名称查找
     */
    List<Lead> findByCompanyName(String companyName);
    
    /**
     * 根据联系人电话查找
     */
    List<Lead> findByContactPhone(String contactPhone);
    
    /**
     * 根据联系人邮箱查找
     */
    List<Lead> findByContactEmail(String contactEmail);
    
    /**
     * 根据所属销售查找
     */
    Page<Lead> findByOwnerId(UUID ownerId, Pageable pageable);
    
    /**
     * 根据状态查找
     */
    List<Lead> findByStatus(String status);
    
    /**
     * 根据优先级查找
     */
    List<Lead> findByPriority(String priority);
    
    /**
     * 根据来源查找
     */
    List<Lead> findBySource(String source);
    
    /**
     * 根据行业查找
     */
    List<Lead> findByIndustry(String industry);
    
    /**
     * 根据地区查找
     */
    List<Lead> findByRegion(String region);
    
    /**
     * 查找已转换的线索
     */
    List<Lead> findByConvertedCustomerIdIsNotNull();
    
    /**
     * 查找未转换的线索
     */
    List<Lead> findByConvertedCustomerIdIsNull();
    
    /**
     * 查找需要跟进的线索
     */
    @Query("SELECT l FROM Lead l WHERE l.nextFollowTime <= :now AND l.status != 'CONVERTED' AND l.status != 'UNQUALIFIED' AND l.deleted = false")
    List<Lead> findLeadsToFollow(@Param("now") LocalDateTime now);
    
    /**
     * 查找超期未跟进的线索
     */
    @Query("SELECT l FROM Lead l WHERE l.nextFollowTime < :now AND l.status != 'CONVERTED' AND l.status != 'UNQUALIFIED' AND l.deleted = false")
    List<Lead> findOverdueLeads(@Param("now") LocalDateTime now);
    
    /**
     * 查找高评分线索
     */
    @Query("SELECT l FROM Lead l WHERE l.score >= :minScore AND l.deleted = false ORDER BY l.score DESC")
    List<Lead> findHighScoreLeads(@Param("minScore") Integer minScore);
    
    /**
     * 查找即将到期的线索
     */
    @Query("SELECT l FROM Lead l WHERE l.estimatedCloseDate BETWEEN :startDate AND :endDate AND l.status != 'CONVERTED' AND l.status != 'UNQUALIFIED' AND l.deleted = false")
    List<Lead> findLeadsClosingSoon(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 检查线索编号唯一性
     */
    @Query("SELECT COUNT(l) > 0 FROM Lead l WHERE l.code = :code AND l.deleted = false")
    boolean existsByCode(@Param("code") String code);
    
    /**
     * 检查线索名称唯一性
     */
    @Query("SELECT COUNT(l) > 0 FROM Lead l WHERE l.name = :name AND l.deleted = false")
    boolean existsByName(@Param("name") String name);
    
    /**
     * 搜索线索（支持名称、公司名称、联系人模糊搜索）
     */
    @Query("SELECT l FROM Lead l WHERE " +
           "(LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.contactName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR l.contactPhone LIKE CONCAT('%', :keyword, '%') " +
           "OR LOWER(l.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND l.deleted = false")
    Page<Lead> searchLeads(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计线索数量
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.deleted = false")
    Long countActiveLeads();
    
    /**
     * 统计销售人员的线索数量
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.ownerId = :ownerId AND l.deleted = false")
    Long countByOwnerId(@Param("ownerId") UUID ownerId);
    
    /**
     * 统计各状态线索数量
     */
    @Query("SELECT l.status, COUNT(l) FROM Lead l WHERE l.deleted = false GROUP BY l.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计各来源线索数量
     */
    @Query("SELECT l.source, COUNT(l) FROM Lead l WHERE l.deleted = false GROUP BY l.source")
    List<Object[]> countBySource();
    
    /**
     * 统计转换率
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.convertedCustomerId IS NOT NULL AND l.deleted = false")
    Long countConvertedLeads();
    
    /**
     * 软删除线索
     */
    @Modifying
    @Query("UPDATE Lead l SET l.deleted = true, l.updatedAt = :now WHERE l.id = :leadId")
    void softDelete(@Param("leadId") UUID leadId, @Param("now") LocalDateTime now);
    
    /**
     * 批量软删除线索
     */
    @Modifying
    @Query("UPDATE Lead l SET l.deleted = true, l.updatedAt = :now WHERE l.id IN :leadIds")
    void softDeleteBatch(@Param("leadIds") List<UUID> leadIds, @Param("now") LocalDateTime now);
    
    /**
     * 更新线索状态
     */
    @Modifying
    @Query("UPDATE Lead l SET l.status = :status, l.updatedAt = :now WHERE l.id = :leadId")
    void updateStatus(@Param("leadId") UUID leadId, @Param("status") String status, @Param("now") LocalDateTime now);
    
    /**
     * 更新线索评分
     */
    @Modifying
    @Query("UPDATE Lead l SET l.score = :score, l.updatedAt = :now WHERE l.id = :leadId")
    void updateScore(@Param("leadId") UUID leadId, @Param("score") Integer score, @Param("now") LocalDateTime now);
    
    /**
     * 更新最后联系时间
     */
    @Modifying
    @Query("UPDATE Lead l SET l.lastContactTime = :contactTime, l.updatedAt = :now WHERE l.id = :leadId")
    void updateLastContactTime(@Param("leadId") UUID leadId, @Param("contactTime") LocalDateTime contactTime, @Param("now") LocalDateTime now);
    
    /**
     * 转换线索为客户
     */
    @Modifying
    @Query("UPDATE Lead l SET l.status = 'CONVERTED', l.convertedCustomerId = :customerId, l.convertedAt = :convertedAt, l.updatedAt = :now WHERE l.id = :leadId")
    void convertToCustomer(@Param("leadId") UUID leadId, @Param("customerId") UUID customerId, @Param("convertedAt") LocalDateTime convertedAt, @Param("now") LocalDateTime now);
}
