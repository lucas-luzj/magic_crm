package com.magic.crm.repository;

import com.magic.crm.entity.Supplier;
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
 * 供应商数据访问层
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID>, JpaSpecificationExecutor<Supplier> {
    
    /**
     * 根据供应商编码查找
     */
    Optional<Supplier> findByCode(String code);
    
    /**
     * 根据供应商名称查找
     */
    Optional<Supplier> findByName(String name);
    
    /**
     * 根据统一社会信用代码查找
     */
    Optional<Supplier> findByUscc(String uscc);
    
    /**
     * 根据行业查找
     */
    List<Supplier> findByIndustry(String industry);
    
    /**
     * 根据地区查找
     */
    List<Supplier> findByRegion(String region);
    
    /**
     * 根据供应商等级查找
     */
    List<Supplier> findBySupplierLevel(String supplierLevel);
    
    /**
     * 根据供应商类型查找
     */
    List<Supplier> findBySupplierType(String supplierType);
    
    /**
     * 根据来源查找
     */
    List<Supplier> findBySource(String source);
    
    /**
     * 根据状态查找
     */
    List<Supplier> findByStatus(String status);
    
    /**
     * 查找黑名单供应商
     */
    List<Supplier> findByIsBlacklistTrue();
    
    /**
     * 查找重点供应商
     */
    List<Supplier> findByIsKeySupplierTrue();
    
    /**
     * 查找即将到期的合同
     */
    @Query("SELECT s FROM Supplier s WHERE s.contractEndDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE' AND s.deleted = false")
    List<Supplier> findExpiringContracts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查找已过期的合同
     */
    @Query("SELECT s FROM Supplier s WHERE s.contractEndDate < :now AND s.status = 'ACTIVE' AND s.deleted = false")
    List<Supplier> findExpiredContracts(@Param("now") LocalDateTime now);
    
    /**
     * 查找高评级供应商
     */
    @Query("SELECT s FROM Supplier s WHERE s.overallRating >= :minRating AND s.deleted = false ORDER BY s.overallRating DESC")
    List<Supplier> findHighRatedSuppliers(@Param("minRating") Integer minRating);
    
    /**
     * 查找需要评价的供应商
     */
    @Query("SELECT s FROM Supplier s WHERE s.lastEvaluationDate < :beforeDate AND s.status = 'ACTIVE' AND s.deleted = false")
    List<Supplier> findSuppliersNeedingEvaluation(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 检查供应商编码唯一性
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.code = :code AND s.deleted = false")
    boolean existsByCode(@Param("code") String code);
    
    /**
     * 检查供应商名称唯一性
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.name = :name AND s.deleted = false")
    boolean existsByName(@Param("name") String name);
    
    /**
     * 检查统一社会信用代码唯一性
     */
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.uscc = :uscc AND s.deleted = false")
    boolean existsByUscc(@Param("uscc") String uscc);
    
    /**
     * 搜索供应商（支持名称、编码、简称模糊搜索）
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.shortName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND s.deleted = false")
    Page<Supplier> searchSuppliers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计供应商数量
     */
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.deleted = false")
    Long countActiveSuppliers();
    
    /**
     * 统计各状态供应商数量
     */
    @Query("SELECT s.status, COUNT(s) FROM Supplier s WHERE s.deleted = false GROUP BY s.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计各行业供应商数量
     */
    @Query("SELECT s.industry, COUNT(s) FROM Supplier s WHERE s.deleted = false GROUP BY s.industry")
    List<Object[]> countByIndustry();
    
    /**
     * 统计各等级供应商数量
     */
    @Query("SELECT s.supplierLevel, COUNT(s) FROM Supplier s WHERE s.deleted = false GROUP BY s.supplierLevel")
    List<Object[]> countBySupplierLevel();
    
    /**
     * 软删除供应商
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.deleted = true, s.updatedAt = :now WHERE s.id = :supplierId")
    void softDelete(@Param("supplierId") UUID supplierId, @Param("now") LocalDateTime now);
    
    /**
     * 批量软删除供应商
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.deleted = true, s.updatedAt = :now WHERE s.id IN :supplierIds")
    void softDeleteBatch(@Param("supplierIds") List<UUID> supplierIds, @Param("now") LocalDateTime now);
    
    /**
     * 更新供应商状态
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.status = :status, s.updatedAt = :now WHERE s.id = :supplierId")
    void updateStatus(@Param("supplierId") UUID supplierId, @Param("status") String status, @Param("now") LocalDateTime now);
    
    /**
     * 更新供应商评级
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.qualityRating = :qualityRating, s.serviceRating = :serviceRating, " +
           "s.priceRating = :priceRating, s.overallRating = :overallRating, s.lastEvaluationDate = :evaluationDate, " +
           "s.updatedAt = :now WHERE s.id = :supplierId")
    void updateRatings(@Param("supplierId") UUID supplierId, 
                      @Param("qualityRating") Integer qualityRating,
                      @Param("serviceRating") Integer serviceRating,
                      @Param("priceRating") Integer priceRating,
                      @Param("overallRating") Integer overallRating,
                      @Param("evaluationDate") LocalDateTime evaluationDate,
                      @Param("now") LocalDateTime now);
    
    /**
     * 设置为黑名单
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.isBlacklist = :isBlacklist, s.updatedAt = :now WHERE s.id = :supplierId")
    void setBlacklist(@Param("supplierId") UUID supplierId, @Param("isBlacklist") Boolean isBlacklist, @Param("now") LocalDateTime now);
    
    /**
     * 设置为重点供应商
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.isKeySupplier = :isKeySupplier, s.updatedAt = :now WHERE s.id = :supplierId")
    void setKeySupplier(@Param("supplierId") UUID supplierId, @Param("isKeySupplier") Boolean isKeySupplier, @Param("now") LocalDateTime now);
}
