package com.magic.crm.controller;

import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationTemplate;
import com.magic.crm.entity.NotificationType;
import com.magic.crm.service.NotificationTemplateService;
import com.magic.crm.util.ApiResponse;
import com.magic.crm.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知模板控制器
 */
@RestController
@RequestMapping("/api/notification-templates")
@CrossOrigin(origins = "*")
public class NotificationTemplateController {
    
    @Autowired
    private NotificationTemplateService templateService;
    
    /**
     * 创建通知模板
     */
    @PostMapping
    public ApiResponse<NotificationTemplate> createTemplate(@RequestBody NotificationTemplate template) {
        try {
            // 设置创建者（这里应该从当前登录用户获取）
            template.setCreatedBy(1L); // TODO: 从SecurityContext获取当前用户ID
            
            NotificationTemplate createdTemplate = templateService.createTemplate(template);
            return ApiResponse.success(createdTemplate);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("创建模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新通知模板
     */
    @PutMapping("/{id}")
    public ApiResponse<NotificationTemplate> updateTemplate(@PathVariable Long id, @RequestBody NotificationTemplate template) {
        try {
            NotificationTemplate updatedTemplate = templateService.updateTemplate(id, template);
            return ApiResponse.success(updatedTemplate);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除通知模板
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteTemplate(@PathVariable Long id) {
        try {
            boolean success = templateService.deleteTemplate(id);
            return ApiResponse.success(success);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取模板详情
     */
    @GetMapping("/{id}")
    public ApiResponse<NotificationTemplate> getTemplateById(@PathVariable Long id) {
        return templateService.getTemplateById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.notFound("模板不存在"));
    }
    
    /**
     * 根据代码获取模板
     */
    @GetMapping("/code/{code}")
    public ApiResponse<NotificationTemplate> getTemplateByCode(@PathVariable String code) {
        return templateService.getTemplateByCode(code)
                .map(ApiResponse::success)
                .orElse(ApiResponse.notFound("模板不存在"));
    }
    
    /**
     * 分页查询模板
     */
    @GetMapping
    public ApiResponse<PageResponse<NotificationTemplate>> getTemplates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationChannel channel,
            @RequestParam(required = false) Boolean isEnabled) {
        
        PageResponse<NotificationTemplate> templates = templateService.getTemplates(
                page, size, name, type, channel, isEnabled);
        return ApiResponse.success(templates);
    }
    
    /**
     * 根据类型获取模板列表
     */
    @GetMapping("/type/{type}")
    public ApiResponse<List<NotificationTemplate>> getTemplatesByType(@PathVariable NotificationType type) {
        List<NotificationTemplate> templates = templateService.getTemplatesByType(type);
        return ApiResponse.success(templates);
    }
    
    /**
     * 根据渠道获取模板列表
     */
    @GetMapping("/channel/{channel}")
    public ApiResponse<List<NotificationTemplate>> getTemplatesByChannel(@PathVariable NotificationChannel channel) {
        List<NotificationTemplate> templates = templateService.getTemplatesByChannel(channel);
        return ApiResponse.success(templates);
    }
    
    /**
     * 根据类型和渠道获取模板列表
     */
    @GetMapping("/type/{type}/channel/{channel}")
    public ApiResponse<List<NotificationTemplate>> getTemplatesByTypeAndChannel(
            @PathVariable NotificationType type, @PathVariable NotificationChannel channel) {
        List<NotificationTemplate> templates = templateService.getTemplatesByTypeAndChannel(type, channel);
        return ApiResponse.success(templates);
    }
    
    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/toggle")
    public ApiResponse<Boolean> toggleTemplateStatus(@PathVariable Long id) {
        boolean success = templateService.toggleTemplateStatus(id);
        return ApiResponse.success(success);
    }
    
    /**
     * 复制模板
     */
    @PostMapping("/{id}/copy")
    public ApiResponse<NotificationTemplate> copyTemplate(@PathVariable Long id, 
                                                         @RequestBody Map<String, String> request) {
        try {
            String newCode = request.get("code");
            String newName = request.get("name");
            
            if (newCode == null || newName == null) {
                return ApiResponse.badRequest("新代码和新名称不能为空");
            }
            
            NotificationTemplate copiedTemplate = templateService.copyTemplate(id, newCode, newName);
            return ApiResponse.success(copiedTemplate);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("复制模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 渲染模板
     */
    @PostMapping("/{code}/render")
    public ApiResponse<Map<String, String>> renderTemplate(@PathVariable String code, 
                                                          @RequestBody Map<String, Object> variables) {
        try {
            String renderedContent = templateService.renderTemplate(code, variables);
            String renderedTitle = templateService.renderTitleTemplate(code, variables);
            
            return ApiResponse.success(Map.of(
                "title", renderedTitle,
                "content", renderedContent
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("渲染模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取模板统计信息
     */
    @GetMapping("/stats/type")
    public ApiResponse<List<Object[]>> getTemplateStats() {
        List<Object[]> stats = templateService.getTemplateStats();
        return ApiResponse.success(stats);
    }
    
    /**
     * 获取渠道统计信息
     */
    @GetMapping("/stats/channel")
    public ApiResponse<List<Object[]>> getChannelStats() {
        List<Object[]> stats = templateService.getChannelStats();
        return ApiResponse.success(stats);
    }
    
    /**
     * 批量启用/禁用模板
     */
    @PutMapping("/batch-toggle")
    public ApiResponse<Integer> batchToggleTemplateStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            Boolean isEnabled = (Boolean) request.get("isEnabled");
            
            if (ids == null || isEnabled == null) {
                return ApiResponse.badRequest("参数不能为空");
            }
            
            int count = templateService.batchToggleTemplateStatus(ids, isEnabled);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("批量操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有通知类型
     */
    @GetMapping("/types")
    public ApiResponse<NotificationType[]> getNotificationTypes() {
        return ApiResponse.success(NotificationType.values());
    }
    
    /**
     * 获取所有通知渠道
     */
    @GetMapping("/channels")
    public ApiResponse<NotificationChannel[]> getNotificationChannels() {
        return ApiResponse.success(NotificationChannel.values());
    }
}
