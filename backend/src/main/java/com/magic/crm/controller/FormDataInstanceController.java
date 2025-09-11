package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.FormDataInstance;
import com.magic.crm.service.FormDataInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 表单数据实例控制器
 */
@RestController
@RequestMapping("/api/form-data-instances")
public class FormDataInstanceController {

    @Autowired
    private FormDataInstanceService formDataInstanceService;

    /**
     * 分页查询表单数据实例
     */
    @GetMapping
    public ApiResponse<PageResponse<FormDataInstance>> getFormDataInstances(
            @RequestParam(required = false) Long formTemplateId,
            @RequestParam(required = false) String processInstanceId,
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String submitUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submitTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        FormDataInstance.FormDataStatus formDataStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                formDataStatus = FormDataInstance.FormDataStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ApiResponse.error("无效的状态值: " + status);
            }
        }

        Page<FormDataInstance> result = formDataInstanceService.findFormDataInstancesWithFilters(
                formTemplateId, processInstanceId, taskId, formDataStatus, submitUser, pageable);

        return ApiResponse.success(new PageResponse<>(result));
    }

    /**
     * 根据ID获取表单数据实例
     */
    @GetMapping("/{id}")
    public ApiResponse<FormDataInstance> getFormDataInstanceById(@PathVariable Long id) {
        Optional<FormDataInstance> formDataInstance = formDataInstanceService.findById(id);
        return formDataInstance.map(ApiResponse::success)
                .orElse(ApiResponse.error("表单数据实例不存在"));
    }

    /**
     * 根据表单模板ID获取表单数据实例
     */
    @GetMapping("/by-form-template/{formTemplateId}")
    public ApiResponse<List<FormDataInstance>> getFormDataInstancesByFormTemplate(@PathVariable Long formTemplateId) {
        List<FormDataInstance> formDataInstances = formDataInstanceService.findByFormTemplateId(formTemplateId);
        return ApiResponse.success(formDataInstances);
    }

    /**
     * 根据流程实例ID获取表单数据实例
     */
    @GetMapping("/by-process-instance/{processInstanceId}")
    public ApiResponse<List<FormDataInstance>> getFormDataInstancesByProcessInstance(
            @PathVariable String processInstanceId) {
        List<FormDataInstance> formDataInstances = formDataInstanceService.findByProcessInstanceId(processInstanceId);
        return ApiResponse.success(formDataInstances);
    }

    /**
     * 根据任务ID获取表单数据实例
     */
    @GetMapping("/by-task/{taskId}")
    public ApiResponse<List<FormDataInstance>> getFormDataInstancesByTask(@PathVariable String taskId) {
        List<FormDataInstance> formDataInstances = formDataInstanceService.findByTaskId(taskId);
        return ApiResponse.success(formDataInstances);
    }

    /**
     * 提交表单数据
     */
    @PostMapping("/submit")
    public ApiResponse<FormDataInstance> submitFormData(@RequestBody FormDataSubmitRequest request) {
        try {
            FormDataInstance formDataInstance = formDataInstanceService.submitFormData(
                    request.getFormTemplateId(),
                    request.getFormData(),
                    request.getSubmitUser(),
                    request.getProcessInstanceId(),
                    request.getTaskId(),
                    request.getBusinessKey());
            return ApiResponse.success("表单提交成功", formDataInstance);
        } catch (Exception e) {
            return ApiResponse.error("表单提交失败：" + e.getMessage());
        }
    }

    /**
     * 保存草稿
     */
    @PostMapping("/draft")
    public ApiResponse<FormDataInstance> saveDraft(@RequestBody FormDataSubmitRequest request) {
        try {
            FormDataInstance formDataInstance = formDataInstanceService.saveDraft(
                    request.getFormTemplateId(),
                    request.getFormData(),
                    request.getSubmitUser(),
                    request.getProcessInstanceId(),
                    request.getTaskId(),
                    request.getBusinessKey());
            return ApiResponse.success("草稿保存成功", formDataInstance);
        } catch (Exception e) {
            return ApiResponse.error("草稿保存失败：" + e.getMessage());
        }
    }

    /**
     * 审批表单数据
     */
    @PutMapping("/{id}/approve")
    public ApiResponse<FormDataInstance> approveFormData(@PathVariable Long id, @RequestParam String approver) {
        try {
            FormDataInstance formDataInstance = formDataInstanceService.approveFormData(id, approver);
            return ApiResponse.success("审批成功", formDataInstance);
        } catch (Exception e) {
            return ApiResponse.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 拒绝表单数据
     */
    @PutMapping("/{id}/reject")
    public ApiResponse<FormDataInstance> rejectFormData(@PathVariable Long id, @RequestParam String rejector) {
        try {
            FormDataInstance formDataInstance = formDataInstanceService.rejectFormData(id, rejector);
            return ApiResponse.success("拒绝成功", formDataInstance);
        } catch (Exception e) {
            return ApiResponse.error("拒绝失败：" + e.getMessage());
        }
    }

    /**
     * 删除表单数据实例
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFormDataInstance(@PathVariable Long id) {
        try {
            formDataInstanceService.deleteFormDataInstance(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取表单使用统计
     */
    @GetMapping("/statistics/form/{formTemplateId}")
    public ApiResponse<Long> getFormUsageCount(@PathVariable Long formTemplateId) {
        Long count = formDataInstanceService.getFormUsageCount(formTemplateId);
        return ApiResponse.success(count);
    }

    /**
     * 获取流程使用统计
     */
    @GetMapping("/statistics/process/{processDefinitionKey}")
    public ApiResponse<Long> getProcessUsageCount(@PathVariable String processDefinitionKey) {
        Long count = formDataInstanceService.getProcessUsageCount(processDefinitionKey);
        return ApiResponse.success(count);
    }

    /**
     * 表单数据提交请求
     */
    public static class FormDataSubmitRequest {
        private Long formTemplateId;
        private com.fasterxml.jackson.databind.JsonNode formData;
        private String submitUser;
        private String processInstanceId;
        private String taskId;
        private String businessKey;

        // Getters and Setters
        public Long getFormTemplateId() {
            return formTemplateId;
        }

        public void setFormTemplateId(Long formTemplateId) {
            this.formTemplateId = formTemplateId;
        }

        public com.fasterxml.jackson.databind.JsonNode getFormData() {
            return formData;
        }

        public void setFormData(com.fasterxml.jackson.databind.JsonNode formData) {
            this.formData = formData;
        }

        public String getSubmitUser() {
            return submitUser;
        }

        public void setSubmitUser(String submitUser) {
            this.submitUser = submitUser;
        }

        public String getProcessInstanceId() {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getBusinessKey() {
            return businessKey;
        }

        public void setBusinessKey(String businessKey) {
            this.businessKey = businessKey;
        }
    }
}
