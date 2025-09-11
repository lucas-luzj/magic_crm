-- =============================================
-- 客户管理相关表
-- =============================================

-- 1. 客户表
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL, -- 客户编号
    name VARCHAR(200) NOT NULL, -- 客户名称
    short_name VARCHAR(100), -- 简称
    uscc VARCHAR(50), -- 统一社会信用代码
    industry VARCHAR(100), -- 行业
    region VARCHAR(100), -- 地区
    address VARCHAR(500), -- 地址
    website VARCHAR(200), -- 官网
    company_size VARCHAR(50), -- 公司规模
    customer_level VARCHAR(50), -- 客户等级
    customer_type VARCHAR(50), -- 客户类型
    source VARCHAR(50), -- 来源
    owner_id UUID, -- 所属销售
    collaborator_ids UUID[], -- 协作人
    parent_customer_id UUID, -- 上级客户
    is_key_customer BOOLEAN DEFAULT false, -- 是否重点客户
    is_blacklist BOOLEAN DEFAULT false, -- 是否黑名单
    is_public_pool BOOLEAN DEFAULT false, -- 是否在公海池
    pool_entry_time TIMESTAMP, -- 进入公海时间
    pool_entry_reason VARCHAR(500), -- 进入公海原因
    last_follow_time TIMESTAMP, -- 最后跟进时间
    last_order_time TIMESTAMP, -- 最后成单时间
    org_unit_id UUID, -- 所属组织
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 状态: ACTIVE, INACTIVE, DELETED
    remark TEXT, -- 备注
    attachments JSONB, -- 附件
    custom_fields JSONB, -- 自定义字段
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_parent_customer FOREIGN KEY (parent_customer_id) REFERENCES customers(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_customers_code ON customers(code);
CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_customers_uscc ON customers(uscc);
CREATE INDEX idx_customers_owner_id ON customers(owner_id);
CREATE INDEX idx_customers_org_unit_id ON customers(org_unit_id);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_is_public_pool ON customers(is_public_pool);
CREATE INDEX idx_customers_deleted ON customers(deleted);
CREATE UNIQUE INDEX idx_customers_unique ON customers(name, uscc, region) WHERE deleted = false;

-- 2. 联系人表
CREATE TABLE IF NOT EXISTS contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL, -- 所属客户
    name VARCHAR(100) NOT NULL, -- 姓名
    title VARCHAR(100), -- 职位
    department VARCHAR(100), -- 部门
    mobile VARCHAR(20), -- 手机
    phone VARCHAR(20), -- 座机
    email VARCHAR(100), -- 邮箱
    im VARCHAR(100), -- 即时通讯
    decision_role VARCHAR(50), -- 决策角色
    birthday DATE, -- 生日
    preference TEXT, -- 偏好
    privacy_consent BOOLEAN DEFAULT false, -- 隐私同意
    is_primary BOOLEAN DEFAULT false, -- 是否主联系人
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark TEXT,
    attachments JSONB,
    custom_fields JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_contacts_customer_id ON contacts(customer_id);
CREATE INDEX idx_contacts_name ON contacts(name);
CREATE INDEX idx_contacts_mobile ON contacts(mobile);
CREATE INDEX idx_contacts_email ON contacts(email);
CREATE INDEX idx_contacts_deleted ON contacts(deleted);
CREATE UNIQUE INDEX idx_contacts_unique ON contacts(customer_id, name, mobile) WHERE deleted = false;

-- 3. 跟进记录表
CREATE TABLE IF NOT EXISTS activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL, -- 客户
    contact_id UUID, -- 联系人
    project_id UUID, -- 项目
    opportunity_id UUID, -- 商机
    activity_type VARCHAR(50) NOT NULL, -- 类型: VISIT, CALL, MEETING, EMAIL, OTHER
    subject VARCHAR(200) NOT NULL, -- 主题
    content TEXT, -- 内容
    start_time TIMESTAMP, -- 开始时间
    end_time TIMESTAMP, -- 结束时间
    location VARCHAR(200), -- 地点
    participants UUID[], -- 参与人
    next_action VARCHAR(500), -- 下一步行动
    next_follow_time TIMESTAMP, -- 下次跟进时间
    voice_note_id UUID, -- 语音记录ID
    status VARCHAR(20) DEFAULT 'COMPLETED',
    attachments JSONB,
    custom_fields JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_contact FOREIGN KEY (contact_id) REFERENCES contacts(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_activities_customer_id ON activities(customer_id);
CREATE INDEX idx_activities_contact_id ON activities(contact_id);
CREATE INDEX idx_activities_project_id ON activities(project_id);
CREATE INDEX idx_activities_opportunity_id ON activities(opportunity_id);
CREATE INDEX idx_activities_activity_type ON activities(activity_type);
CREATE INDEX idx_activities_start_time ON activities(start_time);
CREATE INDEX idx_activities_created_by ON activities(created_by);
CREATE INDEX idx_activities_deleted ON activities(deleted);

-- 4. 客户关系表
CREATE TABLE IF NOT EXISTS customer_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL, -- 客户
    related_customer_id UUID NOT NULL, -- 关联客户
    relation_type VARCHAR(50) NOT NULL, -- 关系类型: PARENT, SUBSIDIARY, PARTNER, COMPETITOR
    description TEXT, -- 关系描述
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_related_customer FOREIGN KEY (related_customer_id) REFERENCES customers(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_customer_relations_customer_id ON customer_relations(customer_id);
CREATE INDEX idx_customer_relations_related_customer_id ON customer_relations(related_customer_id);
CREATE INDEX idx_customer_relations_relation_type ON customer_relations(relation_type);
CREATE INDEX idx_customer_relations_deleted ON customer_relations(deleted);

-- 5. 客户共享申请表
CREATE TABLE IF NOT EXISTS customer_share_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL, -- 客户
    requester_id UUID NOT NULL, -- 申请人
    approver_id UUID, -- 审批人
    share_type VARCHAR(50) NOT NULL, -- 共享类型: READ_ONLY, LIMITED_EDIT, FULL_ACCESS
    reason TEXT, -- 申请原因
    approval_status VARCHAR(20) DEFAULT 'PENDING', -- 审批状态: PENDING, APPROVED, REJECTED
    approval_comment TEXT, -- 审批意见
    approved_at TIMESTAMP, -- 审批时间
    expired_at TIMESTAMP, -- 过期时间
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users(id),
    CONSTRAINT fk_approver FOREIGN KEY (approver_id) REFERENCES users(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_customer_share_requests_customer_id ON customer_share_requests(customer_id);
CREATE INDEX idx_customer_share_requests_requester_id ON customer_share_requests(requester_id);
CREATE INDEX idx_customer_share_requests_approval_status ON customer_share_requests(approval_status);
CREATE INDEX idx_customer_share_requests_deleted ON customer_share_requests(deleted);

-- 添加注释
COMMENT ON TABLE customers IS '客户表';
COMMENT ON TABLE contacts IS '联系人表';
COMMENT ON TABLE activities IS '跟进记录表';
COMMENT ON TABLE customer_relations IS '客户关系表';
COMMENT ON TABLE customer_share_requests IS '客户共享申请表';