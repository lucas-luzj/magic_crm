package com.magic.crm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.crm.entity.FormDataInstance;
import com.magic.crm.repository.FormDataInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 表单数据实例服务层
 */
@Service
@Transactional
public class FormDataInstanceService {

    @Autowired
    private FormDataInstanceRepository formDataInstanceRepository;

    /**
     * 创建表单数据实例
     */
    public FormDataInstance createFormDataInstance(FormDataInstance formDataInstance) {
        formDataInstance.setSubmitTime(LocalDateTime.now());
        return formDataInstanceRepository.save(formDataInstance);
    }

    /**
     * 更新表单数据实例
     */
    public FormDataInstance updateFormDataInstance(Long id, FormDataInstance formDataInstance) {
        FormDataInstance existingInstance = formDataInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单数据实例不存在: " + id));

        existingInstance.setFormData(formDataInstance.getFormData());
        existingInstance.setStatus(formDataInstance.getStatus());
        existingInstance.setSubmitUser(formDataInstance.getSubmitUser());

        return formDataInstanceRepository.save(existingInstance);
    }

    /**
     * 删除表单数据实例
     */
    public void deleteFormDataInstance(Long id) {
        if (!formDataInstanceRepository.existsById(id)) {
            throw new RuntimeException("表单数据实例不存在: " + id);
        }
        formDataInstanceRepository.deleteById(id);
    }

    /**
     * 根据ID查找表单数据实例
     */
    public Optional<FormDataInstance> findById(Long id) {
        return formDataInstanceRepository.findById(id);
    }

    /**
     * 根据表单模板ID查找表单数据实例
     */
    public List<FormDataInstance> findByFormTemplateId(Long formTemplateId) {
        return formDataInstanceRepository.findByFormTemplateId(formTemplateId);
    }

    /**
     * 根据流程实例ID查找表单数据实例
     */
    public List<FormDataInstance> findByProcessInstanceId(String processInstanceId) {
        return formDataInstanceRepository.findByProcessInstanceId(processInstanceId);
    }

    /**
     * 根据任务ID查找表单数据实例
     */
    public List<FormDataInstance> findByTaskId(String taskId) {
        return formDataInstanceRepository.findByTaskId(taskId);
    }

    /**
     * 根据业务键查找表单数据实例
     */
    public List<FormDataInstance> findByBusinessKey(String businessKey) {
        return formDataInstanceRepository.findByBusinessKey(businessKey);
    }

    /**
     * 分页查询表单数据实例
     */
    public Page<FormDataInstance> findFormDataInstancesWithFilters(
            Long formTemplateId, String processInstanceId, String taskId,
            FormDataInstance.FormDataStatus status, String submitUser, Pageable pageable) {
        return formDataInstanceRepository.findFormDataInstancesWithFilters(
                formTemplateId, processInstanceId, taskId, status, submitUser, pageable);
    }

    /**
     * 提交表单数据
     */
    public FormDataInstance submitFormData(Long formTemplateId, JsonNode formData, String submitUser,
                                         String processInstanceId, String taskId, String businessKey) {
        FormDataInstance formDataInstance = new FormDataInstance();
        formDataInstance.setFormTemplateId(formTemplateId);
        formDataInstance.setFormData(formData);
        formDataInstance.setSubmitUser(submitUser);
        formDataInstance.setProcessInstanceId(processInstanceId);
        formDataInstance.setTaskId(taskId);
        formDataInstance.setBusinessKey(businessKey);
        formDataInstance.setStatus(FormDataInstance.FormDataStatus.SUBMITTED);

        return createFormDataInstance(formDataInstance);
    }

    /**
     * 保存草稿
     */
    public FormDataInstance saveDraft(Long formTemplateId, JsonNode formData, String submitUser,
                                    String processInstanceId, String taskId, String businessKey) {
        FormDataInstance formDataInstance = new FormDataInstance();
        formDataInstance.setFormTemplateId(formTemplateId);
        formDataInstance.setFormData(formData);
        formDataInstance.setSubmitUser(submitUser);
        formDataInstance.setProcessInstanceId(processInstanceId);
        formDataInstance.setTaskId(taskId);
        formDataInstance.setBusinessKey(businessKey);
        formDataInstance.setStatus(FormDataInstance.FormDataStatus.DRAFT);

        return createFormDataInstance(formDataInstance);
    }

    /**
     * 审批表单数据
     */
    public FormDataInstance approveFormData(Long id, String approver) {
        FormDataInstance formDataInstance = formDataInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单数据实例不存在: " + id));

        formDataInstance.setStatus(FormDataInstance.FormDataStatus.APPROVED);
        return formDataInstanceRepository.save(formDataInstance);
    }

    /**
     * 拒绝表单数据
     */
    public FormDataInstance rejectFormData(Long id, String rejector) {
        FormDataInstance formDataInstance = formDataInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("表单数据实例不存在: " + id));

        formDataInstance.setStatus(FormDataInstance.FormDataStatus.REJECTED);
        return formDataInstanceRepository.save(formDataInstance);
    }

    /**
     * 统计表单使用次数
     */
    public Long getFormUsageCount(Long formTemplateId) {
        return formDataInstanceRepository.countByFormTemplateId(formTemplateId);
    }

    /**
     * 统计流程使用次数
     */
    public Long getProcessUsageCount(String processDefinitionKey) {
        return formDataInstanceRepository.countByProcessDefinitionKey(processDefinitionKey);
    }
}
