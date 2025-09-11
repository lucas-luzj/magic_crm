package com.magic.crm.service;

import com.magic.crm.entity.SystemSetting;
import com.magic.crm.repository.SystemSettingRepository;
import com.magic.crm.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 系统设置服务
 */
@Service
@Transactional
public class SystemSettingService {
    
    @Autowired
    private SystemSettingRepository systemSettingRepository;
    
    /**
     * 获取所有设置
     */
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }
    
    /**
     * 根据分组获取设置
     */
    public List<SystemSetting> getSettingsByGroup(String group) {
        return systemSettingRepository.findBySettingGroupOrderBySortOrderAsc(group);
    }
    
    /**
     * 获取所有设置分组
     */
    public List<String> getAllSettingGroups() {
        return systemSettingRepository.findDistinctSettingGroups();
    }
    
    /**
     * 根据键获取设置值
     */
    public String getSettingValue(String key) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(null);
    }
    
    /**
     * 根据键获取设置值，如果不存在返回默认值
     */
    public String getSettingValue(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }
    
    /**
     * 获取布尔类型设置值
     */
    public boolean getBooleanSettingValue(String key, boolean defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * 获取数字类型设置值
     */
    public int getIntSettingValue(String key, int defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取长整型设置值
     */
    public long getLongSettingValue(String key, long defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 设置值
     */
    public SystemSetting setSettingValue(String key, String value, Long userId) {
        Optional<SystemSetting> existingSetting = systemSettingRepository.findBySettingKey(key);
        
        if (existingSetting.isPresent()) {
            SystemSetting setting = existingSetting.get();
            if (!setting.getIsEditable()) {
                throw new RuntimeException("设置项不可编辑: " + key);
            }
            setting.setSettingValue(value);
            setting.setUpdatedBy(userId);
            setting.setUpdatedAt(LocalDateTime.now());
            return systemSettingRepository.save(setting);
        } else {
            throw new RuntimeException("设置项不存在: " + key);
        }
    }
    
    /**
     * 批量设置值
     */
    public void setSettingValues(Map<String, String> settings, Long userId) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            setSettingValue(entry.getKey(), entry.getValue(), userId);
        }
    }
    
    /**
     * 创建设置项
     */
    public SystemSetting createSetting(SystemSetting setting, Long userId) {
        if (systemSettingRepository.existsBySettingKey(setting.getSettingKey())) {
            throw new RuntimeException("设置键已存在: " + setting.getSettingKey());
        }
        
        setting.setCreatedBy(userId);
        setting.setUpdatedBy(userId);
        setting.setCreatedAt(LocalDateTime.now());
        setting.setUpdatedAt(LocalDateTime.now());
        
        return systemSettingRepository.save(setting);
    }
    
    /**
     * 更新设置项
     */
    public SystemSetting updateSetting(Long id, SystemSetting setting, Long userId) {
        SystemSetting existingSetting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设置项不存在: " + id));
        
        if (!existingSetting.getIsEditable()) {
            throw new RuntimeException("设置项不可编辑: " + existingSetting.getSettingKey());
        }
        
        existingSetting.setSettingName(setting.getSettingName());
        existingSetting.setDescription(setting.getDescription());
        existingSetting.setSettingValue(setting.getSettingValue());
        existingSetting.setSettingType(setting.getSettingType());
        existingSetting.setSettingGroup(setting.getSettingGroup());
        existingSetting.setIsSensitive(setting.getIsSensitive());
        existingSetting.setIsEditable(setting.getIsEditable());
        existingSetting.setDefaultValue(setting.getDefaultValue());
        existingSetting.setValidationRule(setting.getValidationRule());
        existingSetting.setSortOrder(setting.getSortOrder());
        existingSetting.setUpdatedBy(userId);
        existingSetting.setUpdatedAt(LocalDateTime.now());
        
        return systemSettingRepository.save(existingSetting);
    }
    
    /**
     * 删除设置项
     */
    public void deleteSetting(Long id) {
        SystemSetting setting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设置项不存在: " + id));
        
        if (!setting.getIsEditable()) {
            throw new RuntimeException("设置项不可删除: " + setting.getSettingKey());
        }
        
        systemSettingRepository.delete(setting);
    }
    
    /**
     * 重置为默认值
     */
    public SystemSetting resetToDefault(String key, Long userId) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseThrow(() -> new RuntimeException("设置项不存在: " + key));
        
        if (!setting.getIsEditable()) {
            throw new RuntimeException("设置项不可编辑: " + key);
        }
        
        setting.setSettingValue(setting.getDefaultValue());
        setting.setUpdatedBy(userId);
        setting.setUpdatedAt(LocalDateTime.now());
        
        return systemSettingRepository.save(setting);
    }
    
    /**
     * 获取邮件配置
     */
    public Map<String, String> getEmailConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("smtpHost", getSettingValue("email.smtp.host", ""));
        config.put("smtpPort", getSettingValue("email.smtp.port", "587"));
        config.put("smtpUsername", getSettingValue("email.smtp.username", ""));
        config.put("smtpPassword", getSettingValue("email.smtp.password", ""));
        config.put("smtpAuth", getSettingValue("email.smtp.auth", "true"));
        config.put("smtpStartTls", getSettingValue("email.smtp.starttls", "true"));
        config.put("fromEmail", getSettingValue("email.from.address", ""));
        config.put("fromName", getSettingValue("email.from.name", ""));
        return config;
    }
    
    /**
     * 获取短信配置
     */
    public Map<String, String> getSmsConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("provider", getSettingValue("sms.provider", "aliyun"));
        config.put("accessKeyId", getSettingValue("sms.aliyun.accessKeyId", ""));
        config.put("accessKeySecret", getSettingValue("sms.aliyun.accessKeySecret", ""));
        config.put("signName", getSettingValue("sms.aliyun.signName", ""));
        config.put("templateCode", getSettingValue("sms.aliyun.templateCode", ""));
        return config;
    }
    
    /**
     * 获取微信配置
     */
    public Map<String, String> getWechatConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("corpId", getSettingValue("wechat.corpId", ""));
        config.put("corpSecret", getSettingValue("wechat.corpSecret", ""));
        config.put("agentId", getSettingValue("wechat.agentId", ""));
        config.put("accessToken", getSettingValue("wechat.accessToken", ""));
        return config;
    }
    
    /**
     * 获取钉钉配置
     */
    public Map<String, String> getDingtalkConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("appKey", getSettingValue("dingtalk.appKey", ""));
        config.put("appSecret", getSettingValue("dingtalk.appSecret", ""));
        config.put("accessToken", getSettingValue("dingtalk.accessToken", ""));
        config.put("webhookUrl", getSettingValue("dingtalk.webhookUrl", ""));
        return config;
    }
    
    /**
     * 获取推送配置
     */
    public Map<String, String> getPushConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("provider", getSettingValue("push.provider", "jpush"));
        config.put("appKey", getSettingValue("push.jpush.appKey", ""));
        config.put("masterSecret", getSettingValue("push.jpush.masterSecret", ""));
        config.put("isProduction", getSettingValue("push.jpush.isProduction", "false"));
        return config;
    }
}
