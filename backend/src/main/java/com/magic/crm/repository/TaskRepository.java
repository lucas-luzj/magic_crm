package com.magic.crm.repository;

import com.magic.crm.entity.Task;
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
 * 任务Repository
 * 
 * @author Magic CRM Team
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * 根据任务ID查找
     */
    Optional<Task> findByTaskId(String taskId);

    /**
     * 根据流程实例ID查找任务
     */
    List<Task> findByProcessInstanceId(String processInstanceId);

    /**
     * 根据分配人查找任务
     */
    List<Task> findByAssignee(Long assignee);

    /**
     * 根据状态查找任务
     */
    List<Task> findByStatus(String status);

    /**
     * 根据分配人和状态查找任务
     */
    List<Task> findByAssigneeAndStatus(Long assignee, String status);

    /**
     * 分页查询任务
     */
    @Query("SELECT t FROM Task t WHERE " +
            "(:taskName IS NULL OR t.taskName LIKE %:taskName%) AND " +
            "(:assignee IS NULL OR t.assignee = :assignee) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:processInstanceId IS NULL OR t.processInstanceId = :processInstanceId)")
    Page<Task> findByConditions(
            @Param("taskName") String taskName,
            @Param("assignee") Long assignee,
            @Param("status") String status,
            @Param("processInstanceId") String processInstanceId,
            Pageable pageable);

    /**
     * 查找待办任务
     */
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.status IN ('CREATED', 'ASSIGNED')")
    List<Task> findPendingTasksByAssignee(@Param("assignee") Long assignee);

    /**
     * 查找已办任务
     */
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.status = 'COMPLETED'")
    List<Task> findCompletedTasksByAssignee(@Param("assignee") Long assignee);

    /**
     * 查找候选任务
     */
    @Query("SELECT t FROM Task t WHERE t.assignee IS NULL AND " +
            "(t.candidateUsers LIKE %:userId% OR t.candidateGroups LIKE %:groupId%)")
    List<Task> findCandidateTasks(@Param("userId") String userId, @Param("groupId") String groupId);

    /**
     * 根据到期时间查找任务
     */
    List<Task> findByDueDateBefore(LocalDateTime dueDate);

    /**
     * 根据优先级查找任务
     */
    List<Task> findByPriorityGreaterThanEqual(Integer priority);

    /**
     * 统计用户的任务数量
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.assignee = :assignee GROUP BY t.status")
    List<Object[]> countTasksByAssigneeAndStatus(@Param("assignee") Long assignee);

    /**
     * 检查任务是否存在
     */
    boolean existsByTaskId(String taskId);

    /**
     * 根据父任务ID查找子任务
     */
    List<Task> findByParentTaskId(String parentTaskId);

    /**
     * 根据任务定义Key查找任务
     */
    List<Task> findByTaskDefinitionKey(String taskDefinitionKey);
}