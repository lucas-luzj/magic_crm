package com.magic.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.magic.crm.entity.FormTemplate;

import java.util.List;
import java.util.Optional;

/**
 * 表单模板数据访问层
 */
@Repository
public interface FormTemplateRepository extends JpaRepository<FormTemplate, Long> {

        /**
         * 根据表单键查找表单模板
         */
        Optional<FormTemplate> findByFormKey(String formKey);

        /**
         * 根据状态查找表单模板
         */
        List<FormTemplate> findByStatus(FormTemplate.FormStatus status);

        /**
         * 根据分类查找表单模板
         */
        List<FormTemplate> findByCategory(String category);

        /**
         * 根据分类和状态查找表单模板
         */
        List<FormTemplate> findByCategoryAndStatus(String category, FormTemplate.FormStatus status);

            /**
     * 分页查询表单模板（只返回主要字段，不加载关联数据）
     */
    @Query("SELECT ft FROM FormTemplate ft WHERE " +
            "(:formName IS NULL OR :formName='' OR ft.formName LIKE %:formName%) AND " +
            "(:category IS NULL OR :category='' OR ft.category = :category) AND " +
            "(:status IS NULL OR ft.status = :status)")
    Page<FormTemplate> findFormTemplatesWithFilters(
            @Param("formName") String formName,
            @Param("category") String category,
            @Param("status") FormTemplate.FormStatus status,
            Pageable pageable);

    /**
     * 根据ID查找表单模板（预加载formFields）
     */
    @Query("SELECT DISTINCT ft FROM FormTemplate ft LEFT JOIN FETCH ft.formFields WHERE ft.id = :id")
    Optional<FormTemplate> findByIdWithFields(@Param("id") Long id);

        /**
         * 检查表单键是否存在
         */
        boolean existsByFormKey(String formKey);

        /**
         * 根据创建者查找表单模板
         */
        List<FormTemplate> findByCreatedBy(String createdBy);

        /**
         * 获取所有分类
         */
        @Query("SELECT DISTINCT ft.category FROM FormTemplate ft WHERE ft.category IS NOT NULL")
        List<String> findAllCategories();

        /**
         * 根据标签查找表单模板
         */
        @Query("SELECT ft FROM FormTemplate ft WHERE ft.tags LIKE %:tag%")
        List<FormTemplate> findByTag(@Param("tag") String tag);
}