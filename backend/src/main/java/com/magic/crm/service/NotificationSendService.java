package com.magic.crm.service;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.NotificationChannel;
import com.magic.crm.entity.NotificationSettings;
import com.magic.crm.repository.NotificationRepository;
import com.magic.crm.repository.NotificationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知发送服务
 */
@Service
public class NotificationSendService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationSettingsRepository settingsRepository;
    
    @Autowired
    private NotificationService notificationService;
    
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
     * 异步发送通知
     */
    @Async
    public void sendNotificationAsync(Notification notification) {
        try {
            NotificationSettings settings = settingsRepository.findByUserId(notification.getUserId())
                    .orElse(new NotificationSettings(notification.getUserId()));
            
            boolean sent = false;
            String result = "";
            
            switch (notification.getChannel()) {
                case IN_SITE:
                    sent = sendInSiteNotification(notification);
                    result = sent ? "站内信发送成功" : "站内信发送失败";
                    break;
                case EMAIL:
                    if (settings.getEnableEmail()) {
                        sent = emailService.sendNotificationEmail(notification);
                        result = sent ? "邮件发送成功" : "邮件发送失败";
                    }
                    break;
                case SMS:
                    if (settings.getEnableSms()) {
                        sent = smsService.sendNotificationSms(notification);
                        result = sent ? "短信发送成功" : "短信发送失败";
                    }
                    break;
                case WECHAT:
                    if (settings.getEnableWechat()) {
                        sent = wechatService.sendNotificationWechat(notification);
                        result = sent ? "微信发送成功" : "微信发送失败";
                    }
                    break;
                case DINGTALK:
                    if (settings.getEnableDingtalk()) {
                        sent = dingtalkService.sendNotificationDingtalk(notification);
                        result = sent ? "钉钉发送成功" : "钉钉发送失败";
                    }
                    break;
                case PUSH:
                    if (settings.getEnablePush()) {
                        sent = pushService.sendNotificationPush(notification);
                        result = sent ? "推送发送成功" : "推送发送失败";
                    }
                    break;
            }
            
            // 更新发送状态
            notificationService.updateNotificationSendStatus(notification.getId(), sent, result);
            
        } catch (Exception e) {
            // 更新发送状态为失败
            notificationService.updateNotificationSendStatus(notification.getId(), false, 
                    "发送异常: " + e.getMessage());
        }
    }
    
    /**
     * 定时发送待发送的通知
     */
    @Scheduled(fixedDelay = 30000) // 每30秒执行一次
    public void sendPendingNotifications() {
        List<Notification> notifications = notificationService.getNotificationsToSend(3);
        
        for (Notification notification : notifications) {
            // 检查是否过期
            if (notification.isExpired()) {
                notification.setStatus(com.magic.crm.entity.NotificationStatus.ARCHIVED);
                notificationRepository.save(notification);
                continue;
            }
            
            // 异步发送通知
            sendNotificationAsync(notification);
        }
    }
    
    /**
     * 发送站内信通知
     */
    private boolean sendInSiteNotification(Notification notification) {
        // 站内信通知直接保存到数据库即可，前端会轮询获取
        return true;
    }
    
    
    /**
     * 清理过期通知
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanExpiredNotifications() {
        int count = notificationService.cleanExpiredNotifications();
        System.out.println("清理过期通知数量: " + count);
    }
}
