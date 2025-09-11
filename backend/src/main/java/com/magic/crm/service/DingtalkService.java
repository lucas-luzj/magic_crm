package com.magic.crm.service;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.User;
import com.magic.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉服务
 */
@Service
public class DingtalkService {
    
    @Autowired
    private SystemSettingService systemSettingService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 发送钉钉消息
     */
    public boolean sendDingtalkMessage(String userId, String content) {
        try {
            Map<String, String> config = systemSettingService.getDingtalkConfig();
            
            if (!StringUtils.hasText(userId) || !StringUtils.hasText(content)) {
                System.err.println("用户ID或消息内容为空");
                return false;
            }
            
            // 构建消息
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");
            
            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            message.put("text", text);
            
            // 发送消息到钉钉机器人
            String webhookUrl = config.get("webhookUrl");
            if (StringUtils.hasText(webhookUrl)) {
                return sendToWebhook(webhookUrl, message);
            } else {
                // 使用钉钉API发送
                return sendToUser(userId, message, config);
            }
        } catch (Exception e) {
            System.err.println("发送钉钉消息异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送通知钉钉消息
     */
    public boolean sendNotificationDingtalk(Notification notification) {
        try {
            User user = userRepository.findById(notification.getUserId()).orElse(null);
            if (user == null || !StringUtils.hasText(user.getDingtalkId())) {
                System.err.println("用户不存在或钉钉ID为空: " + notification.getUserId());
                return false;
            }
            
            String content = buildDingtalkContent(notification, user);
            return sendDingtalkMessage(user.getDingtalkId(), content);
        } catch (Exception e) {
            System.err.println("发送通知钉钉消息失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建钉钉消息内容
     */
    private String buildDingtalkContent(Notification notification, User user) {
        StringBuilder content = new StringBuilder();
        content.append("【").append(systemSettingService.getSettingValue("system.name", "CRM系统")).append("】\n");
        content.append("标题：").append(notification.getTitle()).append("\n");
        content.append("内容：").append(notification.getContent()).append("\n");
        content.append("时间：").append(notification.getCreatedAt()).append("\n");
        
        if (StringUtils.hasText(notification.getActionUrl())) {
            content.append("详情：").append(notification.getActionUrl());
        }
        
        return content.toString();
    }
    
    /**
     * 通过Webhook发送消息
     */
    private boolean sendToWebhook(String webhookUrl, Map<String, Object> message) {
        try {
            Map<String, Object> response = restTemplate.postForObject(webhookUrl, message, Map.class);
            
            if (response != null && Integer.valueOf(0).equals(response.get("errcode"))) {
                System.out.println("钉钉Webhook消息发送成功");
                return true;
            } else {
                System.err.println("钉钉Webhook消息发送失败: " + response);
                return false;
            }
        } catch (Exception e) {
            System.err.println("钉钉Webhook消息发送异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 通过钉钉API发送消息给用户
     */
    private boolean sendToUser(String userId, Map<String, Object> message, Map<String, String> config) {
        try {
            // 获取访问令牌
            String accessToken = getAccessToken(config);
            if (!StringUtils.hasText(accessToken)) {
                System.err.println("获取钉钉访问令牌失败");
                return false;
            }
            
            // 发送消息
            String url = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;
            
            Map<String, Object> request = new HashMap<>();
            request.put("agent_id", config.get("agentId"));
            request.put("userid_list", userId);
            request.put("msg", message);
            
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                System.out.println("钉钉API消息发送成功: " + userId);
                return true;
            } else {
                System.err.println("钉钉API消息发送失败: " + response);
                return false;
            }
        } catch (Exception e) {
            System.err.println("钉钉API消息发送异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取钉钉访问令牌
     */
    private String getAccessToken(Map<String, String> config) {
        try {
            // 先从缓存中获取
            String cachedToken = config.get("accessToken");
            if (StringUtils.hasText(cachedToken)) {
                return cachedToken;
            }
            
            // 从钉钉API获取新的访问令牌
            String url = "https://oapi.dingtalk.com/gettoken?appkey=" + config.get("appKey") + 
                        "&appsecret=" + config.get("appSecret");
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && Integer.valueOf(0).equals(response.get("errcode"))) {
                String accessToken = (String) response.get("access_token");
                
                // 更新缓存
                systemSettingService.setSettingValue("dingtalk.accessToken", accessToken, 1L);
                
                return accessToken;
            } else {
                System.err.println("获取钉钉访问令牌失败: " + response);
                return null;
            }
        } catch (Exception e) {
            System.err.println("获取钉钉访问令牌异常: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 测试钉钉配置
     */
    public boolean testDingtalkConfig(String userId, String content) {
        try {
            return sendDingtalkMessage(userId, content);
        } catch (Exception e) {
            System.err.println("测试钉钉配置失败: " + e.getMessage());
            return false;
        }
    }
}
