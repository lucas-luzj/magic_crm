package com.magic.crm.controller;

import com.magic.crm.entity.SystemSetting;
import com.magic.crm.entity.User;
import com.magic.crm.service.DingtalkService;
import com.magic.crm.service.EmailService;
import com.magic.crm.service.PushService;
import com.magic.crm.service.SmsService;
import com.magic.crm.service.SystemSettingService;
import com.magic.crm.service.WechatService;
import com.magic.crm.util.ApiResponse;
import com.magic.crm.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/api/system-settings")
@CrossOrigin(origins = "*")
public class SystemSettingController {
    
    @Autowired
    private SystemSettingService systemSettingService;
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            // 根据JwtAuthenticationFilter的实现，principal就是User实体
            if (principal instanceof User) {
                return ((User) principal).getId();
            }
        }
        
        // 如果无法获取用户信息，抛出异常
        throw new RuntimeException("未找到当前登录用户");
    }
    
    /**
     * 获取所有设置
     */
    @GetMapping
    public ApiResponse<List<SystemSetting>> getAllSettings() {
        List<SystemSetting> settings = systemSettingService.getAllSettings();
        return ApiResponse.success(settings);
    }
    
    /**
     * 分页获取设置
     */
    @GetMapping("/page")
    public ApiResponse<PageResponse<SystemSetting>> getSettingsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("settingGroup", "sortOrder"));
        
        // 这里需要扩展Repository来支持分页查询
        // 暂时返回所有设置
        List<SystemSetting> allSettings = systemSettingService.getAllSettings();
        
        // 简单的分页实现
        int start = (page - 1) * size;
        int end = Math.min(start + size, allSettings.size());
        List<SystemSetting> pageSettings = allSettings.subList(start, end);
        
        PageResponse<SystemSetting> pageResponse = new PageResponse<>();
        pageResponse.setRecords(pageSettings);
        pageResponse.setTotalCount((long) allSettings.size());
        pageResponse.setPageCount((int) Math.ceil((double) allSettings.size() / size));
        pageResponse.setPageSize(size);
        pageResponse.setPageIndex(page);
        
        return ApiResponse.success(pageResponse);
    }
    
    /**
     * 根据分组获取设置
     */
    @GetMapping("/group/{group}")
    public ApiResponse<List<SystemSetting>> getSettingsByGroup(@PathVariable String group) {
        List<SystemSetting> settings = systemSettingService.getSettingsByGroup(group);
        return ApiResponse.success(settings);
    }
    
    /**
     * 获取所有设置分组
     */
    @GetMapping("/groups")
    public ApiResponse<List<String>> getAllSettingGroups() {
        List<String> groups = systemSettingService.getAllSettingGroups();
        return ApiResponse.success(groups);
    }
    
    /**
     * 根据键获取设置值
     */
    @GetMapping("/value/{key}")
    public ApiResponse<String> getSettingValue(@PathVariable String key) {
        String value = systemSettingService.getSettingValue(key);
        return ApiResponse.success(value);
    }
    
    /**
     * 根据键获取设置值，带默认值
     */
    @GetMapping("/value/{key}/{defaultValue}")
    public ApiResponse<String> getSettingValueWithDefault(
            @PathVariable String key, 
            @PathVariable String defaultValue) {
        String value = systemSettingService.getSettingValue(key, defaultValue);
        return ApiResponse.success(value);
    }
    
    /**
     * 设置值
     */
    @PutMapping("/value/{key}")
    public ApiResponse<SystemSetting> setSettingValue(
            @PathVariable String key, 
            @RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            Long userId = getCurrentUserId();
            SystemSetting setting = systemSettingService.setSettingValue(key, value, userId);
            return ApiResponse.success(setting);
        } catch (Exception e) {
            return ApiResponse.error("设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量设置值
     */
    @PutMapping("/batch")
    public ApiResponse<String> setSettingValues(@RequestBody Map<String, String> settings) {
        try {
            Long userId = getCurrentUserId();
            systemSettingService.setSettingValues(settings, userId);
            return ApiResponse.success("批量设置成功");
        } catch (Exception e) {
            return ApiResponse.error("批量设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建设置项
     */
    @PostMapping
    public ApiResponse<SystemSetting> createSetting(@RequestBody SystemSetting setting) {
        try {
            Long userId = getCurrentUserId();
            SystemSetting createdSetting = systemSettingService.createSetting(setting, userId);
            return ApiResponse.success(createdSetting);
        } catch (Exception e) {
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新设置项
     */
    @PutMapping("/{id}")
    public ApiResponse<SystemSetting> updateSetting(
            @PathVariable Long id, 
            @RequestBody SystemSetting setting) {
        try {
            Long userId = getCurrentUserId();
            SystemSetting updatedSetting = systemSettingService.updateSetting(id, setting, userId);
            return ApiResponse.success(updatedSetting);
        } catch (Exception e) {
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除设置项
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSetting(@PathVariable Long id) {
        try {
            systemSettingService.deleteSetting(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置为默认值
     */
    @PostMapping("/reset/{key}")
    public ApiResponse<SystemSetting> resetToDefault(@PathVariable String key) {
        try {
            Long userId = getCurrentUserId();
            SystemSetting setting = systemSettingService.resetToDefault(key, userId);
            return ApiResponse.success(setting);
        } catch (Exception e) {
            return ApiResponse.error("重置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取邮件配置
     */
    @GetMapping("/config/email")
    public ApiResponse<Map<String, String>> getEmailConfig() {
        Map<String, String> config = systemSettingService.getEmailConfig();
        return ApiResponse.success(config);
    }
    
    /**
     * 获取短信配置
     */
    @GetMapping("/config/sms")
    public ApiResponse<Map<String, String>> getSmsConfig() {
        Map<String, String> config = systemSettingService.getSmsConfig();
        return ApiResponse.success(config);
    }
    
    /**
     * 获取微信配置
     */
    @GetMapping("/config/wechat")
    public ApiResponse<Map<String, String>> getWechatConfig() {
        Map<String, String> config = systemSettingService.getWechatConfig();
        return ApiResponse.success(config);
    }
    
    /**
     * 获取钉钉配置
     */
    @GetMapping("/config/dingtalk")
    public ApiResponse<Map<String, String>> getDingtalkConfig() {
        Map<String, String> config = systemSettingService.getDingtalkConfig();
        return ApiResponse.success(config);
    }
    
    /**
     * 获取推送配置
     */
    @GetMapping("/config/push")
    public ApiResponse<Map<String, String>> getPushConfig() {
        Map<String, String> config = systemSettingService.getPushConfig();
        return ApiResponse.success(config);
    }
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private WechatService wechatService;
    
    @Autowired
    private DingtalkService dingtalkService;
    
    @Autowired
    private PushService pushService;
    
    /**
     * 测试邮件配置
     */
    @PostMapping("/test/email")
    public ApiResponse<String> testEmailConfig(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String content = request.get("content");
            
            if (email == null || content == null) {
                return ApiResponse.error("邮箱和内容不能为空");
            }
            
            boolean success = emailService.testEmailConfig(email, content);
            if (success) {
                return ApiResponse.success("邮件配置测试成功");
            } else {
                return ApiResponse.error("邮件配置测试失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("邮件配置测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试短信配置
     */
    @PostMapping("/test/sms")
    public ApiResponse<String> testSmsConfig(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String content = request.get("content");
            
            if (phone == null || content == null) {
                return ApiResponse.error("手机号和内容不能为空");
            }
            
            boolean success = smsService.testSmsConfig(phone, content);
            if (success) {
                return ApiResponse.success("短信配置测试成功");
            } else {
                return ApiResponse.error("短信配置测试失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("短信配置测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试微信配置
     */
    @PostMapping("/test/wechat")
    public ApiResponse<String> testWechatConfig(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String content = request.get("content");
            
            if (userId == null || content == null) {
                return ApiResponse.error("用户ID和内容不能为空");
            }
            
            boolean success = wechatService.testWechatConfig(userId, content);
            if (success) {
                return ApiResponse.success("微信配置测试成功");
            } else {
                return ApiResponse.error("微信配置测试失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("微信配置测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试钉钉配置
     */
    @PostMapping("/test/dingtalk")
    public ApiResponse<String> testDingtalkConfig(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String content = request.get("content");
            
            if (userId == null || content == null) {
                return ApiResponse.error("用户ID和内容不能为空");
            }
            
            boolean success = dingtalkService.testDingtalkConfig(userId, content);
            if (success) {
                return ApiResponse.success("钉钉配置测试成功");
            } else {
                return ApiResponse.error("钉钉配置测试失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("钉钉配置测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试推送配置
     */
    @PostMapping("/test/push")
    public ApiResponse<String> testPushConfig(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String title = request.get("title");
            String content = request.get("content");
            
            if (userId == null || title == null || content == null) {
                return ApiResponse.error("用户ID、标题和内容不能为空");
            }
            
            boolean success = pushService.testPushConfig(userId, title, content);
            if (success) {
                return ApiResponse.success("推送配置测试成功");
            } else {
                return ApiResponse.error("推送配置测试失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("推送配置测试失败: " + e.getMessage());
        }
    }
}
