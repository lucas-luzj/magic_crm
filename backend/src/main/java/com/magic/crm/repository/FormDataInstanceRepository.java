package com.magic.crm.repository;

import com.magic.crm.entity.FormDataInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 表单数据实例数据访问层
 */
@Repository
public interface FormDataInstanceRepository extends JpaRepository<FormDataInstance, Long> {

    /**
     * 根据表单模板ID查找表单数据实例
     */
    List<FormDataInstance> findByFormTemplateId(Long formTemplateId);

    /**
     * 根据流程实例ID查找表单数据实例
     */
    List<FormDataInstance> findByProcessInstanceId(String processInstanceId);

    /**
     * 根据任务ID查找表单数据实例
     */
    List<FormDataInstance> findByTaskId(String taskId);

    /**
     * 根据业务键查找表单数据实例
     */
    List<FormDataInstance> findByBusinessKey(String businessKey);

    /**
     * 根据提交用户查找表单数据实例
     */
    List<FormDataInstance> findBySubmitUser(String submitUser);

    /**
     * 根据状态查找表单数据实例
     */
    List<FormDataInstance> findByStatus(FormDataInstance.FormDataStatus status);

    /**
     * 分页查询表单数据实例
     */
    @Query("SELECT fdi FROM FormDataInstance fdi WHERE " +
            "(:formTemplateId IS NULL OR fdi.formTemplateId = :formTemplateId) AND " +
            "(:processInstanceId IS NULL OR fdi.processInstanceId = :processInstanceId) AND " +
            "(:taskId IS NULL OR fdi.taskId = :taskId) AND " +
            "(:status IS NULL OR fdi.status = :status) AND " +
            "(:submitUser IS NULL OR fdi.submitUser = :submitUser)")
    Page<FormDataInstance> findFormDataInstancesWithFilters(
            @Param("formTemplateId") Long formTemplateId,
            @Param("processInstanceId") String processInstanceId,
            @Param("taskId") String taskId,
            @Param("status") FormDataInstance.FormDataStatus status,
            @Param("submitUser") String submitUser,
            Pageable pageable);

    /**
     * 根据表单模板ID和流程实例ID查找表单数据实例
     */
    Optional<FormDataInstance> findByFormTemplateIdAndProcessInstanceId(Long formTemplateId, String processInstanceId);

    /**
     * 根据表单模板ID和任务ID查找表单数据实例
     */
    Optional<FormDataInstance> findByFormTemplateIdAndTaskId(Long formTemplateId, String taskId);

    /**
     * 统计表单模板的使用次数
     */
    @Query("SELECT COUNT(fdi) FROM FormDataInstance fdi WHERE fdi.formTemplateId = :formTemplateId")
    Long countByFormTemplateId(@Param("formTemplateId") Long formTemplateId);

    /**
     * 统计流程定义的使用次数
     */
    @Query("SELECT COUNT(fdi) FROM FormDataInstance fdi WHERE fdi.processInstanceId IN " +
            "(SELECT pi.processInstanceId FROM ProcessInstance pi WHERE pi.processDefinitionKey = :processDefinitionKey)")
    Long countByProcessDefinitionKey(@Param("processDefinitionKey") String processDefinitionKey);
}
