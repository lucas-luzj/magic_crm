-- 创建通知模板表（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notification_templates') THEN
        CREATE TABLE notification_templates (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(200) NOT NULL,
            code VARCHAR(100) NOT NULL UNIQUE,
            description TEXT,
            type VARCHAR(50) NOT NULL,
            channel VARCHAR(50) NOT NULL,
            title_template TEXT NOT NULL,
            content_template TEXT NOT NULL,
            variables TEXT,
            is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
            is_system BOOLEAN NOT NULL DEFAULT FALSE,
            created_by BIGINT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
        
        -- 创建索引
        CREATE INDEX idx_notification_templates_type ON notification_templates(type);
        CREATE INDEX idx_notification_templates_channel ON notification_templates(channel);
        CREATE INDEX idx_notification_templates_code ON notification_templates(code);
        CREATE INDEX idx_notification_templates_enabled ON notification_templates(is_enabled);
    END IF;
END $$;

-- 插入默认通知模板（如果不存在）
INSERT INTO notification_templates (name, code, description, type, channel, title_template, content_template, variables, is_enabled, is_system, created_by, created_at, updated_at) 
SELECT * FROM (VALUES
-- 工作流通知模板
('任务分配通知', 'TASK_ASSIGNED', '当任务分配给用户时发送的通知', 'TASK', 'IN_SITE', '新任务分配', '您有一个新任务需要处理：{{taskName}}\n\n任务描述：{{taskDescription}}\n\n截止时间：{{dueDate}}\n\n请及时处理。', '{"taskName": "任务名称", "taskDescription": "任务描述", "dueDate": "截止时间", "assignee": "处理人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('任务完成通知', 'TASK_COMPLETED', '当任务完成时发送的通知', 'TASK', 'IN_SITE', '任务已完成', '任务 "{{taskName}}" 已完成。\n\n完成时间：{{completeTime}}\n\n处理人：{{assignee}}', '{"taskName": "任务名称", "completeTime": "完成时间", "assignee": "处理人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('任务超时提醒', 'TASK_OVERDUE', '当任务超时时发送的提醒', 'TASK', 'IN_SITE', '任务超时提醒', '任务 "{{taskName}}" 已超时！\n\n原定截止时间：{{dueDate}}\n\n超时时间：{{overdueTime}}\n\n请尽快处理。', '{"taskName": "任务名称", "dueDate": "截止时间", "overdueTime": "超时时间", "assignee": "处理人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 工作流通知模板
('流程启动通知', 'PROCESS_STARTED', '当流程启动时发送的通知', 'WORKFLOW', 'IN_SITE', '流程已启动', '流程 "{{processName}}" 已成功启动。\n\n流程实例ID：{{processInstanceId}}\n\n启动时间：{{startTime}}\n\n发起人：{{initiator}', '{"processName": "流程名称", "processInstanceId": "流程实例ID", "startTime": "启动时间", "initiator": "发起人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('流程完成通知', 'PROCESS_COMPLETED', '当流程完成时发送的通知', 'WORKFLOW', 'IN_SITE', '流程已完成', '流程 "{{processName}}" 已完成。\n\n流程实例ID：{{processInstanceId}}\n\n完成时间：{{endTime}}\n\n发起人：{{initiator}', '{"processName": "流程名称", "processInstanceId": "流程实例ID", "endTime": "完成时间", "initiator": "发起人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('流程暂停通知', 'PROCESS_SUSPENDED', '当流程暂停时发送的通知', 'WORKFLOW', 'IN_SITE', '流程已暂停', '流程 "{{processName}}" 已暂停。\n\n流程实例ID：{{processInstanceId}}\n\n暂停时间：{{suspendTime}}\n\n暂停原因：{{reason}', '{"processName": "流程名称", "processInstanceId": "流程实例ID", "suspendTime": "暂停时间", "reason": "暂停原因"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 审批通知模板
('审批请求通知', 'APPROVAL_REQUEST', '当有审批请求时发送的通知', 'APPROVAL', 'IN_SITE', '待审批事项', '您有一个待审批事项：{{approvalTitle}}\n\n审批类型：{{approvalType}}\n\n申请人：{{applicant}}\n\n申请时间：{{requestTime}}\n\n请及时审批。', '{"approvalTitle": "审批标题", "approvalType": "审批类型", "applicant": "申请人", "requestTime": "申请时间"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('审批结果通知', 'APPROVAL_RESULT', '当审批完成时发送的通知', 'APPROVAL', 'IN_SITE', '审批结果通知', '您的审批申请 "{{approvalTitle}}" 已处理。\n\n审批结果：{{result}}\n\n审批人：{{approver}}\n\n审批时间：{{approvalTime}}\n\n审批意见：{{comment}', '{"approvalTitle": "审批标题", "result": "审批结果", "approver": "审批人", "approvalTime": "审批时间", "comment": "审批意见"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 系统通知模板
('系统维护通知', 'SYSTEM_MAINTENANCE', '系统维护时发送的通知', 'SYSTEM', 'IN_SITE', '系统维护通知', '系统将于 {{maintenanceTime}} 进行维护，预计维护时间 {{duration}}。\n\n维护期间系统将暂停服务，请提前保存工作。\n\n维护内容：{{maintenanceContent}}', '{"maintenanceTime": "维护时间", "duration": "维护时长", "maintenanceContent": "维护内容"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('系统升级通知', 'SYSTEM_UPGRADE', '系统升级时发送的通知', 'SYSTEM', 'IN_SITE', '系统升级通知', '系统已升级到版本 {{version}}。\n\n升级时间：{{upgradeTime}}\n\n新功能：{{newFeatures}}\n\n注意事项：{{notes}}', '{"version": "版本号", "upgradeTime": "升级时间", "newFeatures": "新功能", "notes": "注意事项"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 邮件通知模板
('邮件任务通知', 'EMAIL_TASK_NOTIFICATION', '通过邮件发送的任务通知', 'TASK', 'EMAIL', '【任务通知】{{taskName}}', '尊敬的 {{userName}}，\n\n您有一个新任务需要处理：\n\n任务名称：{{taskName}}\n任务描述：{{taskDescription}}\n截止时间：{{dueDate}}\n\n请登录系统及时处理。\n\n此邮件由系统自动发送，请勿回复。', '{"userName": "用户姓名", "taskName": "任务名称", "taskDescription": "任务描述", "dueDate": "截止时间"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('邮件流程通知', 'EMAIL_PROCESS_NOTIFICATION', '通过邮件发送的流程通知', 'WORKFLOW', 'EMAIL', '【流程通知】{{processName}}', '尊敬的 {{userName}}，\n\n流程 "{{processName}}" 状态更新：\n\n当前状态：{{status}}\n更新时间：{{updateTime}}\n\n请登录系统查看详情。\n\n此邮件由系统自动发送，请勿回复。', '{"userName": "用户姓名", "processName": "流程名称", "status": "状态", "updateTime": "更新时间"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 短信通知模板
('短信紧急通知', 'SMS_URGENT_NOTIFICATION', '通过短信发送的紧急通知', 'SYSTEM', 'SMS', '【紧急通知】', '【{{systemName}}】{{message}} 时间：{{time}} 详情请登录系统查看。', '{"systemName": "系统名称", "message": "通知内容", "time": "时间"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 微信通知模板
('微信任务通知', 'WECHAT_TASK_NOTIFICATION', '通过微信发送的任务通知', 'TASK', 'WECHAT', '任务通知', '您有一个新任务：{{taskName}}\n截止时间：{{dueDate}}\n\n点击查看详情', '{"taskName": "任务名称", "dueDate": "截止时间"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 钉钉通知模板
('钉钉审批通知', 'DINGTALK_APPROVAL_NOTIFICATION', '通过钉钉发送的审批通知', 'APPROVAL', 'DINGTALK', '审批通知', '【审批通知】\n\n审批事项：{{approvalTitle}}\n申请人：{{applicant}}\n申请时间：{{requestTime}}\n\n请及时处理。', '{"approvalTitle": "审批标题", "applicant": "申请人", "requestTime": "申请时间"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 客户通知模板
('客户跟进提醒', 'CUSTOMER_FOLLOW_UP', '客户跟进提醒通知', 'CUSTOMER', 'IN_SITE', '客户跟进提醒', '客户 "{{customerName}}" 需要跟进。\n\n上次跟进时间：{{lastFollowTime}}\n\n提醒时间：{{remindTime}}\n\n跟进人：{{followUser}}', '{"customerName": "客户名称", "lastFollowTime": "上次跟进时间", "remindTime": "提醒时间", "followUser": "跟进人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('客户生日提醒', 'CUSTOMER_BIRTHDAY', '客户生日提醒通知', 'CUSTOMER', 'IN_SITE', '客户生日提醒', '客户 "{{customerName}}" 今天生日！\n\n生日：{{birthday}}\n\n客户等级：{{customerLevel}}\n\n建议：{{suggestion}}', '{"customerName": "客户名称", "birthday": "生日", "customerLevel": "客户等级", "suggestion": "建议"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 销售通知模板
('销售机会提醒', 'SALES_OPPORTUNITY', '销售机会提醒通知', 'SALES', 'IN_SITE', '销售机会提醒', '销售机会 "{{opportunityName}}" 需要关注。\n\n预计成交时间：{{expectedCloseDate}}\n\n金额：{{amount}}\n\n负责人：{{owner}}', '{"opportunityName": "机会名称", "expectedCloseDate": "预计成交时间", "amount": "金额", "owner": "负责人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('合同到期提醒', 'CONTRACT_EXPIRY', '合同到期提醒通知', 'SALES', 'IN_SITE', '合同到期提醒', '合同 "{{contractName}}" 即将到期。\n\n到期时间：{{expiryDate}}\n\n客户：{{customerName}}\n\n负责人：{{owner}}', '{"contractName": "合同名称", "expiryDate": "到期时间", "customerName": "客户名称", "owner": "负责人"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 营销通知模板
('营销活动通知', 'MARKETING_CAMPAIGN', '营销活动通知', 'MARKETING', 'IN_SITE', '营销活动通知', '新的营销活动 "{{campaignName}}" 已启动。\n\n活动时间：{{startDate}} - {{endDate}}\n\n活动类型：{{campaignType}}\n\n参与方式：{{participationMethod}}', '{"campaignName": "活动名称", "startDate": "开始时间", "endDate": "结束时间", "campaignType": "活动类型", "participationMethod": "参与方式"}', true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
) AS t(name, code, description, type, channel, title_template, content_template, variables, is_enabled, is_system, created_by, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE code = t.code);
