package com.magic.crm.repository;

import com.magic.crm.entity.ProcessFormMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 流程表单映射数据访问层
 */
@Repository
public interface ProcessFormMappingRepository extends JpaRepository<ProcessFormMapping, Long> {

    /**
     * 根据流程定义Key查找映射
     */
    List<ProcessFormMapping> findByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 根据任务定义Key查找映射
     */
    List<ProcessFormMapping> findByTaskDefinitionKey(String taskDefinitionKey);

    /**
     * 根据表单模板ID查找映射
     */
    List<ProcessFormMapping> findByFormTemplateId(Long formTemplateId);

    /**
     * 根据流程定义Key和任务定义Key查找映射
     */
    List<ProcessFormMapping> findByProcessDefinitionKeyAndTaskDefinitionKey(String processDefinitionKey, String taskDefinitionKey);

    /**
     * 根据流程定义Key和映射类型查找映射
     */
    List<ProcessFormMapping> findByProcessDefinitionKeyAndMappingType(String processDefinitionKey, ProcessFormMapping.MappingType mappingType);

    /**
     * 查找默认表单映射
     */
    @Query("SELECT pfm FROM ProcessFormMapping pfm WHERE " +
            "pfm.processDefinitionKey = :processDefinitionKey AND " +
            "(:taskDefinitionKey IS NULL OR pfm.taskDefinitionKey = :taskDefinitionKey) AND " +
            "pfm.isDefault = true")
    Optional<ProcessFormMapping> findDefaultFormMapping(
            @Param("processDefinitionKey") String processDefinitionKey,
            @Param("taskDefinitionKey") String taskDefinitionKey);

    /**
     * 检查是否存在默认表单映射
     */
    @Query("SELECT COUNT(pfm) > 0 FROM ProcessFormMapping pfm WHERE " +
            "pfm.processDefinitionKey = :processDefinitionKey AND " +
            "(:taskDefinitionKey IS NULL OR pfm.taskDefinitionKey = :taskDefinitionKey) AND " +
            "pfm.isDefault = true")
    boolean existsDefaultFormMapping(
            @Param("processDefinitionKey") String processDefinitionKey,
            @Param("taskDefinitionKey") String taskDefinitionKey);

    /**
     * 根据流程定义Key查找所有映射（按任务定义Key排序）
     */
    @Query("SELECT pfm FROM ProcessFormMapping pfm WHERE pfm.processDefinitionKey = :processDefinitionKey " +
            "ORDER BY pfm.taskDefinitionKey ASC, pfm.createdTime ASC")
    List<ProcessFormMapping> findByProcessDefinitionKeyOrderByTaskDefinitionKey(@Param("processDefinitionKey") String processDefinitionKey);
}
