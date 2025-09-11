package com.magic.crm.controller;

import com.magic.crm.dto.ApiResponse;
import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.ProcessInstance;
import com.magic.crm.entity.User;
import com.magic.crm.service.ProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程实例管理控制器
 */
@RestController
@RequestMapping("/api/process-instances")
@RequiredArgsConstructor
public class ProcessInstanceController {

    private final ProcessInstanceService processInstanceService;

    /**
     * 根据流程定义Key启动流程实例
     */
    @PostMapping("/start")
    public ApiResponse<ProcessInstance> startProcessInstance(
            @RequestParam("processDefinitionKey") String processDefinitionKey,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables,
            @AuthenticationPrincipal User currentUser) {

        ProcessInstance processInstance = processInstanceService.startProcessInstance(
                processDefinitionKey, businessKey, variables, currentUser.getId());

        return ApiResponse.success(processInstance);
    }

    /**
     * 根据流程定义ID启动流程实例
     */
    @PostMapping("/start-by-id")
    public ApiResponse<ProcessInstance> startProcessInstanceById(
            @RequestParam("processDefinitionId") String processDefinitionId,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables,
            @AuthenticationPrincipal User currentUser) {

        ProcessInstance processInstance = processInstanceService.startProcessInstanceById(
                processDefinitionId, businessKey, variables, currentUser.getId());

        return ApiResponse.success(processInstance);
    }

    /**
     * 分页查询流程实例
     */
    @GetMapping
    public ApiResponse<PageResponse<ProcessInstance>> getProcessInstances(
            @RequestParam(value = "processName", required = false) String processName,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startUserId", required = false) Long startUserId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageResponse<ProcessInstance> pageResponse = processInstanceService.getProcessInstances(
                processName, businessKey, status, startUserId, page, size);

        return ApiResponse.success(pageResponse);
    }

    /**
     * 根据ID获取流程实例详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProcessInstance> getProcessInstanceById(@PathVariable Long id) {
        ProcessInstance processInstance = processInstanceService.getProcessInstanceById(id);
        return ApiResponse.success(processInstance);
    }

    /**
     * 暂停流程实例
     */
    @PutMapping("/{processInstanceId}/suspend")
    public ApiResponse<Void> suspendProcessInstance(@PathVariable String processInstanceId) {
        processInstanceService.suspendProcessInstance(processInstanceId);
        return ApiResponse.success("流程实例已暂停");
    }

    /**
     * 激活流程实例
     */
    @PutMapping("/{processInstanceId}/activate")
    public ApiResponse<Void> activateProcessInstance(@PathVariable String processInstanceId) {
        processInstanceService.activateProcessInstance(processInstanceId);
        return ApiResponse.success("流程实例已激活");
    }

    /**
     * 终止流程实例
     */
    @DeleteMapping("/{processInstanceId}")
    public ApiResponse<Void> terminateProcessInstance(
            @PathVariable String processInstanceId,
            @RequestParam(value = "reason", defaultValue = "手动终止") String reason) {

        processInstanceService.terminateProcessInstance(processInstanceId, reason);
        return ApiResponse.success("流程实例已终止");
    }

    /**
     * 获取流程实例的当前任务
     */
    @GetMapping("/{processInstanceId}/current-tasks")
    public ApiResponse<List<Map<String, Object>>> getCurrentTasks(@PathVariable String processInstanceId) {
        List<Map<String, Object>> tasks = processInstanceService.getCurrentTasks(processInstanceId);
        return ApiResponse.success(tasks);
    }

    /**
     * 获取流程实例的历史任务
     */
    @GetMapping("/{processInstanceId}/history-tasks")
    public ApiResponse<List<Map<String, Object>>> getHistoryTasks(@PathVariable String processInstanceId) {
        List<Map<String, Object>> tasks = processInstanceService.getHistoryTasks(processInstanceId);
        return ApiResponse.success(tasks);
    }

    /**
     * 设置流程变量
     */
    @PutMapping("/{processInstanceId}/variables/{variableName}")
    public ApiResponse<Void> setProcessVariable(
            @PathVariable String processInstanceId,
            @PathVariable String variableName,
            @RequestBody Object value) {

        processInstanceService.setProcessVariable(processInstanceId, variableName, value);
        return ApiResponse.success("流程变量设置成功");
    }

    /**
     * 获取流程变量
     */
    @GetMapping("/{processInstanceId}/variables")
    public ApiResponse<Map<String, Object>> getProcessVariables(@PathVariable String processInstanceId) {
        Map<String, Object> variables = processInstanceService.getProcessVariables(processInstanceId);
        return ApiResponse.success(variables);
    }

    /**
     * 获取当前用户的流程实例
     */
    @GetMapping("/my-instances")
    public ApiResponse<List<ProcessInstance>> getMyProcessInstances(@AuthenticationPrincipal User currentUser) {
        List<ProcessInstance> instances = processInstanceService.getUserProcessInstances(currentUser.getId());
        return ApiResponse.success(instances);
    }
}