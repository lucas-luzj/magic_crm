package com.magic.crm.repository;

import com.magic.crm.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 产品数据访问层
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    
    /**
     * 根据产品编码查找
     */
    Optional<Product> findByCode(String code);
    
    /**
     * 根据产品名称查找
     */
    Optional<Product> findByName(String name);
    
    /**
     * 根据品牌查找
     */
    List<Product> findByBrand(String brand);
    
    /**
     * 根据型号查找
     */
    List<Product> findByModel(String model);
    
    /**
     * 根据分类查找
     */
    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);
    
    /**
     * 根据状态查找
     */
    List<Product> findByStatus(String status);
    
    /**
     * 查找备件产品
     */
    List<Product> findByIsSparePartTrue();
    
    /**
     * 查找套餐产品
     */
    List<Product> findByIsBundleTrue();
    
    /**
     * 查找可定制产品
     */
    List<Product> findByIsCustomizableTrue();
    
    /**
     * 检查产品编码唯一性
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.code = :code AND p.deleted = false")
    boolean existsByCode(@Param("code") String code);
    
    /**
     * 检查产品名称唯一性
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.name = :name AND p.deleted = false")
    boolean existsByName(@Param("name") String name);
    
    /**
     * 搜索产品（支持名称、编码、品牌、型号模糊搜索）
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.model) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND p.deleted = false")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据分类路径查找产品
     */
    @Query("SELECT p FROM Product p JOIN ProductCategory pc ON p.categoryId = pc.id " +
           "WHERE pc.path LIKE CONCAT(:path, '%') AND p.deleted = false")
    Page<Product> findByCategoryPath(@Param("path") String path, Pageable pageable);
    
    /**
     * 查找低库存产品
     */
    @Query("SELECT p FROM Product p JOIN Inventory i ON p.id = i.productId " +
           "WHERE i.availableQuantity <= i.minStockLevel AND p.deleted = false")
    List<Product> findLowStockProducts();
    
    /**
     * 查找缺货产品
     */
    @Query("SELECT p FROM Product p JOIN Inventory i ON p.id = i.productId " +
           "WHERE i.availableQuantity <= 0 AND p.deleted = false")
    List<Product> findOutOfStockProducts();
    
    /**
     * 统计产品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deleted = false")
    Long countActiveProducts();
    
    /**
     * 统计分类下的产品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.categoryId = :categoryId AND p.deleted = false")
    Long countByCategoryId(@Param("categoryId") UUID categoryId);
    
    /**
     * 软删除产品
     */
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true, p.updatedAt = :now WHERE p.id = :productId")
    void softDelete(@Param("productId") UUID productId, @Param("now") java.time.LocalDateTime now);
    
    /**
     * 批量软删除产品
     */
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true, p.updatedAt = :now WHERE p.id IN :productIds")
    void softDeleteBatch(@Param("productIds") List<UUID> productIds, @Param("now") java.time.LocalDateTime now);
    
    /**
     * 更新产品状态
     */
    @Modifying
    @Query("UPDATE Product p SET p.status = :status, p.updatedAt = :now WHERE p.id = :productId")
    void updateStatus(@Param("productId") UUID productId, @Param("status") String status, @Param("now") java.time.LocalDateTime now);
    
    /**
     * 批量更新产品状态
     */
    @Modifying
    @Query("UPDATE Product p SET p.status = :status, p.updatedAt = :now WHERE p.id IN :productIds")
    void updateStatusBatch(@Param("productIds") List<UUID> productIds, @Param("status") String status, @Param("now") java.time.LocalDateTime now);
}
