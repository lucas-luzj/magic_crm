-- =============================================
-- 供应商管理相关表
-- =============================================

-- 1. 供应商表
CREATE TABLE IF NOT EXISTS suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL, -- 供应商编码
    name VARCHAR(200) NOT NULL, -- 供应商名称
    short_name VARCHAR(100), -- 简称
    uscc VARCHAR(50), -- 统一社会信用代码
    supplier_type VARCHAR(50), -- 供应商类型: MANUFACTURER, DISTRIBUTOR, SERVICE_PROVIDER
    industry VARCHAR(100), -- 行业
    region VARCHAR(100), -- 地区
    address VARCHAR(500), -- 地址
    website VARCHAR(200), -- 官网
    contact_person VARCHAR(100), -- 联系人
    contact_phone VARCHAR(20), -- 联系电话
    contact_email VARCHAR(100), -- 联系邮箱
    bank_name VARCHAR(100), -- 开户银行
    bank_account VARCHAR(50), -- 银行账号
    tax_number VARCHAR(50), -- 税号
    payment_terms VARCHAR(200), -- 付款条件
    credit_limit DECIMAL(18,2), -- 信用额度
    rating VARCHAR(20), -- 评级: A, B, C, D
    rating_score DECIMAL(5,2), -- 评分
    is_qualified BOOLEAN DEFAULT true, -- 是否合格
    is_blacklist BOOLEAN DEFAULT false, -- 是否黑名单
    qualification_date DATE, -- 资质认证日期
    qualification_expiry DATE, -- 资质到期日期
    certifications JSONB, -- 资质证书
    product_categories JSONB, -- 供应产品类别
    cooperation_start_date DATE, -- 合作开始日期
    last_purchase_date DATE, -- 最后采购日期
    total_purchase_amount DECIMAL(18,2) DEFAULT 0, -- 累计采购金额
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 状态: ACTIVE, INACTIVE, SUSPENDED
    remark TEXT,
    attachments JSONB,
    custom_fields JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_suppliers_code ON suppliers(code);
CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_uscc ON suppliers(uscc);
CREATE INDEX idx_suppliers_supplier_type ON suppliers(supplier_type);
CREATE INDEX idx_suppliers_region ON suppliers(region);
CREATE INDEX idx_suppliers_rating ON suppliers(rating);
CREATE INDEX idx_suppliers_is_blacklist ON suppliers(is_blacklist);
CREATE INDEX idx_suppliers_status ON suppliers(status);
CREATE INDEX idx_suppliers_deleted ON suppliers(deleted);

-- 2. 供应商联系人表
CREATE TABLE IF NOT EXISTS supplier_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL, -- 供应商ID
    name VARCHAR(100) NOT NULL, -- 姓名
    title VARCHAR(100), -- 职位
    department VARCHAR(100), -- 部门
    mobile VARCHAR(20), -- 手机
    phone VARCHAR(20), -- 座机
    email VARCHAR(100), -- 邮箱
    im VARCHAR(100), -- 即时通讯
    is_primary BOOLEAN DEFAULT false, -- 是否主联系人
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark TEXT,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_supplier_contacts_supplier_id ON supplier_contacts(supplier_id);
CREATE INDEX idx_supplier_contacts_name ON supplier_contacts(name);
CREATE INDEX idx_supplier_contacts_deleted ON supplier_contacts(deleted);

-- 3. 供应商产品关联表
CREATE TABLE IF NOT EXISTS supplier_products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL, -- 供应商ID
    product_id UUID NOT NULL, -- 产品ID
    supplier_product_code VARCHAR(100), -- 供应商产品编码
    supplier_product_name VARCHAR(200), -- 供应商产品名称
    price DECIMAL(18,2), -- 供货价格
    currency VARCHAR(10) DEFAULT 'CNY', -- 币种
    lead_time INTEGER, -- 交付周期(天)
    min_order_quantity INTEGER, -- 最小起订量
    packaging_unit VARCHAR(50), -- 包装单位
    is_primary_supplier BOOLEAN DEFAULT false, -- 是否主供应商
    priority INTEGER DEFAULT 0, -- 优先级
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_supplier_products_supplier_id ON supplier_products(supplier_id);
CREATE INDEX idx_supplier_products_product_id ON supplier_products(product_id);
CREATE INDEX idx_supplier_products_is_primary ON supplier_products(is_primary_supplier);
CREATE INDEX idx_supplier_products_deleted ON supplier_products(deleted);
CREATE UNIQUE INDEX idx_supplier_products_unique ON supplier_products(supplier_id, product_id) WHERE deleted = false;

-- 4. 供应商评价表
CREATE TABLE IF NOT EXISTS supplier_evaluations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL, -- 供应商ID
    evaluation_date DATE NOT NULL, -- 评价日期
    evaluation_period VARCHAR(50), -- 评价周期: MONTHLY, QUARTERLY, YEARLY
    quality_score DECIMAL(5,2), -- 质量评分
    delivery_score DECIMAL(5,2), -- 交付评分
    service_score DECIMAL(5,2), -- 服务评分
    price_score DECIMAL(5,2), -- 价格评分
    overall_score DECIMAL(5,2), -- 综合评分
    evaluation_result VARCHAR(50), -- 评价结果: EXCELLENT, GOOD, QUALIFIED, UNQUALIFIED
    improvement_items TEXT, -- 改进项
    evaluator_id UUID, -- 评价人
    status VARCHAR(20) DEFAULT 'COMPLETED',
    attachments JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_evaluator FOREIGN KEY (evaluator_id) REFERENCES users(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_supplier_evaluations_supplier_id ON supplier_evaluations(supplier_id);
CREATE INDEX idx_supplier_evaluations_evaluation_date ON supplier_evaluations(evaluation_date);
CREATE INDEX idx_supplier_evaluations_evaluation_period ON supplier_evaluations(evaluation_period);
CREATE INDEX idx_supplier_evaluations_deleted ON supplier_evaluations(deleted);

-- 5. 供应商黑名单记录表
CREATE TABLE IF NOT EXISTS supplier_blacklist_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL, -- 供应商ID
    blacklist_date DATE NOT NULL, -- 加入黑名单日期
    blacklist_reason TEXT NOT NULL, -- 加入原因
    severity VARCHAR(50), -- 严重程度: HIGH, MEDIUM, LOW
    expected_release_date DATE, -- 预计解除日期
    actual_release_date DATE, -- 实际解除日期
    release_reason TEXT, -- 解除原因
    is_active BOOLEAN DEFAULT true, -- 是否生效
    approver_id UUID, -- 审批人
    approved_at TIMESTAMP, -- 审批时间
    attachments JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_approver FOREIGN KEY (approver_id) REFERENCES users(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_supplier_blacklist_supplier_id ON supplier_blacklist_records(supplier_id);
CREATE INDEX idx_supplier_blacklist_is_active ON supplier_blacklist_records(is_active);
CREATE INDEX idx_supplier_blacklist_blacklist_date ON supplier_blacklist_records(blacklist_date);

-- 添加注释
COMMENT ON TABLE suppliers IS '供应商表';
COMMENT ON TABLE supplier_contacts IS '供应商联系人表';
COMMENT ON TABLE supplier_products IS '供应商产品关联表';
COMMENT ON TABLE supplier_evaluations IS '供应商评价表';
COMMENT ON TABLE supplier_blacklist_records IS '供应商黑名单记录表';