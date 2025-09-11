-- =============================================
-- 线索管理相关表
-- =============================================

-- 1. 线索表
CREATE TABLE IF NOT EXISTS leads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL, -- 线索编号
    source VARCHAR(50) NOT NULL, -- 来源: WEBSITE, PHONE, EMAIL, WECHAT, EXHIBITION, REFERRAL, OTHER
    channel VARCHAR(100), -- 渠道
    customer_name VARCHAR(200), -- 客户名称
    contact_name VARCHAR(100), -- 联系人姓名
    mobile VARCHAR(20), -- 手机
    phone VARCHAR(20), -- 座机
    email VARCHAR(100), -- 邮箱
    region VARCHAR(100), -- 地区
    address VARCHAR(500), -- 地址
    industry VARCHAR(100), -- 行业
    company_size VARCHAR(50), -- 公司规模
    product_intent TEXT, -- 意向产品
    budget_range VARCHAR(100), -- 预算范围
    purchase_timeline VARCHAR(50), -- 采购时间线
    summary TEXT, -- 摘要
    lead_score INTEGER DEFAULT 0, -- 线索评分
    lead_status VARCHAR(50) DEFAULT 'NEW', -- 状态: NEW, ASSIGNED, FOLLOWING, CONVERTED, INVALID
    assigned_to UUID, -- 分配给
    assigned_at TIMESTAMP, -- 分配时间
    assignment_method VARCHAR(50), -- 分配方式: MANUAL, AUTO_REGION, AUTO_ROUND_ROBIN, AUTO_WEIGHT
    follow_status VARCHAR(50) DEFAULT 'NOT_STARTED', -- 跟进状态: NOT_STARTED, IN_PROGRESS, COMPLETED
    last_follow_time TIMESTAMP, -- 最后跟进时间
    next_follow_time TIMESTAMP, -- 下次跟进时间
    conversion_status VARCHAR(50), -- 转化状态: NOT_CONVERTED, PARTIAL, FULL
    converted_at TIMESTAMP, -- 转化时间
    converted_by UUID, -- 转化人
    customer_id UUID, -- 转化后的客户ID
    contact_id UUID, -- 转化后的联系人ID
    opportunity_id UUID, -- 转化后的商机ID
    invalid_reason VARCHAR(500), -- 无效原因
    org_unit_id UUID, -- 所属组织
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark TEXT,
    attachments JSONB,
    custom_fields JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id),
    CONSTRAINT fk_converted_by FOREIGN KEY (converted_by) REFERENCES users(id),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_contact FOREIGN KEY (contact_id) REFERENCES contacts(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_leads_code ON leads(code);
CREATE INDEX idx_leads_source ON leads(source);
CREATE INDEX idx_leads_customer_name ON leads(customer_name);
CREATE INDEX idx_leads_mobile ON leads(mobile);
CREATE INDEX idx_leads_email ON leads(email);
CREATE INDEX idx_leads_lead_status ON leads(lead_status);
CREATE INDEX idx_leads_assigned_to ON leads(assigned_to);
CREATE INDEX idx_leads_conversion_status ON leads(conversion_status);
CREATE INDEX idx_leads_created_at ON leads(created_at);
CREATE INDEX idx_leads_deleted ON leads(deleted);

-- 2. 线索分配规则表
CREATE TABLE IF NOT EXISTS lead_assignment_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name VARCHAR(100) NOT NULL, -- 规则名称
    rule_type VARCHAR(50) NOT NULL, -- 规则类型: REGION, SOURCE, ROUND_ROBIN, WEIGHT, BALANCE
    priority INTEGER DEFAULT 0, -- 优先级
    is_active BOOLEAN DEFAULT true, -- 是否启用
    conditions JSONB, -- 条件配置
    assignments JSONB, -- 分配配置
    max_daily_leads INTEGER, -- 每日最大线索数
    max_total_leads INTEGER, -- 总最大线索数
    effective_from TIMESTAMP, -- 生效开始时间
    effective_to TIMESTAMP, -- 生效结束时间
    description TEXT, -- 描述
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_lead_assignment_rules_rule_type ON lead_assignment_rules(rule_type);
CREATE INDEX idx_lead_assignment_rules_is_active ON lead_assignment_rules(is_active);
CREATE INDEX idx_lead_assignment_rules_priority ON lead_assignment_rules(priority);
CREATE INDEX idx_lead_assignment_rules_deleted ON lead_assignment_rules(deleted);

-- 3. 线索分配记录表
CREATE TABLE IF NOT EXISTS lead_assignment_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL, -- 线索ID
    rule_id UUID, -- 规则ID
    assigned_from UUID, -- 原分配人
    assigned_to UUID NOT NULL, -- 新分配人
    assignment_type VARCHAR(50) NOT NULL, -- 分配类型: INITIAL, TRANSFER, REASSIGN, RECLAIM
    assignment_reason TEXT, -- 分配原因
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 分配时间
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lead FOREIGN KEY (lead_id) REFERENCES leads(id),
    CONSTRAINT fk_rule FOREIGN KEY (rule_id) REFERENCES lead_assignment_rules(id),
    CONSTRAINT fk_assigned_from FOREIGN KEY (assigned_from) REFERENCES users(id),
    CONSTRAINT fk_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_lead_assignment_logs_lead_id ON lead_assignment_logs(lead_id);
CREATE INDEX idx_lead_assignment_logs_assigned_to ON lead_assignment_logs(assigned_to);
CREATE INDEX idx_lead_assignment_logs_assignment_type ON lead_assignment_logs(assignment_type);
CREATE INDEX idx_lead_assignment_logs_assigned_at ON lead_assignment_logs(assigned_at);

-- 4. 线索跟进记录表
CREATE TABLE IF NOT EXISTS lead_follow_ups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL, -- 线索ID
    follow_type VARCHAR(50) NOT NULL, -- 跟进类型: CALL, EMAIL, VISIT, WECHAT, OTHER
    follow_content TEXT, -- 跟进内容
    follow_result VARCHAR(50), -- 跟进结果: INTERESTED, CONSIDERING, NOT_INTERESTED, NO_RESPONSE
    next_action VARCHAR(500), -- 下一步行动
    next_follow_time TIMESTAMP, -- 下次跟进时间
    attachments JSONB, -- 附件
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_lead FOREIGN KEY (lead_id) REFERENCES leads(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_lead_follow_ups_lead_id ON lead_follow_ups(lead_id);
CREATE INDEX idx_lead_follow_ups_follow_type ON lead_follow_ups(follow_type);
CREATE INDEX idx_lead_follow_ups_created_at ON lead_follow_ups(created_at);
CREATE INDEX idx_lead_follow_ups_deleted ON lead_follow_ups(deleted);

-- 5. 线索评分规则表
CREATE TABLE IF NOT EXISTS lead_scoring_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name VARCHAR(100) NOT NULL, -- 规则名称
    dimension VARCHAR(50) NOT NULL, -- 维度: SOURCE, BEHAVIOR, PROFILE, ENGAGEMENT
    condition_field VARCHAR(100), -- 条件字段
    condition_operator VARCHAR(20), -- 条件操作符: EQUALS, CONTAINS, GREATER_THAN, LESS_THAN
    condition_value TEXT, -- 条件值
    score_value INTEGER NOT NULL, -- 分值
    is_active BOOLEAN DEFAULT true, -- 是否启用
    description TEXT, -- 描述
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_lead_scoring_rules_dimension ON lead_scoring_rules(dimension);
CREATE INDEX idx_lead_scoring_rules_is_active ON lead_scoring_rules(is_active);
CREATE INDEX idx_lead_scoring_rules_deleted ON lead_scoring_rules(deleted);

-- 6. 线索评分记录表
CREATE TABLE IF NOT EXISTS lead_scoring_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL, -- 线索ID
    rule_id UUID NOT NULL, -- 规则ID
    score_before INTEGER, -- 评分前分数
    score_change INTEGER, -- 分数变化
    score_after INTEGER, -- 评分后分数
    trigger_event VARCHAR(100), -- 触发事件
    scored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 评分时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lead FOREIGN KEY (lead_id) REFERENCES leads(id),
    CONSTRAINT fk_rule FOREIGN KEY (rule_id) REFERENCES lead_scoring_rules(id)
);

-- 创建索引
CREATE INDEX idx_lead_scoring_logs_lead_id ON lead_scoring_logs(lead_id);
CREATE INDEX idx_lead_scoring_logs_rule_id ON lead_scoring_logs(rule_id);
CREATE INDEX idx_lead_scoring_logs_scored_at ON lead_scoring_logs(scored_at);

-- 7. 线索去重记录表
CREATE TABLE IF NOT EXISTS lead_duplicate_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL, -- 线索ID
    duplicate_lead_id UUID, -- 重复线索ID
    duplicate_type VARCHAR(50) NOT NULL, -- 重复类型: MOBILE, EMAIL, COMPANY_REGION
    duplicate_value VARCHAR(200), -- 重复值
    merge_status VARCHAR(50) DEFAULT 'PENDING', -- 合并状态: PENDING, MERGED, IGNORED
    merged_to UUID, -- 合并到的线索ID
    merged_at TIMESTAMP, -- 合并时间
    merged_by UUID, -- 合并人
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lead FOREIGN KEY (lead_id) REFERENCES leads(id),
    CONSTRAINT fk_duplicate_lead FOREIGN KEY (duplicate_lead_id) REFERENCES leads(id),
    CONSTRAINT fk_merged_to FOREIGN KEY (merged_to) REFERENCES leads(id),
    CONSTRAINT fk_merged_by FOREIGN KEY (merged_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_lead_duplicate_lead_id ON lead_duplicate_records(lead_id);
CREATE INDEX idx_lead_duplicate_type ON lead_duplicate_records(duplicate_type);
CREATE INDEX idx_lead_duplicate_merge_status ON lead_duplicate_records(merge_status);

-- 添加注释
COMMENT ON TABLE leads IS '线索表';
COMMENT ON TABLE lead_assignment_rules IS '线索分配规则表';
COMMENT ON TABLE lead_assignment_logs IS '线索分配记录表';
COMMENT ON TABLE lead_follow_ups IS '线索跟进记录表';
COMMENT ON TABLE lead_scoring_rules IS '线索评分规则表';
COMMENT ON TABLE lead_scoring_logs IS '线索评分记录表';
COMMENT ON TABLE lead_duplicate_records IS '线索去重记录表';