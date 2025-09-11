package com.magic.crm.controller;

import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.User;
import com.magic.crm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务管理控制器
 */
@RestController
@RequestMapping("/api/workflow/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 获取待办任务列表
     */
    @GetMapping("/pending")
    public ResponseEntity<PageResponse<Map<String, Object>>> getPendingTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<Map<String, Object>> result = taskService.getPendingTasks(currentUser.getId(), taskName,
                processDefinitionKey,
                page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取已完成任务列表
     */
    @GetMapping("/completed")
    public ResponseEntity<PageResponse<Map<String, Object>>> getCompletedTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<Map<String, Object>> result = taskService.getCompletedTasks(currentUser.getId(), taskName,
                processDefinitionKey,
                page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 完成任务
     */
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {

        taskService.completeTask(taskId, variables, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 分配任务
     */
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<Void> assignTask(

            @PathVariable String taskId,
            @RequestParam String assignee,
            @RequestParam Long operatorId) {

        taskService.assignTask(taskId, assignee, operatorId);
        return ResponseEntity.ok().build();
    }

    /**
     * 委派任务
     */
    @PostMapping("/{taskId}/delegate")
    public ResponseEntity<Void> delegateTask(
            @PathVariable String taskId,
            @RequestParam String delegatee,
            @RequestParam Long operatorId) {

        taskService.delegateTask(taskId, delegatee, operatorId);
        return ResponseEntity.ok().build();
    }

    /**
     * 转办任务
     */
    @PostMapping("/{taskId}/transfer")
    public ResponseEntity<Void> transferTask(
            @PathVariable String taskId,
            @RequestParam String newAssignee,
            @RequestParam Long operatorId) {

        taskService.transferTask(taskId, newAssignee, operatorId);
        return ResponseEntity.ok().build();
    }

    /**
     * 审批任务（同意/拒绝）
     */
    @PostMapping("/{taskId}/approve")
    public ResponseEntity<Void> approveTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String taskId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comment,
            @RequestBody(required = false) Map<String, Object> variables) {

        taskService.approveTask(taskId, currentUser.getId(), approved, comment, variables);
        return ResponseEntity.ok().build();
    }

    /**
     * 回退任务
     */
    @PostMapping("/{taskId}/rollback")
    public ResponseEntity<Void> rollbackTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String taskId,
            @RequestParam String targetActivityId,
            @RequestParam(required = false) String reason) {

        taskService.rollbackTask(taskId, targetActivityId, currentUser.getId(), reason);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量完成任务
     */
    @PostMapping("/batch/complete")
    public ResponseEntity<Void> batchCompleteTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestBody java.util.List<String> taskIds,
            @RequestBody(required = false) Map<String, Object> variables) {

        taskService.batchCompleteTasks(taskIds, currentUser.getId(), variables);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量分配任务
     */
    @PostMapping("/batch/assign")
    public ResponseEntity<Void> batchAssignTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestBody java.util.List<String> taskIds,
            @RequestParam String assignee) {

        taskService.batchAssignTasks(taskIds, assignee, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 获取任务历史记录
     */
    @GetMapping("/{taskId}/history")
    public ResponseEntity<java.util.List<Map<String, Object>>> getTaskHistory(@PathVariable String taskId) {
        java.util.List<Map<String, Object>> history = taskService.getTaskHistory(taskId);
        return ResponseEntity.ok(history);
    }

    /**
     * 添加任务评论
     */
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<Void> addTaskComment(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String taskId,
            @RequestParam String comment) {

        taskService.addTaskComment(taskId, currentUser.getId(), comment);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取任务评论
     */
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<java.util.List<Map<String, Object>>> getTaskComments(@PathVariable String taskId) {
        java.util.List<Map<String, Object>> comments = taskService.getTaskComments(taskId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 获取可回退的节点
     */
    @GetMapping("/{taskId}/rollback-nodes")
    public ResponseEntity<java.util.List<Map<String, Object>>> getRollbackNodes(@PathVariable String taskId) {
        java.util.List<Map<String, Object>> nodes = taskService.getRollbackNodes(taskId);
        return ResponseEntity.ok(nodes);
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(@AuthenticationPrincipal User currentUser) {
        Map<String, Object> statistics = taskService.getTaskStatistics(currentUser);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 根据ID获取任务详情
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable String taskId) {
        Map<String, Object> task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }
}