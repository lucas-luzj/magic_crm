package com.magic.crm.repository;

import com.magic.crm.entity.ProcessInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 流程实例Repository
 * 
 * @author Magic CRM Team
 */
@Repository
public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, Long> {

        /**
         * 根据流程实例ID查找
         */
        Optional<ProcessInstance> findByProcessInstanceId(String processInstanceId);

        /**
         * 根据流程定义ID查找
         */
        List<ProcessInstance> findByProcessDefinitionId(String processDefinitionId);

        /**
         * 根据流程定义Key查找
         */
        List<ProcessInstance> findByProcessDefinitionKey(String processDefinitionKey);

        /**
         * 根据业务Key查找
         */
        Optional<ProcessInstance> findByBusinessKey(String businessKey);

        /**
         * 根据启动人查找
         */
        List<ProcessInstance> findByStartedBy(Long startedBy);

        /**
         * 根据状态查找
         */
        List<ProcessInstance> findByStatus(String status);

        /**
         * 分页查询流程实例（原方法保留）
         */
        @Query("SELECT pi FROM ProcessInstance pi " +
                        "LEFT JOIN FETCH pi.processDefinition " +
                        "LEFT JOIN FETCH pi.startedByUser " +
                        "WHERE " +
                        "(:processInstanceName IS NULL OR :processInstanceName = '' OR pi.processInstanceName LIKE %:processInstanceName%) AND "
                        +
                        "(:processDefinitionKey IS NULL OR :processDefinitionKey = '' OR pi.processDefinitionKey = :processDefinitionKey) AND "
                        +
                        "(:status IS NULL OR :status = '' OR pi.status = :status) AND " +
                        "(:startedBy IS NULL OR pi.startedBy = :startedBy)")
        Page<ProcessInstance> findByConditions(
                        @Param("processInstanceName") String processInstanceName,
                        @Param("processDefinitionKey") String processDefinitionKey,
                        @Param("status") String status,
                        @Param("startedBy") Long startedBy,
                        Pageable pageable);

        /**
         * 根据时间范围查找
         */
        List<ProcessInstance> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

        /**
         * 查找运行中的流程实例
         */
        @Query("SELECT pi FROM ProcessInstance pi WHERE pi.status = 'ACTIVE' AND pi.endTime IS NULL")
        List<ProcessInstance> findActiveInstances();

        /**
         * 查找已完成的流程实例
         */
        @Query("SELECT pi FROM ProcessInstance pi WHERE pi.status = 'COMPLETED' AND pi.endTime IS NOT NULL")
        List<ProcessInstance> findCompletedInstances();

        /**
         * 统计各状态的流程实例数量
         */
        @Query("SELECT pi.status, COUNT(pi) FROM ProcessInstance pi GROUP BY pi.status")
        List<Object[]> countByStatus();

        /**
         * 检查流程实例是否存在
         */
        boolean existsByProcessInstanceId(String processInstanceId);

        /**
         * 根据父流程实例ID查找子流程
         */
        List<ProcessInstance> findBySuperProcessInstanceId(String superProcessInstanceId);

        /**
         * 根据启动用户ID查找流程实例，按启动时间倒序
         */
        List<ProcessInstance> findByStartedByOrderByStartTimeDesc(Long startedBy);
}