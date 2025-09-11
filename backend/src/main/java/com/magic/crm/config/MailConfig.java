package com.magic.crm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

/**
 * 邮件配置
 */
@Configuration
public class MailConfig {
    
    @Autowired
    private com.magic.crm.service.SystemSettingService systemSettingService;
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // 从系统设置中获取邮件配置
        Map<String, String> config = systemSettingService.getEmailConfig();
        
        mailSender.setHost(config.get("smtpHost"));
        mailSender.setPort(Integer.parseInt(config.get("smtpPort")));
        mailSender.setUsername(config.get("smtpUsername"));
        mailSender.setPassword(config.get("smtpPassword"));
        
        // 设置SMTP属性
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", config.get("smtpAuth"));
        props.put("mail.smtp.starttls.enable", config.get("smtpStartTls"));
        props.put("mail.smtp.ssl.trust", config.get("smtpHost"));
        props.put("mail.debug", "false");
        
        return mailSender;
    }
}
