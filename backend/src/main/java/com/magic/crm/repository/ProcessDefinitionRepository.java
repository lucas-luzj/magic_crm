package com.magic.crm.repository;

import com.magic.crm.entity.ProcessDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 流程定义Repository
 * 
 * @author Magic CRM Team
 */
@Repository
public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinition, Long> {

        /**
         * 根据流程定义ID查找
         */
        Optional<ProcessDefinition> findByProcessDefinitionId(String processDefinitionId);

        /**
         * 根据流程Key查找
         */
        List<ProcessDefinition> findByProcessKey(String processKey);

        /**
         * 根据流程Key和版本查找
         */
        Optional<ProcessDefinition> findByProcessKeyAndVersion(String processKey, Integer version);

        /**
         * 根据状态查找
         */
        List<ProcessDefinition> findByStatus(String status);

        /**
         * 根据分类查找
         */
        List<ProcessDefinition> findByCategory(String category);

        /**
         * 根据创建人查找
         */
        List<ProcessDefinition> findByCreatedBy(Long createdBy);

        /**
         * 分页查询流程定义
         */
        @Query("SELECT pd FROM ProcessDefinition pd WHERE " +
                        "(:processName IS NULL OR :processName = '' OR pd.processName LIKE %:processName%) AND " +
                        "(:category IS NULL OR :category = '' OR pd.category = :category) AND " +
                        "(:status IS NULL OR :status = '' OR pd.status = :status)")
        Page<ProcessDefinition> findByConditions(
                        @Param("processName") String processName,
                        @Param("category") String category,
                        @Param("status") String status,
                        Pageable pageable);

        /**
         * 根据流程Key获取最新版本
         */
        @Query("SELECT pd FROM ProcessDefinition pd WHERE pd.processKey = :processKey " +
                        "ORDER BY pd.version DESC")
        List<ProcessDefinition> findLatestByProcessKey(@Param("processKey") String processKey);

        /**
         * 检查流程定义是否存在
         */
        boolean existsByProcessDefinitionId(String processDefinitionId);

        /**
         * 根据部署ID查找
         */
        List<ProcessDefinition> findByDeploymentId(String deploymentId);
}