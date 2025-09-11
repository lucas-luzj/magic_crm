package com.magic.crm.repository;

import com.magic.crm.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统设置Repository
 */
@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    
    /**
     * 根据设置键查找设置
     */
    Optional<SystemSetting> findBySettingKey(String settingKey);
    
    /**
     * 根据设置分组查找设置列表
     */
    List<SystemSetting> findBySettingGroupOrderBySortOrderAsc(String settingGroup);
    
    /**
     * 检查设置键是否存在
     */
    boolean existsBySettingKey(String settingKey);
    
    /**
     * 根据设置键列表查找设置
     */
    @Query("SELECT s FROM SystemSetting s WHERE s.settingKey IN :keys")
    List<SystemSetting> findBySettingKeyIn(@Param("keys") List<String> keys);
    
    /**
     * 查找所有设置分组
     */
    @Query("SELECT DISTINCT s.settingGroup FROM SystemSetting s ORDER BY s.settingGroup")
    List<String> findDistinctSettingGroups();
    
    /**
     * 根据设置类型查找设置
     */
    List<SystemSetting> findBySettingTypeOrderBySortOrderAsc(SystemSetting.SettingType settingType);
    
    /**
     * 查找可编辑的设置
     */
    List<SystemSetting> findByIsEditableTrueOrderBySettingGroupAscSortOrderAsc();
    
    /**
     * 根据设置分组和类型查找设置
     */
    List<SystemSetting> findBySettingGroupAndSettingTypeOrderBySortOrderAsc(
            String settingGroup, SystemSetting.SettingType settingType);
}
