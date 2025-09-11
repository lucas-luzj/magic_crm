-- 创建通知表（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notifications') THEN
        CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sender_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UNREAD',
    priority VARCHAR(50) DEFAULT 'NORMAL',
    business_id VARCHAR(100),
    business_type VARCHAR(100),
    action_url VARCHAR(500),
    expire_time TIMESTAMP,
    is_sent BOOLEAN DEFAULT FALSE,
    sent_time TIMESTAMP,
    send_result TEXT,
    retry_count INTEGER DEFAULT 0,
    extra_data TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_time TIMESTAMP
        );
    END IF;
END $$;

-- 创建通知设置表（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notification_settings') THEN
        CREATE TABLE notification_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    enable_in_site BOOLEAN DEFAULT TRUE,
    enable_email BOOLEAN DEFAULT TRUE,
    enable_sms BOOLEAN DEFAULT FALSE,
    enable_wechat BOOLEAN DEFAULT FALSE,
    enable_dingtalk BOOLEAN DEFAULT FALSE,
    enable_push BOOLEAN DEFAULT TRUE,
    workflow_notifications TEXT,
    task_notifications TEXT,
    system_notifications TEXT,
    email_notification_time VARCHAR(20) DEFAULT '09:00-18:00',
    sms_notification_time VARCHAR(20) DEFAULT '09:00-18:00',
    quiet_hours_start VARCHAR(10) DEFAULT '22:00',
    quiet_hours_end VARCHAR(10) DEFAULT '08:00',
    enable_weekend_notifications BOOLEAN DEFAULT FALSE,
    enable_holiday_notifications BOOLEAN DEFAULT FALSE,
    frequency_limit_minutes INTEGER DEFAULT 5,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    END IF;
END $$;

-- 创建索引（如果不存在）
DO $$
BEGIN
    -- notifications 表索引
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_user_id') THEN
        CREATE INDEX idx_notifications_user_id ON notifications(user_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_status') THEN
        CREATE INDEX idx_notifications_status ON notifications(status);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_type') THEN
        CREATE INDEX idx_notifications_type ON notifications(type);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_channel') THEN
        CREATE INDEX idx_notifications_channel ON notifications(channel);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_created_at') THEN
        CREATE INDEX idx_notifications_created_at ON notifications(created_at);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_business') THEN
        CREATE INDEX idx_notifications_business ON notifications(business_id, business_type);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_send_status') THEN
        CREATE INDEX idx_notifications_send_status ON notifications(is_sent, retry_count);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notifications_expire_time') THEN
        CREATE INDEX idx_notifications_expire_time ON notifications(expire_time);
    END IF;
    
    -- notification_settings 表索引
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_notification_settings_user_id') THEN
        CREATE INDEX idx_notification_settings_user_id ON notification_settings(user_id);
    END IF;
END $$;

-- 添加外键约束（如果不存在）
DO $$
BEGIN
    -- notifications 表外键约束
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'fk_notifications_user_id') THEN
        ALTER TABLE notifications ADD CONSTRAINT fk_notifications_user_id 
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'fk_notifications_sender_id') THEN
        ALTER TABLE notifications ADD CONSTRAINT fk_notifications_sender_id 
            FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL;
    END IF;
    
    -- notification_settings 表外键约束
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'fk_notification_settings_user_id') THEN
        ALTER TABLE notification_settings ADD CONSTRAINT fk_notification_settings_user_id 
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

-- 添加注释
COMMENT ON TABLE notifications IS '通知表';
COMMENT ON COLUMN notifications.user_id IS '接收用户ID';
COMMENT ON COLUMN notifications.sender_id IS '发送用户ID';
COMMENT ON COLUMN notifications.title IS '通知标题';
COMMENT ON COLUMN notifications.content IS '通知内容';
COMMENT ON COLUMN notifications.type IS '通知类型';
COMMENT ON COLUMN notifications.channel IS '通知渠道';
COMMENT ON COLUMN notifications.status IS '通知状态';
COMMENT ON COLUMN notifications.priority IS '优先级';
COMMENT ON COLUMN notifications.business_id IS '关联的业务ID';
COMMENT ON COLUMN notifications.business_type IS '关联的业务类型';
COMMENT ON COLUMN notifications.action_url IS '跳转链接';
COMMENT ON COLUMN notifications.expire_time IS '过期时间';
COMMENT ON COLUMN notifications.is_sent IS '是否已发送';
COMMENT ON COLUMN notifications.sent_time IS '发送时间';
COMMENT ON COLUMN notifications.send_result IS '发送结果';
COMMENT ON COLUMN notifications.retry_count IS '重试次数';
COMMENT ON COLUMN notifications.extra_data IS '扩展数据';
COMMENT ON COLUMN notifications.read_time IS '已读时间';

COMMENT ON TABLE notification_settings IS '通知设置表';
COMMENT ON COLUMN notification_settings.user_id IS '用户ID';
COMMENT ON COLUMN notification_settings.enable_in_site IS '是否启用站内信通知';
COMMENT ON COLUMN notification_settings.enable_email IS '是否启用邮件通知';
COMMENT ON COLUMN notification_settings.enable_sms IS '是否启用短信通知';
COMMENT ON COLUMN notification_settings.enable_wechat IS '是否启用微信通知';
COMMENT ON COLUMN notification_settings.enable_dingtalk IS '是否启用钉钉通知';
COMMENT ON COLUMN notification_settings.enable_push IS '是否启用推送通知';
COMMENT ON COLUMN notification_settings.workflow_notifications IS '工作流通知设置';
COMMENT ON COLUMN notification_settings.task_notifications IS '任务通知设置';
COMMENT ON COLUMN notification_settings.system_notifications IS '系统通知设置';
COMMENT ON COLUMN notification_settings.email_notification_time IS '邮件通知时间';
COMMENT ON COLUMN notification_settings.sms_notification_time IS '短信通知时间';
COMMENT ON COLUMN notification_settings.quiet_hours_start IS '免打扰时间开始';
COMMENT ON COLUMN notification_settings.quiet_hours_end IS '免打扰时间结束';
COMMENT ON COLUMN notification_settings.enable_weekend_notifications IS '是否在周末接收通知';
COMMENT ON COLUMN notification_settings.enable_holiday_notifications IS '是否在节假日接收通知';
COMMENT ON COLUMN notification_settings.frequency_limit_minutes IS '通知频率限制（分钟）';
