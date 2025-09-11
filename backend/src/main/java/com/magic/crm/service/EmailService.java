package com.magic.crm.service;

import com.magic.crm.entity.Notification;
import com.magic.crm.entity.User;
import com.magic.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

/**
 * 邮件服务
 */
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private SystemSettingService systemSettingService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 发送简单邮件
     */
    public boolean sendSimpleEmail(String to, String subject, String content) {
        try {
            Map<String, String> config = systemSettingService.getEmailConfig();
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(config.get("fromEmail"));
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("发送简单邮件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送HTML邮件
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            Map<String, String> config = systemSettingService.getEmailConfig();
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(config.get("fromEmail"), config.get("fromName"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            System.err.println("发送HTML邮件失败: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("发送HTML邮件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送通知邮件
     */
    public boolean sendNotificationEmail(Notification notification) {
        try {
            User user = userRepository.findById(notification.getUserId()).orElse(null);
            if (user == null || !StringUtils.hasText(user.getEmail())) {
                System.err.println("用户不存在或邮箱为空: " + notification.getUserId());
                return false;
            }
            
            String subject = notification.getTitle();
            String content = buildEmailContent(notification, user);
            
            return sendHtmlEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            System.err.println("发送通知邮件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建邮件内容
     */
    private String buildEmailContent(Notification notification, User user) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>").append(notification.getTitle()).append("</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px; }");
        html.append(".content { padding: 20px; background: white; border: 1px solid #dee2e6; border-radius: 5px; }");
        html.append(".footer { margin-top: 20px; padding: 10px; text-align: center; color: #666; font-size: 12px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h2>").append(notification.getTitle()).append("</h2>");
        html.append("</div>");
        html.append("<div class='content'>");
        html.append("<p>尊敬的 ").append(user.getFullName()).append("，</p>");
        html.append("<p>").append(notification.getContent()).append("</p>");
        
        if (StringUtils.hasText(notification.getActionUrl())) {
            html.append("<p><a href='").append(notification.getActionUrl()).append("' style='color: #007bff; text-decoration: none;'>点击查看详情</a></p>");
        }
        
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>此邮件由系统自动发送，请勿回复。</p>");
        html.append("<p>发送时间: ").append(notification.getCreatedAt()).append("</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * 测试邮件配置
     */
    public boolean testEmailConfig(String to, String content) {
        try {
            String subject = "邮件配置测试";
            return sendSimpleEmail(to, subject, content);
        } catch (Exception e) {
            System.err.println("测试邮件配置失败: " + e.getMessage());
            return false;
        }
    }
}
