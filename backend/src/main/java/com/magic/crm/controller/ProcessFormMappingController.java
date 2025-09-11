package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.entity.ProcessFormMapping;
import com.magic.crm.service.ProcessFormMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 流程表单映射控制器
 */
@RestController
@RequestMapping("/api/process-form-mappings")
public class ProcessFormMappingController {

    @Autowired
    private ProcessFormMappingService processFormMappingService;

    /**
     * 创建流程表单映射
     */
    @PostMapping
    public ApiResponse<ProcessFormMapping> createProcessFormMapping(@RequestBody ProcessFormMapping mapping) {
        try {
            ProcessFormMapping savedMapping = processFormMappingService.createProcessFormMapping(mapping);
            return ApiResponse.success("创建流程表单映射成功", savedMapping);
        } catch (Exception e) {
            return ApiResponse.error("创建流程表单映射失败：" + e.getMessage());
        }
    }

    /**
     * 更新流程表单映射
     */
    @PutMapping("/{id}")
    public ApiResponse<ProcessFormMapping> updateProcessFormMapping(
            @PathVariable Long id,
            @RequestBody ProcessFormMapping mapping) {
        try {
            ProcessFormMapping updatedMapping = processFormMappingService.updateProcessFormMapping(id, mapping);
            return ApiResponse.success("更新流程表单映射成功", updatedMapping);
        } catch (Exception e) {
            return ApiResponse.error("更新流程表单映射失败：" + e.getMessage());
        }
    }

    /**
     * 删除流程表单映射
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProcessFormMapping(@PathVariable Long id) {
        try {
            processFormMappingService.deleteProcessFormMapping(id);
            return ApiResponse.success("删除流程表单映射成功");
        } catch (Exception e) {
            return ApiResponse.error("删除流程表单映射失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取流程表单映射
     */
    @GetMapping("/{id}")
    public ApiResponse<ProcessFormMapping> getProcessFormMappingById(@PathVariable Long id) {
        Optional<ProcessFormMapping> mapping = processFormMappingService.findById(id);
        return mapping.map(ApiResponse::success)
                .orElse(ApiResponse.error("流程表单映射不存在"));
    }

    /**
     * 根据流程定义Key获取映射
     */
    @GetMapping("/by-process/{processDefinitionKey}")
    public ApiResponse<List<ProcessFormMapping>> getMappingsByProcessDefinition(
            @PathVariable String processDefinitionKey) {
        List<ProcessFormMapping> mappings = processFormMappingService.findByProcessDefinitionKey(processDefinitionKey);
        return ApiResponse.success(mappings);
    }

    /**
     * 根据任务定义Key获取映射
     */
    @GetMapping("/by-task/{taskDefinitionKey}")
    public ApiResponse<List<ProcessFormMapping>> getMappingsByTaskDefinition(@PathVariable String taskDefinitionKey) {
        List<ProcessFormMapping> mappings = processFormMappingService.findByTaskDefinitionKey(taskDefinitionKey);
        return ApiResponse.success(mappings);
    }

    /**
     * 根据表单模板ID获取映射
     */
    @GetMapping("/by-form-template/{formTemplateId}")
    public ApiResponse<List<ProcessFormMapping>> getMappingsByFormTemplate(@PathVariable Long formTemplateId) {
        List<ProcessFormMapping> mappings = processFormMappingService.findByFormTemplateId(formTemplateId);
        return ApiResponse.success(mappings);
    }

    /**
     * 获取默认表单映射
     */
    @GetMapping("/default")
    public ApiResponse<ProcessFormMapping> getDefaultFormMapping(
            @RequestParam String processDefinitionKey,
            @RequestParam(required = false) String taskDefinitionKey) {
        Optional<ProcessFormMapping> mapping = processFormMappingService.findDefaultFormMapping(processDefinitionKey,
                taskDefinitionKey);
        return mapping.map(ApiResponse::success)
                .orElse(ApiResponse.error("未找到默认表单映射"));
    }

    /**
     * 根据流程定义Key和任务定义Key获取映射
     */
    @GetMapping("/by-process-and-task")
    public ApiResponse<List<ProcessFormMapping>> getMappingsByProcessAndTask(
            @RequestParam String processDefinitionKey,
            @RequestParam String taskDefinitionKey) {
        List<ProcessFormMapping> mappings = processFormMappingService
                .findByProcessDefinitionKeyAndTaskDefinitionKey(processDefinitionKey, taskDefinitionKey);
        return ApiResponse.success(mappings);
    }

    /**
     * 根据流程定义Key和映射类型获取映射
     */
    @GetMapping("/by-process-and-type")
    public ApiResponse<List<ProcessFormMapping>> getMappingsByProcessAndType(
            @RequestParam String processDefinitionKey,
            @RequestParam String mappingType) {
        try {
            ProcessFormMapping.MappingType type = ProcessFormMapping.MappingType.valueOf(mappingType.toUpperCase());
            List<ProcessFormMapping> mappings = processFormMappingService
                    .findByProcessDefinitionKeyAndMappingType(processDefinitionKey, type);
            return ApiResponse.success(mappings);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("无效的映射类型: " + mappingType);
        }
    }

    /**
     * 批量创建流程表单映射
     */
    @PostMapping("/batch")
    public ApiResponse<List<ProcessFormMapping>> createBatchMappings(@RequestBody List<ProcessFormMapping> mappings) {
        try {
            List<ProcessFormMapping> savedMappings = processFormMappingService.createBatchMappings(mappings);
            return ApiResponse.success("批量创建流程表单映射成功", savedMappings);
        } catch (Exception e) {
            return ApiResponse.error("批量创建流程表单映射失败：" + e.getMessage());
        }
    }

    /**
     * 根据流程定义Key删除所有映射
     */
    @DeleteMapping("/by-process/{processDefinitionKey}")
    public ApiResponse<Void> deleteMappingsByProcessDefinition(@PathVariable String processDefinitionKey) {
        try {
            processFormMappingService.deleteByProcessDefinitionKey(processDefinitionKey);
            return ApiResponse.success("删除流程表单映射成功");
        } catch (Exception e) {
            return ApiResponse.error("删除流程表单映射失败：" + e.getMessage());
        }
    }

    /**
     * 根据表单模板ID删除所有映射
     */
    @DeleteMapping("/by-form-template/{formTemplateId}")
    public ApiResponse<Void> deleteMappingsByFormTemplate(@PathVariable Long formTemplateId) {
        try {
            processFormMappingService.deleteByFormTemplateId(formTemplateId);
            return ApiResponse.success("删除流程表单映射成功");
        } catch (Exception e) {
            return ApiResponse.error("删除流程表单映射失败：" + e.getMessage());
        }
    }
}
