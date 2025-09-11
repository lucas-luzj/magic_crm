-- 完整的表单管理系统数据库结构
-- 创建时间: 2025-01-05
-- 描述: 支持动态表单设计、流程关联、数据存储和版本管理的完整表单系统

-- =====================================================
-- 1. 表单模板表 - 存储表单的基本信息和配置
-- =====================================================
CREATE TABLE IF NOT EXISTS form_template (
    id BIGSERIAL PRIMARY KEY,
    form_key VARCHAR(100) NOT NULL UNIQUE,
    form_name VARCHAR(200) NOT NULL,
    form_description TEXT,
    form_config JSONB NOT NULL,
    form_version INTEGER DEFAULT 1,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT')),
    category VARCHAR(50),
    tags VARCHAR(500),
    created_by VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. 表单字段表 - 存储表单的详细字段定义
-- =====================================================
CREATE TABLE IF NOT EXISTS form_field (
    id BIGSERIAL PRIMARY KEY,
    form_template_id BIGINT NOT NULL,
    field_key VARCHAR(100) NOT NULL,
    field_name VARCHAR(200) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    field_config JSONB,
    sort_order INTEGER DEFAULT 0,
    is_required BOOLEAN DEFAULT FALSE,
    is_readonly BOOLEAN DEFAULT FALSE,
    is_visible BOOLEAN DEFAULT TRUE,
    validation_rules JSONB,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (form_template_id) REFERENCES form_template(id) ON DELETE CASCADE
);

-- =====================================================
-- 3. 流程表单映射表 - 定义流程节点与表单的关联关系
-- =====================================================
CREATE TABLE IF NOT EXISTS process_form_mapping (
    id BIGSERIAL PRIMARY KEY,
    process_definition_key VARCHAR(255) NOT NULL,
    task_definition_key VARCHAR(255),
    form_template_id BIGINT NOT NULL,
    mapping_type VARCHAR(20) DEFAULT 'TASK' CHECK (mapping_type IN ('START', 'TASK', 'END')),
    form_version INTEGER DEFAULT 1,
    is_default BOOLEAN DEFAULT FALSE,
    form_position VARCHAR(20) DEFAULT 'BEFORE' CHECK (form_position IN ('BEFORE', 'AFTER', 'REPLACE')),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    FOREIGN KEY (form_template_id) REFERENCES form_template(id) ON DELETE CASCADE
);

-- =====================================================
-- 4. 表单数据实例表 - 存储用户提交的表单数据
-- =====================================================
CREATE TABLE IF NOT EXISTS form_data_instance (
    id BIGSERIAL PRIMARY KEY,
    form_template_id BIGINT NOT NULL,
    process_instance_id VARCHAR(64),
    task_id VARCHAR(64),
    business_key VARCHAR(200),
    form_data JSONB NOT NULL,
    submit_user VARCHAR(100),
    submit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SUBMITTED' CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED')),
    FOREIGN KEY (form_template_id) REFERENCES form_template(id)
);

-- =====================================================
-- 5. 表单版本管理表 - 管理表单模板的版本历史
-- =====================================================
CREATE TABLE IF NOT EXISTS form_template_version (
    id BIGSERIAL PRIMARY KEY,
    form_template_id BIGINT NOT NULL,
    version_number INTEGER NOT NULL,
    form_config JSONB NOT NULL,
    change_log TEXT,
    created_by VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (form_template_id) REFERENCES form_template(id) ON DELETE CASCADE,
    UNIQUE(form_template_id, version_number)
);

-- =====================================================
-- 6. 表单使用统计表 - 统计表单的使用情况
-- =====================================================
CREATE TABLE IF NOT EXISTS form_usage_statistics (
    id BIGSERIAL PRIMARY KEY,
    form_template_id BIGINT NOT NULL,
    process_definition_key VARCHAR(255),
    usage_count INTEGER DEFAULT 0,
    last_used_time TIMESTAMP,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (form_template_id) REFERENCES form_template(id) ON DELETE CASCADE
);


-- =====================================================
-- 创建索引以优化查询性能
-- =====================================================

-- 表单模板索引
CREATE INDEX idx_form_template_key ON form_template(form_key);
CREATE INDEX idx_form_template_status ON form_template(status);
CREATE INDEX idx_form_template_category ON form_template(category);
CREATE INDEX idx_form_template_created_time ON form_template(created_time);

-- 表单字段索引
CREATE INDEX idx_form_field_template_id ON form_field(form_template_id);
CREATE INDEX idx_form_field_key ON form_field(field_key);
CREATE INDEX idx_form_field_type ON form_field(field_type);

-- 流程表单映射索引
CREATE INDEX idx_process_form_mapping_process ON process_form_mapping(process_definition_key);
CREATE INDEX idx_process_form_mapping_task ON process_form_mapping(task_definition_key);
CREATE INDEX idx_process_form_mapping_form ON process_form_mapping(form_template_id);

