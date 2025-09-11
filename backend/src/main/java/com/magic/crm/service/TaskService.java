package com.magic.crm.service;

import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.User;
import com.magic.crm.exception.BusinessException;
import com.magic.crm.repository.TaskRepository;
import com.magic.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.engine.task.Comment;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final org.flowable.engine.TaskService flowableTaskService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * 分页查询待办任务
     */
    public PageResponse<Map<String, Object>> getPendingTasks(Long userId,
            String taskName,
            String processDefinitionKey,
            int page, int size) {
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 构建任务查询
        TaskQuery taskQuery = flowableTaskService.createTaskQuery()
                .taskAssignee(user.getUsername())
                .active()
                .orderByTaskCreateTime()
                .desc();

        // 添加过滤条件
        if (taskName != null && !taskName.trim().isEmpty()) {
            taskQuery.taskNameLike("%" + taskName + "%");
        }
        if (processDefinitionKey != null && !processDefinitionKey.trim().isEmpty()) {
            taskQuery.processDefinitionKey(processDefinitionKey);
        }

        // 分页查询
        List<org.flowable.task.api.Task> tasks = taskQuery
                .listPage(page * size, size);

        long totalCount = taskQuery.count();

        // 转换为返回格式
        List<Map<String, Object>> taskList = tasks.stream().map(task -> {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("name", task.getName());
            taskInfo.put("description", task.getDescription());
            taskInfo.put("assignee", task.getAssignee());
            taskInfo.put("createTime", task.getCreateTime());
            taskInfo.put("dueDate", task.getDueDate());
            taskInfo.put("priority", task.getPriority());
            taskInfo.put("processInstanceId", task.getProcessInstanceId());
            taskInfo.put("processDefinitionId", task.getProcessDefinitionId());
            taskInfo.put("taskDefinitionKey", task.getTaskDefinitionKey());

            // 获取流程实例信息
            try {
                org.flowable.engine.runtime.ProcessInstance processInstance = runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .singleResult();
                if (processInstance != null) {
                    taskInfo.put("processName", processInstance.getName());
                    taskInfo.put("businessKey", processInstance.getBusinessKey());
                }
            } catch (Exception e) {
                log.warn("获取流程实例信息失败: {}", task.getProcessInstanceId(), e);
            }

            // 获取任务变量
            try {
                Map<String, Object> variables = flowableTaskService.getVariables(task.getId());
                taskInfo.put("variables", variables);
            } catch (Exception e) {
                log.warn("获取任务变量失败: {}", task.getId(), e);
                taskInfo.put("variables", new HashMap<>());
            }

            return taskInfo;
        }).collect(Collectors.toList());

        PageResponse<Map<String, Object>> response = new PageResponse<>();
        response.setTotalCount(totalCount);
        response.setPageIndex(page + 1);
        response.setPageSize(size);
        response.setPageCount((int) Math.ceil((double) totalCount / size));
        response.setRecords(taskList);
        return response;
    }

    /**
     * 分页查询已办任务
     */
    public PageResponse<Map<String, Object>> getCompletedTasks(Long userId,
            String taskName,
            String processDefinitionKey,
            int page, int size) {
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 构建历史任务查询
        var historyTaskQuery = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(user.getUsername())
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc();

        // 添加过滤条件
        if (taskName != null && !taskName.trim().isEmpty()) {
            historyTaskQuery.taskNameLike("%" + taskName + "%");
        }
        if (processDefinitionKey != null && !processDefinitionKey.trim().isEmpty()) {
            historyTaskQuery.processDefinitionKey(processDefinitionKey);
        }

        // 分页查询
        List<HistoricTaskInstance> tasks = historyTaskQuery
                .listPage(page * size, size);

        long totalCount = historyTaskQuery.count();

        // 转换为返回格式
        List<Map<String, Object>> taskList = tasks.stream().map(task -> {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("name", task.getName());
            taskInfo.put("description", task.getDescription());
            taskInfo.put("assignee", task.getAssignee());
            taskInfo.put("createTime", task.getCreateTime());
            taskInfo.put("endTime", task.getEndTime());
            taskInfo.put("durationInMillis", task.getDurationInMillis());
            taskInfo.put("priority", task.getPriority());
            taskInfo.put("processInstanceId", task.getProcessInstanceId());
            taskInfo.put("processDefinitionId", task.getProcessDefinitionId());
            taskInfo.put("taskDefinitionKey", task.getTaskDefinitionKey());

            // 获取流程实例信息
            try {
                var historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .singleResult();
                if (historicProcessInstance != null) {
                    taskInfo.put("processName", historicProcessInstance.getName());
                    taskInfo.put("businessKey", historicProcessInstance.getBusinessKey());
                }
            } catch (Exception e) {
                log.warn("获取历史流程实例信息失败: {}", task.getProcessInstanceId(), e);
            }

            return taskInfo;
        }).collect(Collectors.toList());

        PageResponse<Map<String, Object>> response = new PageResponse<>();
        response.setTotalCount(totalCount);
        response.setPageIndex(page + 1);
        response.setPageSize(size);
        response.setPageCount((int) Math.ceil((double) totalCount / size));
        response.setRecords(taskList);
        return response;
    }

    /**
     * 根据任务ID获取任务详情
     */
    public Map<String, Object> getTaskById(String taskId) {
        org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        Map<String, Object> taskInfo = new HashMap<>();
        taskInfo.put("id", task.getId());
        taskInfo.put("name", task.getName());
        taskInfo.put("description", task.getDescription());
        taskInfo.put("assignee", task.getAssignee());
        taskInfo.put("owner", task.getOwner());
        taskInfo.put("createTime", task.getCreateTime());
        taskInfo.put("dueDate", task.getDueDate());
        taskInfo.put("priority", task.getPriority());
        taskInfo.put("processInstanceId", task.getProcessInstanceId());
        taskInfo.put("processDefinitionId", task.getProcessDefinitionId());
        taskInfo.put("taskDefinitionKey", task.getTaskDefinitionKey());

        // 获取任务变量
        Map<String, Object> variables = flowableTaskService.getVariables(taskId);
        taskInfo.put("variables", variables);

        // 获取流程实例信息
        org.flowable.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        if (processInstance != null) {
            taskInfo.put("processName", processInstance.getName());
            taskInfo.put("businessKey", processInstance.getBusinessKey());
        }

        return taskInfo;
    }

    /**
     * 完成任务
     */
    @Transactional
    public void completeTask(String taskId, Map<String, Object> variables, Long userId) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException("任务不存在");
            }

            // 验证用户权限
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("用户不存在"));

            if (!user.getUsername().equals(task.getAssignee())) {
                throw new BusinessException("无权限完成此任务");
            }

            // 设置完成时间和完成人
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("completedBy", user.getUsername());
            variables.put("completedTime", LocalDateTime.now());

            // 完成任务
            flowableTaskService.complete(taskId, variables);

            // 记录任务完成日志
            log.info("任务完成成功: taskId={}, userId={}, username={}",
                    taskId, userId, user.getUsername());

        } catch (Exception e) {
            log.error("完成任务失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException("完成任务失败: " + e.getMessage());
        }
    }

    /**
     * 分配任务
     */
    @Transactional
    public void assignTask(String taskId, String assignee, Long operatorId) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException("任务不存在");
            }

            // 验证被分配人是否存在
            User assigneeUser = userRepository.findByUsername(assignee)
                    .orElseThrow(() -> new BusinessException("被分配用户不存在"));

            // 分配任务
            flowableTaskService.setAssignee(taskId, assignee);

            // 记录分配日志
            User operator = userRepository.findById(operatorId)
                    .orElseThrow(() -> new BusinessException("操作用户不存在"));

            log.info("任务分配成功: taskId={}, assignee={}, operator={}",
                    taskId, assignee, operator.getUsername());

        } catch (Exception e) {
            log.error("分配任务失败: taskId={}, assignee={}, operatorId={}",
                    taskId, assignee, operatorId, e);
            throw new BusinessException("分配任务失败: " + e.getMessage());
        }
    }

    /**
     * 委派任务
     */
    @Transactional
    public void delegateTask(String taskId, String delegatee, Long operatorId) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException("任务不存在");
            }

            // 验证委派人是否存在
            User delegateeUser = userRepository.findByUsername(delegatee)
                    .orElseThrow(() -> new BusinessException("委派用户不存在"));

            // 委派任务
            flowableTaskService.delegateTask(taskId, delegatee);

            // 记录委派日志
            User operator = userRepository.findById(operatorId)
                    .orElseThrow(() -> new BusinessException("操作用户不存在"));

            log.info("任务委派成功: taskId={}, delegatee={}, operator={}",
                    taskId, delegatee, operator.getUsername());

        } catch (Exception e) {
            log.error("委派任务失败: taskId={}, delegatee={}, operatorId={}",
                    taskId, delegatee, operatorId, e);
            throw new BusinessException("委派任务失败: " + e.getMessage());
        }
    }

    /**
     * 转办任务
     */
    @Transactional
    public void transferTask(String taskId, String transferee, Long operatorId) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException("任务不存在");
            }

            // 验证转办人是否存在
            User transfereeUser = userRepository.findByUsername(transferee)
                    .orElseThrow(() -> new BusinessException("转办用户不存在"));

            // 转办任务（设置新的处理人）
            flowableTaskService.setAssignee(taskId, transferee);

            // 添加转办记录到任务变量
            Map<String, Object> variables = new HashMap<>();
            User operator = userRepository.findById(operatorId)
                    .orElseThrow(() -> new BusinessException("操作用户不存在"));

            variables.put("transferredBy", operator.getUsername());
            variables.put("transferredTo", transferee);
            variables.put("transferredTime", LocalDateTime.now());

            flowableTaskService.setVariablesLocal(taskId, variables);

            log.info("任务转办成功: taskId={}, transferee={}, operator={}",
                    taskId, transferee, operator.getUsername());

        } catch (Exception e) {
            log.error("转办任务失败: taskId={}, transferee={}, operatorId={}",
                    taskId, transferee, operatorId, e);
            throw new BusinessException("转办任务失败: " + e.getMessage());
        }
    }

    /**
     * 设置任务变量
     */
    public void setTaskVariable(String taskId, String variableName, Object value) {
        try {
            flowableTaskService.setVariable(taskId, variableName, value);
            log.info("设置任务变量成功: taskId={}, variableName={}", taskId, variableName);
        } catch (Exception e) {
            log.error("设置任务变量失败: taskId={}, variableName={}", taskId, variableName, e);
            throw new BusinessException("设置任务变量失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务变量
     */
    public Map<String, Object> getTaskVariables(String taskId) {
        try {
            return flowableTaskService.getVariables(taskId);
        } catch (Exception e) {
            log.error("获取任务变量失败: {}", taskId, e);
            throw new BusinessException("获取任务变量失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的任务统计
     */
    public Map<String, Long> getTaskStatistics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        Map<String, Long> statistics = new HashMap<>();

        // 待办任务数量
        long pendingCount = flowableTaskService.createTaskQuery()
                .taskAssignee(user.getUsername())
                .active()
                .count();

        // 已办任务数量
        long completedCount = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(user.getUsername())
                .finished()
                .count();

        statistics.put("pending", pendingCount);
        statistics.put("completed", completedCount);
        statistics.put("total", pendingCount + completedCount);

        return statistics;
    }

    /**
     * 检查任务是否存在
     */
    public boolean existsTask(String taskId) {
        return flowableTaskService.createTaskQuery()
                .taskId(taskId)
                .count() > 0;
    }

    /**
     * 审批任务（同意/拒绝）
     */
    public void approveTask(String taskId, Long userId, boolean approved, String comment,
            Map<String, Object> variables) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }

            // 设置审批结果变量
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("approved", approved);
            variables.put("approvalResult", approved ? "APPROVED" : "REJECTED");

            // 添加审批意见
            if (comment != null && !comment.trim().isEmpty()) {
                variables.put("approvalComment", comment);
                // 添加任务评论
                addTaskComment(taskId, userId, comment);
            }

            // 完成任务
            flowableTaskService.complete(taskId, variables);

            log.info("审批任务成功: taskId={}, userId={}, approved={}", taskId, userId, approved);
        } catch (Exception e) {
            log.error("审批任务失败: taskId={}, userId={}, approved={}", taskId, userId, approved, e);
            throw new RuntimeException("审批任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 回退任务
     */
    public void rollbackTask(String taskId, String targetActivityId, Long operatorId, String reason) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }

            // 获取流程实例
            String processInstanceId = task.getProcessInstanceId();

            // 使用RuntimeService进行回退
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(processInstanceId)
                    .moveActivityIdTo(task.getTaskDefinitionKey(), targetActivityId)
                    .changeState();

            // 记录回退原因
            if (reason != null && !reason.trim().isEmpty()) {
                addTaskComment(taskId, operatorId, "回退原因: " + reason);
            }

            log.info("回退任务成功: taskId={}, targetActivityId={}, operatorId={}", taskId, targetActivityId, operatorId);
        } catch (Exception e) {
            log.error("回退任务失败: taskId={}, targetActivityId={}, operatorId={}", taskId, targetActivityId, operatorId, e);
            throw new RuntimeException("回退任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量完成任务
     */
    public void batchCompleteTasks(List<String> taskIds, Long userId, Map<String, Object> variables) {
        try {
            for (String taskId : taskIds) {
                completeTask(taskId, variables, userId);
            }
            log.info("批量完成任务成功: taskIds={}, userId={}", taskIds, userId);
        } catch (Exception e) {
            log.error("批量完成任务失败: taskIds={}, userId={}", taskIds, userId, e);
            throw new RuntimeException("批量完成任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量分配任务
     */
    public void batchAssignTasks(List<String> taskIds, String assignee, Long operatorId) {
        try {
            for (String taskId : taskIds) {
                assignTask(taskId, assignee, operatorId);
            }
            log.info("批量分配任务成功: taskIds={}, assignee={}, operatorId={}", taskIds, assignee, operatorId);
        } catch (Exception e) {
            log.error("批量分配任务失败: taskIds={}, assignee={}, operatorId={}", taskIds, assignee, operatorId, e);
            throw new RuntimeException("批量分配任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取任务历史记录
     */
    public List<Map<String, Object>> getTaskHistory(String taskId) {
        try {
            // 获取任务信息
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }

            // 获取历史任务实例
            List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .orderByHistoricTaskInstanceEndTime()
                    .desc()
                    .list();

            List<Map<String, Object>> history = new ArrayList<>();
            for (HistoricTaskInstance historicTask : historicTasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("taskId", historicTask.getId());
                taskInfo.put("taskName", historicTask.getName());
                taskInfo.put("assignee", historicTask.getAssignee());
                taskInfo.put("startTime", historicTask.getStartTime());
                taskInfo.put("endTime", historicTask.getEndTime());
                taskInfo.put("durationInMillis", historicTask.getDurationInMillis());

                // 获取任务变量
                Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                        .taskId(historicTask.getId())
                        .list()
                        .stream()
                        .collect(Collectors.toMap(
                                HistoricVariableInstance::getVariableName,
                                HistoricVariableInstance::getValue));
                taskInfo.put("variables", variables);

                history.add(taskInfo);
            }

            return history;
        } catch (Exception e) {
            log.error("获取任务历史记录失败: taskId={}", taskId, e);
            throw new RuntimeException("获取任务历史记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加任务评论
     */
    public void addTaskComment(String taskId, Long userId, String comment) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }

            // 获取用户信息
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

            // 添加评论
            flowableTaskService.addComment(taskId, task.getProcessInstanceId(), comment);

            log.info("添加任务评论成功: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("添加任务评论失败: taskId={}, userId={}", taskId, userId, e);
            throw new RuntimeException("添加任务评论失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取任务评论
     */
    public List<Map<String, Object>> getTaskComments(String taskId) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }

            // 获取评论
            List<Comment> comments = flowableTaskService.getTaskComments(taskId);

            List<Map<String, Object>> result = new ArrayList<>();
            for (Comment comment : comments) {
                Map<String, Object> commentInfo = new HashMap<>();
                commentInfo.put("id", comment.getId());
                commentInfo.put("message", comment.getFullMessage());
                commentInfo.put("userId", comment.getUserId());
                commentInfo.put("time", comment.getTime());
                result.add(commentInfo);
            }

            return result;
        } catch (Exception e) {
            log.error("获取任务评论失败: taskId={}", taskId, e);
            throw new RuntimeException("获取任务评论失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取可回退的节点
     */
    public List<Map<String, Object>> getRollbackNodes(String taskId) {
        try {
            // 验证任务是否存在
            org.flowable.task.api.Task task = flowableTaskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }

            // 获取流程定义
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();

            // 获取BPMN模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            Process process = bpmnModel.getMainProcess();

            // 获取所有用户任务节点
            List<Map<String, Object>> nodes = new ArrayList<>();
            Collection<FlowElement> flowElements = process.getFlowElements();

            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    Map<String, Object> nodeInfo = new HashMap<>();
                    nodeInfo.put("activityId", userTask.getId());
                    nodeInfo.put("activityName", userTask.getName());
                    nodes.add(nodeInfo);
                }
            }

            return nodes;
        } catch (Exception e) {
            log.error("获取可回退节点失败: taskId={}", taskId, e);
            throw new RuntimeException("获取可回退节点失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStatistics(User user) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 待办任务数量
            long pendingCount = flowableTaskService.createTaskQuery()
                    .taskAssignee(user.getUsername())
                    .count();
            statistics.put("pendingCount", pendingCount);

            // 今日完成任务数量
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date startOfNextDay = calendar.getTime();

            long todayCompletedCount = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(user.getUsername())
                    .finished()
                    .taskCompletedAfter(startOfDay)
                    .taskCompletedBefore(startOfNextDay)
                    .count();
            statistics.put("todayCompletedCount", todayCompletedCount);

            // 本周完成任务数量
            calendar.setTime(today);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfWeek = calendar.getTime();

            long weekCompletedCount = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(user.getUsername())
                    .finished()
                    .taskCompletedAfter(startOfWeek)
                    .count();
            statistics.put("weekCompletedCount", weekCompletedCount);

            // 逾期任务数量
            long overdueCount = flowableTaskService.createTaskQuery()
                    .taskAssignee(user.getUsername())
                    .taskDueBefore(today)
                    .count();
            statistics.put("overdueCount", overdueCount);

            return statistics;
        } catch (Exception e) {
            log.error("获取任务统计信息失败: userId={}", user.getId(), e);
            throw new RuntimeException("获取任务统计信息失败: " + e.getMessage(), e);
        }
    }
}