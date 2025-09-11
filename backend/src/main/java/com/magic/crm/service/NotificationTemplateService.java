package com.magic.crm.service;

import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationTemplate;
import com.magic.crm.entity.NotificationType;
import com.magic.crm.repository.NotificationTemplateRepository;
import com.magic.crm.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通知模板服务类
 */
@Service
@Transactional
public class NotificationTemplateService {
    
    @Autowired
    private NotificationTemplateRepository templateRepository;
    
    /**
     * 创建通知模板
     */
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        // 检查模板代码是否已存在
        if (templateRepository.existsByCode(template.getCode())) {
            throw new IllegalArgumentException("模板代码已存在: " + template.getCode());
        }
        
        // 设置默认值
        if (template.getIsEnabled() == null) {
            template.setIsEnabled(true);
        }
        if (template.getIsSystem() == null) {
            template.setIsSystem(false);
        }
        
        return templateRepository.save(template);
    }
    
    /**
     * 更新通知模板
     */
    public NotificationTemplate updateTemplate(Long id, NotificationTemplate template) {
        NotificationTemplate existingTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        
        // 检查模板代码是否与其他模板冲突
        if (!existingTemplate.getCode().equals(template.getCode()) && 
            templateRepository.existsByCodeAndIdNot(template.getCode(), id)) {
            throw new IllegalArgumentException("模板代码已存在: " + template.getCode());
        }
        
        // 更新字段
        existingTemplate.setName(template.getName());
        existingTemplate.setCode(template.getCode());
        existingTemplate.setDescription(template.getDescription());
        existingTemplate.setType(template.getType());
        existingTemplate.setChannel(template.getChannel());
        existingTemplate.setTitleTemplate(template.getTitleTemplate());
        existingTemplate.setContentTemplate(template.getContentTemplate());
        existingTemplate.setVariables(template.getVariables());
        existingTemplate.setIsEnabled(template.getIsEnabled());
        
        return templateRepository.save(existingTemplate);
    }
    
    /**
     * 删除通知模板
     */
    public boolean deleteTemplate(Long id) {
        Optional<NotificationTemplate> templateOpt = templateRepository.findById(id);
        if (templateOpt.isPresent()) {
            NotificationTemplate template = templateOpt.get();
            // 系统模板不能删除
            if (template.getIsSystem()) {
                throw new IllegalArgumentException("系统模板不能删除");
            }
            templateRepository.delete(template);
            return true;
        }
        return false;
    }
    
    /**
     * 根据ID获取模板
     */
    public Optional<NotificationTemplate> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }
    
    /**
     * 根据代码获取模板
     */
    public Optional<NotificationTemplate> getTemplateByCode(String code) {
        return templateRepository.findByCode(code);
    }
    
    /**
     * 根据代码获取启用的模板
     */
    public Optional<NotificationTemplate> getEnabledTemplateByCode(String code) {
        return templateRepository.findByCodeAndIsEnabled(code, true);
    }
    
    /**
     * 分页查询模板
     */
    public PageResponse<NotificationTemplate> getTemplates(int page, int size, String name, 
                                                          NotificationType type, NotificationChannel channel, 
                                                          Boolean isEnabled) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationTemplate> templatePage = templateRepository.findByConditions(name, type, channel, isEnabled, pageable);
        return new PageResponse<>(templatePage);
    }
    
    /**
     * 根据类型获取模板列表
     */
    public List<NotificationTemplate> getTemplatesByType(NotificationType type) {
        return templateRepository.findByTypeAndIsEnabled(type, true);
    }
    
    /**
     * 根据渠道获取模板列表
     */
    public List<NotificationTemplate> getTemplatesByChannel(NotificationChannel channel) {
        return templateRepository.findByChannelAndIsEnabled(channel, true);
    }
    
    /**
     * 根据类型和渠道获取模板列表
     */
    public List<NotificationTemplate> getTemplatesByTypeAndChannel(NotificationType type, NotificationChannel channel) {
        return templateRepository.findByTypeAndChannelAndIsEnabled(type, channel, true);
    }
    
    /**
     * 启用/禁用模板
     */
    public boolean toggleTemplateStatus(Long id) {
        Optional<NotificationTemplate> templateOpt = templateRepository.findById(id);
        if (templateOpt.isPresent()) {
            NotificationTemplate template = templateOpt.get();
            template.setIsEnabled(!template.getIsEnabled());
            templateRepository.save(template);
            return true;
        }
        return false;
    }
    
    /**
     * 复制模板
     */
    public NotificationTemplate copyTemplate(Long id, String newCode, String newName) {
        NotificationTemplate originalTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        
        // 检查新代码是否已存在
        if (templateRepository.existsByCode(newCode)) {
            throw new IllegalArgumentException("模板代码已存在: " + newCode);
        }
        
        NotificationTemplate newTemplate = new NotificationTemplate();
        newTemplate.setName(newName);
        newTemplate.setCode(newCode);
        newTemplate.setDescription(originalTemplate.getDescription() + " (复制)");
        newTemplate.setType(originalTemplate.getType());
        newTemplate.setChannel(originalTemplate.getChannel());
        newTemplate.setTitleTemplate(originalTemplate.getTitleTemplate());
        newTemplate.setContentTemplate(originalTemplate.getContentTemplate());
        newTemplate.setVariables(originalTemplate.getVariables());
        newTemplate.setIsEnabled(true);
        newTemplate.setIsSystem(false);
        newTemplate.setCreatedBy(originalTemplate.getCreatedBy());
        
        return templateRepository.save(newTemplate);
    }
    
    /**
     * 渲染模板
     */
    public String renderTemplate(String templateCode, Map<String, Object> variables) {
        Optional<NotificationTemplate> templateOpt = getEnabledTemplateByCode(templateCode);
        if (!templateOpt.isPresent()) {
            throw new IllegalArgumentException("模板不存在或未启用: " + templateCode);
        }
        
        NotificationTemplate template = templateOpt.get();
        String content = template.getContentTemplate();
        
        // 简单的变量替换
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                content = content.replace(placeholder, value);
            }
        }
        
        return content;
    }
    
    /**
     * 渲染标题模板
     */
    public String renderTitleTemplate(String templateCode, Map<String, Object> variables) {
        Optional<NotificationTemplate> templateOpt = getEnabledTemplateByCode(templateCode);
        if (!templateOpt.isPresent()) {
            throw new IllegalArgumentException("模板不存在或未启用: " + templateCode);
        }
        
        NotificationTemplate template = templateOpt.get();
        String title = template.getTitleTemplate();
        
        // 简单的变量替换
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                title = title.replace(placeholder, value);
            }
        }
        
        return title;
    }
    
    /**
     * 获取模板统计信息
     */
    public List<Object[]> getTemplateStats() {
        return templateRepository.countByTypeAndIsEnabled(true);
    }
    
    /**
     * 获取渠道统计信息
     */
    public List<Object[]> getChannelStats() {
        return templateRepository.countByChannelAndIsEnabled(true);
    }
    
    /**
     * 批量启用/禁用模板
     */
    public int batchToggleTemplateStatus(List<Long> ids, Boolean isEnabled) {
        int count = 0;
        for (Long id : ids) {
            Optional<NotificationTemplate> templateOpt = templateRepository.findById(id);
            if (templateOpt.isPresent()) {
                NotificationTemplate template = templateOpt.get();
                // 系统模板不能禁用
                if (!template.getIsSystem() || isEnabled) {
                    template.setIsEnabled(isEnabled);
                    templateRepository.save(template);
                    count++;
                }
            }
        }
        return count;
    }
}