-- 表单数据实例索引
CREATE INDEX idx_form_data_instance_form_template ON form_data_instance(form_template_id);
CREATE INDEX idx_form_data_instance_process ON form_data_instance(process_instance_id);
CREATE INDEX idx_form_data_instance_task ON form_data_instance(task_id);
CREATE INDEX idx_form_data_instance_business_key ON form_data_instance(business_key);
CREATE INDEX idx_form_data_instance_submit_time ON form_data_instance(submit_time);
CREATE INDEX idx_form_data_instance_submit_user ON form_data_instance(submit_user);

-- 版本管理索引
CREATE INDEX idx_form_version_template_id ON form_template_version(form_template_id);
CREATE INDEX idx_form_version_number ON form_template_version(version_number);

-- 使用统计索引
CREATE INDEX idx_form_usage_template_id ON form_usage_statistics(form_template_id);
CREATE INDEX idx_form_usage_process_key ON form_usage_statistics(process_definition_key);

-- 分类管理索引
CREATE INDEX idx_form_category_key ON form_category(category_key);
CREATE INDEX idx_form_category_parent ON form_category(parent_id);

-- =====================================================
-- 创建唯一约束
-- =====================================================

-- 确保每个流程任务只有一个默认表单
CREATE UNIQUE INDEX idx_unique_default_form 
ON process_form_mapping (process_definition_key, task_definition_key, is_default) 
WHERE is_default = TRUE;

-- =====================================================
-- 插入基础数据
-- =====================================================



-- 插入示例表单模板
INSERT INTO form_template (form_key, form_name, form_description, form_config, category, created_by, status) VALUES
-- 请假申请表单
('leave_application_form', '请假申请表单', '员工请假申请使用的表单', '{
  "fields": [
    {
      "id": "leaveType",
      "label": "请假类型",
      "type": "select",
      "required": true,
      "options": "annual|年假\nsick|病假\npersonal|事假\nmaternity|产假\nother|其他",
      "placeholder": "请选择请假类型",
      "visible": true,
      "readonly": false
    },
    {
      "id": "startDate",
      "label": "开始日期",
      "type": "date",
      "required": true,
      "placeholder": "请选择开始日期",
      "visible": true,
      "readonly": false
    },
    {
      "id": "endDate",
      "label": "结束日期",
      "type": "date",
      "required": true,
      "placeholder": "请选择结束日期",
      "visible": true,
      "readonly": false
    },
    {
      "id": "days",
      "label": "请假天数",
      "type": "number",
      "required": true,
      "placeholder": "请输入请假天数",
      "visible": true,
      "readonly": false,
      "min": 0.5,
      "max": 365,
      "step": 0.5
    },
    {
      "id": "reason",
      "label": "请假原因",
      "type": "textarea",
      "required": true,
      "placeholder": "请详细说明请假原因",
      "visible": true,
      "readonly": false,
      "maxLength": 500
    },
    {
      "id": "emergencyContact",
      "label": "紧急联系人",
      "type": "text",
      "required": false,
      "placeholder": "请输入紧急联系人姓名和电话",
      "visible": true,
      "readonly": false
    }
  ]
}', 'hr', 'system', 'ACTIVE'),

-- 费用报销表单
('expense_reimbursement_form', '费用报销表单', '员工费用报销申请表单', '{
  "fields": [
    {
      "id": "expenseType",
      "label": "费用类型",
      "type": "select",
      "required": true,
      "options": "travel|差旅费\noffice|办公用品\ntraining|培训费\nmeal|餐费\ntransport|交通费\nother|其他",
      "placeholder": "请选择费用类型",
      "visible": true,
      "readonly": false
    },
    {
      "id": "amount",
      "label": "报销金额",
      "type": "number",
      "required": true,
      "placeholder": "请输入报销金额",
      "visible": true,
      "readonly": false,
      "min": 0,
      "step": 0.01
    },
    {
      "id": "currency",
      "label": "币种",
      "type": "select",
      "required": true,
      "options": "CNY|人民币\nUSD|美元\nEUR|欧元",
      "defaultValue": "CNY",
      "visible": true,
      "readonly": false
    },
    {
      "id": "expenseDate",
      "label": "费用发生日期",
      "type": "date",
      "required": true,
      "placeholder": "请选择费用发生日期",
      "visible": true,
      "readonly": false
    },
    {
      "id": "description",
      "label": "费用说明",
      "type": "textarea",
      "required": true,
      "placeholder": "请详细说明费用用途和明细",
      "visible": true,
      "readonly": false,
      "maxLength": 1000
    },
    {
      "id": "attachments",
      "label": "附件上传",
      "type": "file",
      "required": true,
      "placeholder": "请上传相关票据（发票、收据等）",
      "visible": true,
      "readonly": false,
      "accept": ".jpg,.jpeg,.png,.pdf,.doc,.docx"
    }
  ]
}', 'finance', 'system', 'ACTIVE'),

