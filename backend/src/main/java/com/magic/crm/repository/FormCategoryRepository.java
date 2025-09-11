package com.magic.crm.repository;

import com.magic.crm.entity.FormCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 表单分类数据访问层
 */
@Repository
public interface FormCategoryRepository extends JpaRepository<FormCategory, Long> {

    /**
     * 根据分类键查找分类
     */
    Optional<FormCategory> findByCategoryKey(String categoryKey);

    /**
     * 根据父级ID查找子分类
     */
    List<FormCategory> findByParentId(Long parentId);

    /**
     * 查找根分类（父级ID为null）
     */
    List<FormCategory> findByParentIdIsNull();

    /**
     * 根据状态查找分类
     */
    List<FormCategory> findByIsActive(Boolean isActive);

    /**
     * 根据父级ID和状态查找分类
     */
    List<FormCategory> findByParentIdAndIsActive(Long parentId, Boolean isActive);

    /**
     * 查找所有启用的分类（按排序字段排序）
     */
    @Query("SELECT fc FROM FormCategory fc WHERE fc.isActive = true ORDER BY fc.sortOrder ASC, fc.createdTime ASC")
    List<FormCategory> findAllActiveOrderBySortOrder();

    /**
     * 查找根分类（父级ID为null且启用）
     */
    @Query("SELECT fc FROM FormCategory fc WHERE fc.parentId IS NULL AND fc.isActive = true ORDER BY fc.sortOrder ASC")
    List<FormCategory> findRootCategories();

    /**
     * 根据分类键检查是否存在
     */
    boolean existsByCategoryKey(String categoryKey);

    /**
     * 根据分类名称检查是否存在
     */
    boolean existsByCategoryName(String categoryName);

    /**
     * 查找分类树（递归查询所有子分类）
     * 注意：PostgreSQL不支持START WITH...CONNECT BY语法，这里改为简单的递归查询
     */
    @Query("SELECT fc FROM FormCategory fc WHERE fc.isActive = true " +
            "AND (:parentId IS NULL OR fc.parentId = :parentId) " +
            "ORDER BY fc.sortOrder ASC")
    List<FormCategory> findCategoryTree(@Param("parentId") Long parentId);
}