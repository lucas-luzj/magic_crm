package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.DeployProcessRequest;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.ProcessDefinition;
import com.magic.crm.entity.User;
import com.magic.crm.service.ProcessDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 流程定义控制器
 * 
 * @author Magic CRM Team
 */
@RestController
@RequestMapping("/api/workflow/process-definitions")
public class ProcessDefinitionController {

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    /**
     * 部署流程定义（通过文件上传）
     */
    @PostMapping("/deploy")
    public ApiResponse<ProcessDefinition> deployProcess(
            @RequestParam("processName") String processName,
            @RequestParam("category") String category,
            @RequestParam("bpmnFile") MultipartFile bpmnFile,
            @AuthenticationPrincipal User currentUser) {

        ProcessDefinition processDefinition = processDefinitionService
                .deployProcess(processName, category, bpmnFile, currentUser.getId());

        return ApiResponse.success("流程部署成功", processDefinition);
    }

    /**
     * 部署流程定义（通过XML内容）
     */
    @PostMapping("/deploy-xml")
    public ApiResponse<ProcessDefinition> deployProcessFromXml(
            @RequestBody DeployProcessRequest request,
            @AuthenticationPrincipal User currentUser) {

        ProcessDefinition processDefinition = processDefinitionService
                .deployProcessFromXml(request.getProcessName(), request.getCategory(),
                        request.getBpmnXml(), request.getFileName(), currentUser.getId());

        return ApiResponse.success("流程部署成功", processDefinition);
    }

    /**
     * 分页查询流程定义
     */
    @GetMapping
    public ApiResponse<PageResponse<ProcessDefinition>> getProcessDefinitions(
            @RequestParam(value = "processName", required = false) String processName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageResponse<ProcessDefinition> pageResponse = processDefinitionService
                .getProcessDefinitions(processName, category, status, page, size);

        return ApiResponse.success(pageResponse);
    }

    /**
     * 根据ID获取流程定义详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProcessDefinition> getProcessDefinitionById(@PathVariable Long id) {
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinitionById(id);
        return ApiResponse.success(processDefinition);
    }

    /**
     * 根据流程定义ID获取流程定义详情
     */
    @GetMapping("/by-process-def-id/{processDefinitionId}")
    public ApiResponse<ProcessDefinition> getProcessDefinitionByProcessDefId(
            @PathVariable String processDefinitionId) {
        ProcessDefinition processDefinition = processDefinitionService
                .getProcessDefinitionByProcessDefId(processDefinitionId);
        return ApiResponse.success(processDefinition);
    }

    /**
     * 激活流程定义
     */
    @PostMapping("/{processDefinitionId}/activate")
    public ApiResponse<Void> activateProcessDefinition(@PathVariable String processDefinitionId) {
        processDefinitionService.activateProcessDefinition(processDefinitionId);
        return ApiResponse.success("流程定义激活成功");
    }

    /**
     * 挂起流程定义
     */
    @PostMapping("/{processDefinitionId}/suspend")
    public ApiResponse<Void> suspendProcessDefinition(@PathVariable String processDefinitionId) {
        processDefinitionService.suspendProcessDefinition(processDefinitionId);
        return ApiResponse.success("流程定义挂起成功");
    }

    /**
     * 删除流程定义
     */
    @DeleteMapping("/{processDefinitionId}")
    public ApiResponse<Void> deleteProcessDefinition(
            @PathVariable String processDefinitionId,
            @RequestParam(value = "cascade", defaultValue = "false") boolean cascade) {
        processDefinitionService.deleteProcessDefinition(processDefinitionId, cascade);
        return ApiResponse.success("流程定义删除成功");
    }

    /**
     * 获取流程定义的BPMN XML
     */
    @GetMapping("/{processDefinitionId}/xml")
    public ResponseEntity<String> getProcessDefinitionBpmnXml(@PathVariable String processDefinitionId) {
        String bpmnXml = processDefinitionService.getProcessDefinitionBpmnXml(processDefinitionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .body(bpmnXml);
    }

    /**
     * 获取流程定义的流程图
     */
    @GetMapping("/{processDefinitionId}/diagram")
    public ResponseEntity<byte[]> getProcessDefinitionDiagram(@PathVariable String processDefinitionId) {
        byte[] diagram = processDefinitionService.getProcessDefinitionDiagram(processDefinitionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(diagram);
    }

    /**
     * 根据流程Key获取最新版本的流程定义
     */
    @GetMapping("/latest/{processKey}")
    public ApiResponse<ProcessDefinition> getLatestProcessDefinitionByKey(@PathVariable String processKey) {
        ProcessDefinition processDefinition = processDefinitionService
                .getLatestProcessDefinitionByKey(processKey);
        return ApiResponse.success(processDefinition);
    }

    /**
     * 获取所有流程定义
     */
    @GetMapping("/all")
    public ApiResponse<List<ProcessDefinition>> getAllProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = processDefinitionService.getAllProcessDefinitions();
        return ApiResponse.success(processDefinitions);
    }

    /**
     * 根据分类获取流程定义
     */
    @GetMapping("/by-category/{category}")
    public ApiResponse<List<ProcessDefinition>> getProcessDefinitionsByCategory(@PathVariable String category) {
        List<ProcessDefinition> processDefinitions = processDefinitionService
                .getProcessDefinitionsByCategory(category);
        return ApiResponse.success(processDefinitions);
    }

    /**
     * 检查流程定义是否存在
     */
    @GetMapping("/{processDefinitionId}/exists")
    public ApiResponse<Boolean> existsProcessDefinition(@PathVariable String processDefinitionId) {
        boolean exists = processDefinitionService.existsProcessDefinition(processDefinitionId);
        return ApiResponse.success(exists);
    }

    /**
     * 保存流程定义的表单配置
     */
    @PostMapping("/{processDefinitionId}/form-config")
    public ApiResponse<Void> saveFormConfig(
            @PathVariable String processDefinitionId,
            @RequestBody String formConfig) {
        processDefinitionService.saveFormConfig(processDefinitionId, formConfig);
        return ApiResponse.success("表单配置保存成功");
    }

    /**
     * 获取流程定义的表单配置
     */
    @GetMapping("/{processDefinitionId}/form-config")
    public ApiResponse<String> getFormConfig(@PathVariable String processDefinitionId) {
        String formConfig = processDefinitionService.getFormConfig(processDefinitionId);
        return ApiResponse.success(formConfig);
    }

    /**
     * 根据流程定义ID更新表单配置
     */
    @PutMapping("/{id}/form-config")
    public ApiResponse<ProcessDefinition> updateFormConfig(
            @PathVariable Long id,
            @RequestBody String formConfig) {
        ProcessDefinition processDefinition = processDefinitionService.updateFormConfig(id, formConfig);
        return ApiResponse.success("表单配置更新成功", processDefinition);
    }
}