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
 * 微信服务
 */
@Service
public class WechatService {
    
    @Autowired
    private SystemSettingService systemSettingService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 发送微信消息
     */
    public boolean sendWechatMessage(String userId, String content) {
        try {
            Map<String, String> config = systemSettingService.getWechatConfig();
            
            if (!StringUtils.hasText(userId) || !StringUtils.hasText(content)) {
                System.err.println("用户ID或消息内容为空");
                return false;
            }
            
            // 获取访问令牌
            String accessToken = getAccessToken(config);
            if (!StringUtils.hasText(accessToken)) {
                System.err.println("获取微信访问令牌失败");
                return false;
            }
            
            // 构建消息
            Map<String, Object> message = new HashMap<>();
            message.put("touser", userId);
            message.put("msgtype", "text");
            
            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            message.put("text", text);
            
            // 发送消息
            String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken;
            
            Map<String, Object> response = restTemplate.postForObject(url, message, Map.class);
            
            if (response != null && Integer.valueOf(0).equals(response.get("errcode"))) {
                System.out.println("微信消息发送成功: " + userId);
                return true;
            } else {
                System.err.println("微信消息发送失败: " + response);
                return false;
            }
        } catch (Exception e) {
            System.err.println("发送微信消息异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送通知微信消息
     */
    public boolean sendNotificationWechat(Notification notification) {
        try {
            User user = userRepository.findById(notification.getUserId()).orElse(null);
            if (user == null || !StringUtils.hasText(user.getWechatOpenId())) {
                System.err.println("用户不存在或微信OpenID为空: " + notification.getUserId());
                return false;
            }
            
            String content = buildWechatContent(notification, user);
            return sendWechatMessage(user.getWechatOpenId(), content);
        } catch (Exception e) {
            System.err.println("发送通知微信消息失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建微信消息内容
     */
    private String buildWechatContent(Notification notification, User user) {
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
     * 获取微信访问令牌
     */
    private String getAccessToken(Map<String, String> config) {
        try {
            // 先从缓存中获取
            String cachedToken = config.get("accessToken");
            if (StringUtils.hasText(cachedToken)) {
                return cachedToken;
            }
            
            // 从微信API获取新的访问令牌
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + config.get("corpId") + 
                        "&corpsecret=" + config.get("corpSecret");
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && Integer.valueOf(0).equals(response.get("errcode"))) {
                String accessToken = (String) response.get("access_token");
                
                // 更新缓存（这里简化处理，实际应该缓存到Redis等）
                systemSettingService.setSettingValue("wechat.accessToken", accessToken, 1L);
                
                return accessToken;
            } else {
                System.err.println("获取微信访问令牌失败: " + response);
                return null;
            }
        } catch (Exception e) {
            System.err.println("获取微信访问令牌异常: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 测试微信配置
     */
    public boolean testWechatConfig(String userId, String content) {
        try {
            return sendWechatMessage(userId, content);
        } catch (Exception e) {
            System.err.println("测试微信配置失败: " + e.getMessage());
            return false;
        }
    }
}
