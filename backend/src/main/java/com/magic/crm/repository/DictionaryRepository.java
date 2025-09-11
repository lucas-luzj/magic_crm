package com.magic.crm.repository;

import com.magic.crm.entity.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 字典表数据访问层
 */
@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

        /**
         * 根据字典类型查找字典项
         */
        List<Dictionary> findByDictTypeAndIsActiveTrueOrderBySortOrderAsc(String dictType);

        /**
         * 根据字典键查找字典项
         */
        Optional<Dictionary> findByDictKey(String dictKey);

        /**
         * 根据字典类型和字典键查找字典项
         */
        Optional<Dictionary> findByDictTypeAndDictKey(String dictType, String dictKey);

        /**
         * 检查字典键是否存在
         */
        boolean existsByDictKey(String dictKey);

        /**
         * 检查字典类型和字典键是否存在
         */
        boolean existsByDictTypeAndDictKey(String dictType, String dictKey);

        /**
         * 分页查询字典项
         */
        @Query("SELECT d FROM Dictionary d WHERE " +
                        "(:dictType IS NULL OR :dictType='' OR d.dictType = :dictType) AND " +
                        "(:dictName IS NULL OR :dictName='' OR d.dictName LIKE %:dictName%) AND " +
                        "(:isSystem IS NULL OR d.isSystem = :isSystem) AND " +
                        "(:isActive IS NULL OR d.isActive = :isActive)")
        Page<Dictionary> findDictionariesWithFilters(
                        @Param("dictType") String dictType,
                        @Param("dictName") String dictName,
                        @Param("isSystem") Boolean isSystem,
                        @Param("isActive") Boolean isActive,
                        Pageable pageable);

        /**
         * 获取所有字典类型
         */
        @Query("SELECT DISTINCT d.dictType FROM Dictionary d WHERE d.isActive = true ORDER BY d.dictType")
        List<String> findAllDictTypes();

        /**
         * 根据字典类型获取字典项（用于下拉选择）
         */
        @Query("SELECT d FROM Dictionary d WHERE d.dictType = :dictType AND d.isActive = true ORDER BY d.sortOrder ASC")
        List<Dictionary> findActiveByDictType(@Param("dictType") String dictType);

        /**
         * 根据创建者查找字典项
         */
        List<Dictionary> findByCreatedBy(String createdBy);

        /**
         * 查找系统字典项
         */
        List<Dictionary> findByIsSystemTrue();

        /**
         * 查找用户字典项
         */
        List<Dictionary> findByIsSystemFalse();
}
