package com.magic.crm.service;

import com.magic.crm.dto.PageResponse;
import com.magic.crm.entity.ProcessInstance;
import com.magic.crm.entity.User;
import com.magic.crm.exception.BusinessException;
import com.magic.crm.repository.ProcessInstanceRepository;
import com.magic.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程实例服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessInstanceService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final ProcessInstanceRepository processInstanceRepository;
    private final UserRepository userRepository;

    /**
     * 启动流程实例
     */
    @Transactional
    public ProcessInstance startProcessInstance(String processDefinitionKey,
            String businessKey,
            Map<String, Object> variables,
            Long startUserId) {
        try {
            // 获取启动用户信息
            User startUser = userRepository.findById(startUserId)
                    .orElseThrow(() -> new BusinessException("用户不存在"));

            // 设置流程变量
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("startUserId", startUserId);
            variables.put("startUserName", startUser.getFullName());
            variables.put("startTime", LocalDateTime.now());

            // 创建流程实例构建器
            ProcessInstanceBuilder builder = runtimeService.createProcessInstanceBuilder()
                    .processDefinitionKey(processDefinitionKey)
                    .businessKey(businessKey)
                    .variables(variables);

            // 启动流程实例
            org.flowable.engine.runtime.ProcessInstance flowableInstance = builder.start();

            // 创建自定义流程实例记录
            ProcessInstance processInstance = new ProcessInstance();
            processInstance.setProcessInstanceId(flowableInstance.getId());
            processInstance.setProcessDefinitionId(flowableInstance.getProcessDefinitionId());
            processInstance.setProcessDefinitionKey(processDefinitionKey);
            processInstance.setBusinessKey(businessKey);
            processInstance.setStartedBy(startUserId);
            processInstance.setStartTime(LocalDateTime.now());
            processInstance.setStatus("RUNNING");

            // 设置流程名称 - 如果Flowable实例没有名称，则生成一个默认名称
            String instanceName = flowableInstance.getName();
            if (instanceName == null || instanceName.trim().isEmpty()) {
                instanceName = processDefinitionKey + "_" + LocalDateTime.now().toString().substring(0, 19);
            }
            processInstance.setProcessInstanceName(instanceName);

            return processInstanceRepository.save(processInstance);

        } catch (Exception e) {
            log.error("启动流程实例失败: processDefinitionKey={}, businessKey={}",
                    processDefinitionKey, businessKey, e);
            throw new BusinessException("启动流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 根据流程定义ID启动流程实例
     */
    @Transactional
    public ProcessInstance startProcessInstanceById(String processDefinitionId,
            String businessKey,
            Map<String, Object> variables,
            Long startUserId) {
        try {
            // 获取启动用户信息
            User startUser = userRepository.findById(startUserId)
                    .orElseThrow(() -> new BusinessException("用户不存在"));

            // 设置流程变量
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("startUserId", startUserId);
            variables.put("startUserName", startUser.getFullName());
            variables.put("startTime", LocalDateTime.now());

            // 启动流程实例
            org.flowable.engine.runtime.ProcessInstance flowableInstance = runtimeService
                    .startProcessInstanceById(processDefinitionId, businessKey, variables);

            // 创建自定义流程实例记录
            ProcessInstance processInstance = new ProcessInstance();
            processInstance.setProcessInstanceId(flowableInstance.getId());
            processInstance.setProcessDefinitionId(processDefinitionId);
            processInstance.setProcessDefinitionKey(flowableInstance.getProcessDefinitionKey());
            processInstance.setBusinessKey(businessKey);
            processInstance.setStartedBy(startUserId);
            processInstance.setStartTime(LocalDateTime.now());
            processInstance.setStatus("RUNNING");
            // 设置流程名称 - 如果Flowable实例没有名称，则生成一个默认名称
            String instanceName = flowableInstance.getName();
            if (instanceName == null || instanceName.trim().isEmpty()) {
                instanceName = flowableInstance.getProcessDefinitionKey() + "_"
                        + LocalDateTime.now().toString().substring(0, 19);
            }
            processInstance.setProcessInstanceName(instanceName);

            return processInstanceRepository.save(processInstance);

        } catch (Exception e) {
            log.error("启动流程实例失败: processDefinitionId={}, businessKey={}",
                    processDefinitionId, businessKey, e);
            throw new BusinessException("启动流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询流程实例
     */
    public PageResponse<ProcessInstance> getProcessInstances(String processName,
            String businessKey,
            String status,
            Long startUserId,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "startTime"));

        Page<ProcessInstance> pageResult = processInstanceRepository
                .findByConditions(processName, businessKey, status, startUserId, pageable);

        // 设置启动人姓名（通过关联的startedByUser获取）
        pageResult.getContent().forEach(processInstance -> {
            if (processInstance.getStartedByUser() != null) {
                processInstance.setStartedByName(processInstance.getStartedByUser().getFullName());
            }
        });

        return new PageResponse<>(pageResult);
    }

    /**
     * 根据ID获取流程实例
     */
    public ProcessInstance getProcessInstanceById(Long id) {
        return processInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("流程实例不存在"));
    }

    /**
     * 根据流程实例ID获取流程实例
     */
    public ProcessInstance getProcessInstanceByInstanceId(String processInstanceId) {
        return processInstanceRepository.findByProcessInstanceId(processInstanceId)
                .orElseThrow(() -> new BusinessException("流程实例不存在"));
    }

    /**
     * 暂停流程实例
     */
    @Transactional
    public void suspendProcessInstance(String processInstanceId) {
        try {
            // 暂停Flowable中的流程实例
            runtimeService.suspendProcessInstanceById(processInstanceId);

            // 更新自定义表状态
            ProcessInstance processInstance = getProcessInstanceByInstanceId(processInstanceId);
            processInstance.setStatus("SUSPENDED");
            processInstance.setEndTime(LocalDateTime.now());
            processInstanceRepository.save(processInstance);

            log.info("流程实例已暂停: {}", processInstanceId);

        } catch (Exception e) {
            log.error("暂停流程实例失败: {}", processInstanceId, e);
            throw new BusinessException("暂停流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 激活流程实例
     */
    @Transactional
    public void activateProcessInstance(String processInstanceId) {
        try {
            // 激活Flowable中的流程实例
            runtimeService.activateProcessInstanceById(processInstanceId);

            // 更新自定义表状态
            ProcessInstance processInstance = getProcessInstanceByInstanceId(processInstanceId);
            processInstance.setStatus("RUNNING");
            processInstance.setEndTime(null);
            processInstanceRepository.save(processInstance);

            log.info("流程实例已激活: {}", processInstanceId);

        } catch (Exception e) {
            log.error("激活流程实例失败: {}", processInstanceId, e);
            throw new BusinessException("激活流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 终止流程实例
     */
    @Transactional
    public void terminateProcessInstance(String processInstanceId, String reason) {
        try {
            // 终止Flowable中的流程实例
            runtimeService.deleteProcessInstance(processInstanceId, reason);

            // 更新自定义表状态
            ProcessInstance processInstance = getProcessInstanceByInstanceId(processInstanceId);
            processInstance.setStatus("TERMINATED");
            processInstance.setEndTime(LocalDateTime.now());
            processInstance.setDeleteReason(reason);
            processInstanceRepository.save(processInstance);

            log.info("流程实例已终止: {}, 原因: {}", processInstanceId, reason);

        } catch (Exception e) {
            log.error("终止流程实例失败: {}", processInstanceId, e);
            throw new BusinessException("终止流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程实例的当前任务
     */
    public List<Map<String, Object>> getCurrentTasks(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        return tasks.stream().map(task -> {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("name", task.getName());
            taskInfo.put("assignee", task.getAssignee());
            taskInfo.put("createTime", task.getCreateTime());
            taskInfo.put("dueDate", task.getDueDate());
            taskInfo.put("priority", task.getPriority());
            taskInfo.put("description", task.getDescription());

            // 获取任务变量
            Map<String, Object> variables = taskService.getVariables(task.getId());
            taskInfo.put("variables", variables);

            return taskInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取流程实例的历史任务
     */
    public List<Map<String, Object>> getHistoryTasks(String processInstanceId) {
        // 这里可以使用Flowable的HistoryService来获取历史任务
        // 暂时返回空列表，后续可以扩展
        return List.of();
    }

    /**
     * 设置流程变量
     */
    public void setProcessVariable(String processInstanceId, String variableName, Object value) {
        try {
            runtimeService.setVariable(processInstanceId, variableName, value);
            log.info("设置流程变量成功: processInstanceId={}, variableName={}",
                    processInstanceId, variableName);
        } catch (Exception e) {
            log.error("设置流程变量失败: processInstanceId={}, variableName={}",
                    processInstanceId, variableName, e);
            throw new BusinessException("设置流程变量失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程变量
     */
    public Map<String, Object> getProcessVariables(String processInstanceId) {
        try {
            return runtimeService.getVariables(processInstanceId);
        } catch (Exception e) {
            log.error("获取流程变量失败: {}", processInstanceId, e);
            throw new BusinessException("获取流程变量失败: " + e.getMessage());
        }
    }

    /**
     * 检查流程实例是否存在
     */
    public boolean existsProcessInstance(String processInstanceId) {
        return processInstanceRepository.existsByProcessInstanceId(processInstanceId);
    }

    /**
     * 获取用户的流程实例
     */
    public List<ProcessInstance> getUserProcessInstances(Long userId) {
        return processInstanceRepository.findByStartedByOrderByStartTimeDesc(userId);
    }
}