package com.magic.crm.repository;

import com.magic.crm.entity.ProductCategory;
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
 * 产品分类数据访问层
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID>, JpaSpecificationExecutor<ProductCategory> {
    
    /**
     * 根据分类编码查找
     */
    Optional<ProductCategory> findByCode(String code);
    
    /**
     * 根据分类名称查找
     */
    Optional<ProductCategory> findByName(String name);
    
    /**
     * 查找根分类
     */
    List<ProductCategory> findByParentIdIsNull();
    
    /**
     * 根据父分类查找子分类
     */
    List<ProductCategory> findByParentId(UUID parentId);
    
    /**
     * 根据层级查找分类
     */
    List<ProductCategory> findByLevel(Integer level);
    
    /**
     * 根据状态查找分类
     */
    List<ProductCategory> findByStatus(String status);
    
    /**
     * 检查分类编码唯一性
     */
    @Query("SELECT COUNT(pc) > 0 FROM ProductCategory pc WHERE pc.code = :code AND pc.deleted = false")
    boolean existsByCode(@Param("code") String code);
    
    /**
     * 检查分类名称唯一性
     */
    @Query("SELECT COUNT(pc) > 0 FROM ProductCategory pc WHERE pc.name = :name AND pc.deleted = false")
    boolean existsByName(@Param("name") String name);
    
    /**
     * 搜索分类（支持名称、编码模糊搜索）
     */
    @Query("SELECT pc FROM ProductCategory pc WHERE " +
           "(LOWER(pc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(pc.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND pc.deleted = false")
    Page<ProductCategory> searchCategories(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据路径查找分类
     */
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.path LIKE CONCAT(:path, '%') AND pc.deleted = false")
    List<ProductCategory> findByPath(@Param("path") String path);
    
    /**
     * 查找分类树（按层级和排序）
     */
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.deleted = false ORDER BY pc.level, pc.sortOrder, pc.name")
    List<ProductCategory> findCategoryTree();
    
    /**
     * 统计分类数量
     */
    @Query("SELECT COUNT(pc) FROM ProductCategory pc WHERE pc.deleted = false")
    Long countActiveCategories();
    
    /**
     * 统计父分类下的子分类数量
     */
    @Query("SELECT COUNT(pc) FROM ProductCategory pc WHERE pc.parentId = :parentId AND pc.deleted = false")
    Long countByParentId(@Param("parentId") UUID parentId);
    
    /**
     * 软删除分类
     */
    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.deleted = true, pc.updatedAt = :now WHERE pc.id = :categoryId")
    void softDelete(@Param("categoryId") UUID categoryId, @Param("now") java.time.LocalDateTime now);
    
    /**
     * 批量软删除分类
     */
    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.deleted = true, pc.updatedAt = :now WHERE pc.id IN :categoryIds")
    void softDeleteBatch(@Param("categoryIds") List<UUID> categoryIds, @Param("now") java.time.LocalDateTime now);
    
    /**
     * 更新分类状态
     */
    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.status = :status, pc.updatedAt = :now WHERE pc.id = :categoryId")
    void updateStatus(@Param("categoryId") UUID categoryId, @Param("status") String status, @Param("now") java.time.LocalDateTime now);
    
    /**
     * 更新分类路径
     */
    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.path = :path, pc.updatedAt = :now WHERE pc.id = :categoryId")
    void updatePath(@Param("categoryId") UUID categoryId, @Param("path") String path, @Param("now") java.time.LocalDateTime now);
}