-- 采购申请表单
('purchase_request_form', '采购申请表单', '物品采购申请表单', '{
  "fields": [
    {
      "id": "itemName",
      "label": "采购物品名称",
      "type": "text",
      "required": true,
      "placeholder": "请输入采购物品名称",
      "visible": true,
      "readonly": false
    },
    {
      "id": "category",
      "label": "采购分类",
      "type": "select",
      "required": true,
      "options": "office|办公用品\nit|IT设备\nfurniture|办公家具\nservice|服务采购\nother|其他",
      "placeholder": "请选择采购分类",
      "visible": true,
      "readonly": false
    },
    {
      "id": "quantity",
      "label": "采购数量",
      "type": "number",
      "required": true,
      "placeholder": "请输入采购数量",
      "visible": true,
      "readonly": false,
      "min": 1
    },
    {
      "id": "unitPrice",
      "label": "预估单价",
      "type": "number",
      "required": true,
      "placeholder": "请输入预估单价",
      "visible": true,
      "readonly": false,
      "min": 0,
      "step": 0.01
    },
    {
      "id": "totalAmount",
      "label": "预估总金额",
      "type": "number",
      "required": true,
      "placeholder": "系统自动计算",
      "visible": true,
      "readonly": true
    },
    {
      "id": "urgency",
      "label": "紧急程度",
      "type": "radio",
      "required": true,
      "options": "low|一般\nmedium|紧急\nhigh|非常紧急",
      "defaultValue": "low",
      "visible": true,
      "readonly": false
    },
    {
      "id": "expectedDate",
      "label": "期望到货日期",
      "type": "date",
      "required": true,
      "placeholder": "请选择期望到货日期",
      "visible": true,
      "readonly": false
    },
    {
      "id": "justification",
      "label": "采购理由",
      "type": "textarea",
      "required": true,
      "placeholder": "请详细说明采购理由和用途",
      "visible": true,
      "readonly": false,
      "maxLength": 1000
    }
  ]
}', 'admin', 'system', 'ACTIVE'),

-- 通用审批表单
('general_approval_form', '通用审批表单', '通用的审批申请表单', '{
  "fields": [
    {
      "id": "title",
      "label": "申请标题",
      "type": "text",
      "required": true,
      "placeholder": "请输入申请标题",
      "visible": true,
      "readonly": false,
      "maxLength": 100
    },
    {
      "id": "type",
      "label": "申请类型",
      "type": "select",
      "required": true,
      "options": "approval|审批申请\nrequest|需求申请\nchange|变更申请\nother|其他",
      "placeholder": "请选择申请类型",
      "visible": true,
      "readonly": false
    },
    {
      "id": "priority",
      "label": "优先级",
      "type": "radio",
      "required": true,
      "options": "low|低\nmedium|中\nhigh|高\nurgent|紧急",
      "defaultValue": "medium",
      "visible": true,
      "readonly": false
    },
    {
      "id": "content",
      "label": "申请内容",
      "type": "textarea",
      "required": true,
      "placeholder": "请详细描述申请内容",
      "visible": true,
      "readonly": false,
      "maxLength": 2000
    },
    {
      "id": "expectedDate",
      "label": "期望完成日期",
      "type": "date",
      "required": false,
      "placeholder": "请选择期望完成日期",
      "visible": true,
      "readonly": false
    },
    {
      "id": "attachments",
      "label": "相关附件",
      "type": "file",
      "required": false,
      "placeholder": "请上传相关文档",
      "visible": true,
      "readonly": false
    }
  ]
}', 'general', 'system', 'ACTIVE');

-- 插入表单字段定义（从form_config中提取）
INSERT INTO form_field (form_template_id, field_key, field_name, field_type, field_config, sort_order, is_required) 
SELECT 
    ft.id,
    field_data.value->>'id',
    field_data.value->>'label',
    field_data.value->>'type',
    field_data.value,
    (field_data.ordinality - 1) * 10,
    COALESCE((field_data.value->>'required')::boolean, false)
FROM form_template ft,
     jsonb_array_elements(ft.form_config->'fields') WITH ORDINALITY AS field_data(value, ordinality)
WHERE ft.form_key IN ('leave_application_form', 'expense_reimbursement_form', 'purchase_request_form', 'general_approval_form');

-- =====================================================
-- 添加表注释
-- =====================================================
COMMENT ON TABLE form_template IS '表单模板表 - 存储表单的基本信息和配置';
COMMENT ON TABLE form_field IS '表单字段表 - 存储表单的详细字段定义';
COMMENT ON TABLE process_form_mapping IS '流程表单映射表 - 定义流程节点与表单的关联关系';
COMMENT ON TABLE form_data_instance IS '表单数据实例表 - 存储用户提交的表单数据';
COMMENT ON TABLE form_template_version IS '表单版本管理表 - 管理表单模板的版本历史';
COMMENT ON TABLE form_usage_statistics IS '表单使用统计表 - 统计表单的使用情况';
COMMENT ON TABLE form_category IS '表单分类管理表 - 管理表单分类层次结构';

-- =====================================================
-- 创建触发器函数用于自动更新时间戳
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_time_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为相关表添加更新时间触发器
CREATE TRIGGER update_form_template_updated_time BEFORE UPDATE ON form_template FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER update_form_field_updated_time BEFORE UPDATE ON form_field FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();