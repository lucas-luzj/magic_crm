package com.magic.crm.repository;

import com.magic.crm.entity.FormUsageStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 表单使用统计数据访问层
 */
@Repository
public interface FormUsageStatisticsRepository extends JpaRepository<FormUsageStatistics, Long> {

    /**
     * 根据表单模板ID查找统计
     */
    Optional<FormUsageStatistics> findByFormTemplateId(Long formTemplateId);

    /**
     * 根据流程定义Key查找统计
     */
    List<FormUsageStatistics> findByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 根据表单模板ID和流程定义Key查找统计
     */
    Optional<FormUsageStatistics> findByFormTemplateIdAndProcessDefinitionKey(Long formTemplateId, String processDefinitionKey);

    /**
     * 查找使用次数最多的表单
     */
    @Query("SELECT fus FROM FormUsageStatistics fus ORDER BY fus.usageCount DESC")
    List<FormUsageStatistics> findTopUsedForms();

    /**
     * 查找最近使用的表单
     */
    @Query("SELECT fus FROM FormUsageStatistics fus WHERE fus.lastUsedTime IS NOT NULL ORDER BY fus.lastUsedTime DESC")
    List<FormUsageStatistics> findRecentlyUsedForms();

    /**
     * 统计表单总使用次数
     */
    @Query("SELECT SUM(fus.usageCount) FROM FormUsageStatistics fus WHERE fus.formTemplateId = :formTemplateId")
    Long getTotalUsageCountByFormTemplateId(@Param("formTemplateId") Long formTemplateId);

    /**
     * 统计流程总使用次数
     */
    @Query("SELECT SUM(fus.usageCount) FROM FormUsageStatistics fus WHERE fus.processDefinitionKey = :processDefinitionKey")
    Long getTotalUsageCountByProcessDefinitionKey(@Param("processDefinitionKey") String processDefinitionKey);

    /**
     * 查找未使用的表单
     */
    @Query("SELECT fus FROM FormUsageStatistics fus WHERE fus.usageCount = 0 OR fus.usageCount IS NULL")
    List<FormUsageStatistics> findUnusedForms();
}
