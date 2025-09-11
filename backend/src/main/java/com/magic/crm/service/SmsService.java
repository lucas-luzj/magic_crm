package com.magic.crm.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.magic.crm.entity.Notification;
import com.magic.crm.entity.User;
import com.magic.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 短信服务
 */
@Service
public class SmsService {
    
    @Autowired
    private SystemSettingService systemSettingService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 发送短信
     */
    public boolean sendSms(String phoneNumber, String content) {
        try {
            Map<String, String> config = systemSettingService.getSmsConfig();
            
            if (!StringUtils.hasText(phoneNumber) || !StringUtils.hasText(content)) {
                System.err.println("手机号或短信内容为空");
                return false;
            }
            
            // 创建阿里云短信客户端
            Client client = createSmsClient(config);
            
            // 构建发送请求
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phoneNumber);
            request.setSignName(config.get("signName"));
            request.setTemplateCode(config.get("templateCode"));
            request.setTemplateParam("{\"content\":\"" + content + "\"}");
            
            // 发送短信
            SendSmsResponse response = client.sendSms(request);
            
            if ("OK".equals(response.getBody().getCode())) {
                System.out.println("短信发送成功: " + phoneNumber);
                return true;
            } else {
                System.err.println("短信发送失败: " + response.getBody().getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("发送短信异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送通知短信
     */
    public boolean sendNotificationSms(Notification notification) {
        try {
            User user = userRepository.findById(notification.getUserId()).orElse(null);
            if (user == null || !StringUtils.hasText(user.getPhoneNumber())) {
                System.err.println("用户不存在或手机号为空: " + notification.getUserId());
                return false;
            }
            
            String content = buildSmsContent(notification, user);
            return sendSms(user.getPhoneNumber(), content);
        } catch (Exception e) {
            System.err.println("发送通知短信失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建短信内容
     */
    private String buildSmsContent(Notification notification, User user) {
        StringBuilder content = new StringBuilder();
        content.append("【").append(systemSettingService.getSettingValue("system.name", "CRM系统")).append("】");
        content.append(notification.getTitle());
        content.append("：");
        content.append(notification.getContent());
        
        // 限制短信长度
        String result = content.toString();
        if (result.length() > 500) {
            result = result.substring(0, 500) + "...";
        }
        
        return result;
    }
    
    /**
     * 创建阿里云短信客户端
     */
    private Client createSmsClient(Map<String, String> config) throws Exception {
        Config clientConfig = new Config()
                .setAccessKeyId(config.get("accessKeyId"))
                .setAccessKeySecret(config.get("accessKeySecret"))
                .setEndpoint("dysmsapi.aliyuncs.com");
        
        return new Client(clientConfig);
    }
    
    /**
     * 测试短信配置
     */
    public boolean testSmsConfig(String phoneNumber, String content) {
        try {
            return sendSms(phoneNumber, content);
        } catch (Exception e) {
            System.err.println("测试短信配置失败: " + e.getMessage());
            return false;
        }
    }
}
