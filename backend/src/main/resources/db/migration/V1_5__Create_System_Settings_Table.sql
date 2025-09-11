-- 创建系统设置表（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'system_settings') THEN
        CREATE TABLE system_settings (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_name VARCHAR(200) NOT NULL,
    description TEXT,
    setting_type VARCHAR(20) NOT NULL,
    setting_group VARCHAR(50) NOT NULL,
    is_sensitive BOOLEAN NOT NULL DEFAULT FALSE,
    is_editable BOOLEAN NOT NULL DEFAULT TRUE,
    default_value TEXT,
    validation_rule TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL
        );
    END IF;
END $$;

-- 创建索引（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_system_settings_group') THEN
        CREATE INDEX idx_system_settings_group ON system_settings(setting_group);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_system_settings_type') THEN
        CREATE INDEX idx_system_settings_type ON system_settings(setting_type);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_system_settings_editable') THEN
        CREATE INDEX idx_system_settings_editable ON system_settings(is_editable);
    END IF;
END $$;

-- 插入默认系统设置（如果不存在）
INSERT INTO system_settings (setting_key, setting_value, setting_name, description, setting_type, setting_group, is_sensitive, is_editable, default_value, sort_order, created_at, updated_at, created_by, updated_by) 
SELECT * FROM (VALUES
-- 邮件配置
('email.smtp.host', '', 'SMTP服务器地址', '邮件服务器SMTP地址', 'STRING', 'email', false, true, '', 1, NOW(), NOW(), 1, 1),
('email.smtp.port', '587', 'SMTP端口', 'SMTP服务器端口', 'NUMBER', 'email', false, true, '587', 2, NOW(), NOW(), 1, 1),
('email.smtp.username', '', 'SMTP用户名', 'SMTP认证用户名', 'EMAIL', 'email', true, true, '', 3, NOW(), NOW(), 1, 1),
('email.smtp.password', '', 'SMTP密码', 'SMTP认证密码', 'PASSWORD', 'email', true, true, '', 4, NOW(), NOW(), 1, 1),
('email.smtp.auth', 'true', '启用SMTP认证', '是否启用SMTP认证', 'BOOLEAN', 'email', false, true, 'true', 5, NOW(), NOW(), 1, 1),
('email.smtp.starttls', 'true', '启用STARTTLS', '是否启用STARTTLS加密', 'BOOLEAN', 'email', false, true, 'true', 6, NOW(), NOW(), 1, 1),
('email.from.address', '', '发件人邮箱', '系统发送邮件的发件人地址', 'EMAIL', 'email', false, true, '', 7, NOW(), NOW(), 1, 1),
('email.from.name', 'CRM系统', '发件人名称', '系统发送邮件的发件人名称', 'STRING', 'email', false, true, 'CRM系统', 8, NOW(), NOW(), 1, 1),

-- 短信配置
('sms.provider', 'aliyun', '短信服务商', '短信服务提供商', 'STRING', 'sms', false, true, 'aliyun', 1, NOW(), NOW(), 1, 1),
('sms.aliyun.accessKeyId', '', '阿里云AccessKeyId', '阿里云短信服务AccessKeyId', 'STRING', 'sms', true, true, '', 2, NOW(), NOW(), 1, 1),
('sms.aliyun.accessKeySecret', '', '阿里云AccessKeySecret', '阿里云短信服务AccessKeySecret', 'PASSWORD', 'sms', true, true, '', 3, NOW(), NOW(), 1, 1),
('sms.aliyun.signName', '', '短信签名', '短信签名名称', 'STRING', 'sms', false, true, '', 4, NOW(), NOW(), 1, 1),
('sms.aliyun.templateCode', '', '短信模板代码', '短信模板代码', 'STRING', 'sms', false, true, '', 5, NOW(), NOW(), 1, 1),

-- 微信配置
('wechat.corpId', '', '企业微信CorpID', '企业微信CorpID', 'STRING', 'wechat', true, true, '', 1, NOW(), NOW(), 1, 1),
('wechat.corpSecret', '', '企业微信Secret', '企业微信应用Secret', 'PASSWORD', 'wechat', true, true, '', 2, NOW(), NOW(), 1, 1),
('wechat.agentId', '', '企业微信AgentID', '企业微信应用AgentID', 'STRING', 'wechat', false, true, '', 3, NOW(), NOW(), 1, 1),
('wechat.accessToken', '', '微信访问令牌', '微信访问令牌（自动获取）', 'PASSWORD', 'wechat', true, false, '', 4, NOW(), NOW(), 1, 1),

-- 钉钉配置
('dingtalk.appKey', '', '钉钉AppKey', '钉钉应用AppKey', 'STRING', 'dingtalk', true, true, '', 1, NOW(), NOW(), 1, 1),
('dingtalk.appSecret', '', '钉钉AppSecret', '钉钉应用AppSecret', 'PASSWORD', 'dingtalk', true, true, '', 2, NOW(), NOW(), 1, 1),
('dingtalk.accessToken', '', '钉钉访问令牌', '钉钉访问令牌（自动获取）', 'PASSWORD', 'dingtalk', true, false, '', 3, NOW(), NOW(), 1, 1),
('dingtalk.webhookUrl', '', '钉钉Webhook地址', '钉钉机器人Webhook地址', 'URL', 'dingtalk', true, true, '', 4, NOW(), NOW(), 1, 1),

-- 推送配置
('push.provider', 'jpush', '推送服务商', '推送服务提供商', 'STRING', 'push', false, true, 'jpush', 1, NOW(), NOW(), 1, 1),
('push.jpush.appKey', '', '极光推送AppKey', '极光推送应用AppKey', 'STRING', 'push', true, true, '', 2, NOW(), NOW(), 1, 1),
('push.jpush.masterSecret', '', '极光推送MasterSecret', '极光推送MasterSecret', 'PASSWORD', 'push', true, true, '', 3, NOW(), NOW(), 1, 1),
('push.jpush.isProduction', 'false', '生产环境', '是否为生产环境', 'BOOLEAN', 'push', false, true, 'false', 4, NOW(), NOW(), 1, 1),

-- 系统配置
('system.name', 'Magic CRM', '系统名称', '系统显示名称', 'STRING', 'system', false, true, 'Magic CRM', 1, NOW(), NOW(), 1, 1),
('system.version', '1.0.0', '系统版本', '系统版本号', 'STRING', 'system', false, false, '1.0.0', 2, NOW(), NOW(), 1, 1),
('system.logo', '', '系统Logo', '系统Logo URL', 'URL', 'system', false, true, '', 3, NOW(), NOW(), 1, 1),
('system.favicon', '', '系统图标', '系统Favicon URL', 'URL', 'system', false, true, '', 4, NOW(), NOW(), 1, 1),
('system.copyright', '© 2024 Magic CRM', '版权信息', '系统版权信息', 'STRING', 'system', false, true, '© 2024 Magic CRM', 5, NOW(), NOW(), 1, 1),

-- 安全配置
('security.session.timeout', '30', '会话超时时间', '用户会话超时时间（分钟）', 'NUMBER', 'security', false, true, '30', 1, NOW(), NOW(), 1, 1),
('security.password.minLength', '6', '密码最小长度', '用户密码最小长度', 'NUMBER', 'security', false, true, '6', 2, NOW(), NOW(), 1, 1),
('security.password.requireSpecialChar', 'false', '密码需要特殊字符', '密码是否必须包含特殊字符', 'BOOLEAN', 'security', false, true, 'false', 3, NOW(), NOW(), 1, 1),
('security.login.maxAttempts', '5', '最大登录尝试次数', '用户最大登录尝试次数', 'NUMBER', 'security', false, true, '5', 4, NOW(), NOW(), 1, 1),
('security.login.lockoutDuration', '30', '账户锁定时间', '账户锁定持续时间（分钟）', 'NUMBER', 'security', false, true, '30', 5, NOW(), NOW(), 1, 1),

-- 通知配置
('notification.retry.maxAttempts', '3', '通知重试最大次数', '通知发送失败时的最大重试次数', 'NUMBER', 'notification', false, true, '3', 1, NOW(), NOW(), 1, 1),
('notification.retry.delay', '60', '通知重试延迟', '通知重试延迟时间（秒）', 'NUMBER', 'notification', false, true, '60', 2, NOW(), NOW(), 1, 1),
('notification.cleanup.days', '30', '通知清理天数', '自动清理多少天前的通知', 'NUMBER', 'notification', false, true, '30', 3, NOW(), NOW(), 1, 1),
('notification.batch.size', '100', '批量发送大小', '批量发送通知的最大数量', 'NUMBER', 'notification', false, true, '100', 4, NOW(), NOW(), 1, 1),

-- 文件配置
('file.upload.maxSize', '10485760', '文件上传最大大小', '文件上传最大大小（字节）', 'NUMBER', 'file', false, true, '10485760', 1, NOW(), NOW(), 1, 1),
('file.upload.allowedTypes', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许上传的文件类型', '允许上传的文件类型（逗号分隔）', 'STRING', 'file', false, true, 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 2, NOW(), NOW(), 1, 1),
('file.storage.path', '/uploads', '文件存储路径', '文件存储根路径', 'STRING', 'file', false, true, '/uploads', 3, NOW(), NOW(), 1, 1),
('file.storage.url', 'http://localhost:8080/uploads', '文件访问URL', '文件访问基础URL', 'URL', 'file', false, true, 'http://localhost:8080/uploads', 4, NOW(), NOW(), 1, 1)
) AS t(setting_key, setting_value, setting_name, description, setting_type, setting_group, is_sensitive, is_editable, default_value, sort_order, created_at, updated_at, created_by, updated_by)
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE setting_key = t.setting_key);
