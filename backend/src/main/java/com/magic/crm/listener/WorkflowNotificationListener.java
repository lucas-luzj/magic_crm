package com.magic.crm.listener;

import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationPriority;
import com.magic.crm.entity.NotificationType;
import com.magic.crm.service.NotificationService;
import com.magic.crm.service.NotificationSettingsService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 工作流通知监听器
 */
@Component
public class WorkflowNotificationListener implements FlowableEventListener, ExecutionListener {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationSettingsService settingsService;
    
    @Override
    public void onEvent(FlowableEvent event) {
        if (event instanceof FlowableEngineEntityEvent) {
            FlowableEngineEntityEvent entityEvent = (FlowableEngineEntityEvent) event;
            if (entityEvent.getType() == FlowableEngineEventType.TASK_CREATED) {
                handleTaskCreated(entityEvent);
            } else if (entityEvent.getType() == FlowableEngineEventType.TASK_COMPLETED) {
                handleTaskCompleted(entityEvent);
            } else if (entityEvent.getType() == FlowableEngineEventType.TASK_ASSIGNED) {
                handleTaskAssigned(entityEvent);
            }
        }
    }
    
    @Override
    public boolean isFailOnException() {
        return false;
    }
    
    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }
    
    @Override
    public String getOnTransaction() {
        return null;
    }
    
    @Override
    public void notify(DelegateExecution execution) {
        // 流程执行监听器
        String eventName = execution.getEventName();
        if ("start".equals(eventName)) {
            handleProcessStarted(execution);
        } else if ("end".equals(eventName)) {
            handleProcessCompleted(execution);
        }
    }
    
    /**
     * 处理任务创建事件
     */
    private void handleTaskCreated(FlowableEngineEntityEvent event) {
        // 从事件中获取任务信息
        Object entity = event.getEntity();
        if (entity instanceof org.flowable.task.api.Task) {
            org.flowable.task.api.Task task = (org.flowable.task.api.Task) entity;
            String assignee = task.getAssignee();
            if (assignee != null) {
                try {
                    Long userId = Long.valueOf(assignee);
                    
                    // 检查用户是否启用任务通知
                    if (settingsService.shouldSendNotification(userId, "in_site")) {
                        String title = "新任务待处理";
                        String content = String.format("您有一个新任务需要处理：%s", task.getName());
                        
                        notificationService.sendNotification(
                            userId,
                            null,
                            title,
                            content,
                            NotificationType.TASK,
                            NotificationChannel.IN_SITE,
                            NotificationPriority.NORMAL,
                            task.getId(),
                            "TASK",
                            "/workflow/task-handle/" + task.getId()
                        );
                    }
                } catch (NumberFormatException e) {
                    // 如果assignee不是数字ID，记录日志但不抛出异常
                    System.err.println("Invalid user ID format: " + assignee);
                }
            }
        }
    }
    
    /**
     * 处理任务完成事件
     */
    private void handleTaskCompleted(FlowableEngineEntityEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof org.flowable.task.api.Task) {
            org.flowable.task.api.Task task = (org.flowable.task.api.Task) entity;
            String assignee = task.getAssignee();
            if (assignee != null) {
                try {
                    Long userId = Long.valueOf(assignee);
                    
                    // 通知任务完成
                    if (settingsService.shouldSendNotification(userId, "in_site")) {
                        String title = "任务已完成";
                        String content = String.format("任务 '%s' 已完成", task.getName());
                        
                        notificationService.sendNotification(
                            userId,
                            null,
                            title,
                            content,
                            NotificationType.TASK,
                            NotificationChannel.IN_SITE,
                            NotificationPriority.LOW,
                            task.getId(),
                            "TASK",
                            null
                        );
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid user ID format: " + assignee);
                }
            }
        }
    }
    
    /**
     * 处理任务分配事件
     */
    private void handleTaskAssigned(FlowableEngineEntityEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof org.flowable.task.api.Task) {
            org.flowable.task.api.Task task = (org.flowable.task.api.Task) entity;
            String assignee = task.getAssignee();
            if (assignee != null) {
                try {
                    Long userId = Long.valueOf(assignee);
                    
                    // 通知任务分配
                    if (settingsService.shouldSendNotification(userId, "in_site")) {
                        String title = "任务已分配";
                        String content = String.format("任务 '%s' 已分配给您", task.getName());
                        
                        notificationService.sendNotification(
                            userId,
                            null,
                            title,
                            content,
                            NotificationType.TASK,
                            NotificationChannel.IN_SITE,
                            NotificationPriority.HIGH,
                            task.getId(),
                            "TASK",
                            "/workflow/task-handle/" + task.getId()
                        );
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid user ID format: " + assignee);
                }
            }
        }
    }
    
    /**
     * 处理流程启动事件
     */
    private void handleProcessStarted(DelegateExecution execution) {
        String initiator = (String) execution.getVariable("initiator");
        if (initiator != null) {
            Long userId = Long.valueOf(initiator);
            
            // 通知流程启动
            if (settingsService.shouldSendNotification(userId, "in_site")) {
                String title = "流程已启动";
                String content = String.format("流程 '%s' 已成功启动", execution.getProcessDefinitionId());
                
                notificationService.sendNotification(
                    userId,
                    null,
                    title,
                    content,
                    NotificationType.WORKFLOW,
                    NotificationChannel.IN_SITE,
                    NotificationPriority.NORMAL,
                    execution.getProcessInstanceId(),
                    "PROCESS",
                    "/workflow/process-instances/" + execution.getProcessInstanceId()
                );
            }
        }
    }
    
    /**
     * 处理流程完成事件
     */
    private void handleProcessCompleted(DelegateExecution execution) {
        String initiator = (String) execution.getVariable("initiator");
        if (initiator != null) {
            Long userId = Long.valueOf(initiator);
            
            // 通知流程完成
            if (settingsService.shouldSendNotification(userId, "in_site")) {
                String title = "流程已完成";
                String content = String.format("流程 '%s' 已完成", execution.getProcessDefinitionId());
                
                notificationService.sendNotification(
                    userId,
                    null,
                    title,
                    content,
                    NotificationType.WORKFLOW,
                    NotificationChannel.IN_SITE,
                    NotificationPriority.NORMAL,
                    execution.getProcessInstanceId(),
                    "PROCESS",
                    "/workflow/process-instances/" + execution.getProcessInstanceId()
                );
            }
        }
    }
}
