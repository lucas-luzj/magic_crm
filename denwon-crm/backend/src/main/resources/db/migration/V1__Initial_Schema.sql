-- =============================================
-- Denwon CRM Initial Database Schema
-- Version: 1.0.0
-- =============================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- 1. System Tables
-- =============================================

-- Organization Units Table
CREATE TABLE org_units (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT REFERENCES org_units(id),
    level INTEGER NOT NULL DEFAULT 1,
    path VARCHAR(500),
    type VARCHAR(20) NOT NULL, -- COMPANY, DEPARTMENT, TEAM
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_org_units_parent ON org_units(parent_id);
CREATE INDEX idx_org_units_path ON org_units(path);

-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile VARCHAR(20) UNIQUE,
    name VARCHAR(100) NOT NULL,
    employee_no VARCHAR(50),
    org_unit_id BIGINT REFERENCES org_units(id),
    title VARCHAR(50),
    avatar VARCHAR(500),
    status INTEGER DEFAULT 1, -- 1: Active, 0: Inactive, -1: Deleted
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_users_org_unit ON users(org_unit_id);
CREATE INDEX idx_users_status ON users(status);

-- Roles Table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL, -- SYSTEM, CUSTOM
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Permissions Table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    resource VARCHAR(100),
    description TEXT,
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Roles Mapping
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    assigned_by BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE(user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- Role Permissions Mapping
CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    granted_by BIGINT REFERENCES users(id),
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);

-- Data Dictionary Table
CREATE TABLE dictionaries (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT REFERENCES dictionaries(id),
    value VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE(type, code)
);

CREATE INDEX idx_dictionaries_type ON dictionaries(type);
CREATE INDEX idx_dictionaries_parent ON dictionaries(parent_id);

-- =============================================
-- 2. Customer Management Tables
-- =============================================

-- Customers Table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    short_name VARCHAR(100),
    uscc VARCHAR(50), -- Unified Social Credit Code
    industry VARCHAR(50),
    region VARCHAR(100),
    address TEXT,
    website VARCHAR(200),
    scale VARCHAR(50),
    level VARCHAR(20), -- VIP, KEY, NORMAL
    source VARCHAR(50),
    owner_id BIGINT REFERENCES users(id),
    org_unit_id BIGINT REFERENCES org_units(id),
    parent_customer_id BIGINT REFERENCES customers(id),
    is_key BOOLEAN DEFAULT FALSE,
    is_blacklist BOOLEAN DEFAULT FALSE,
    pool_status VARCHAR(20) DEFAULT 'PRIVATE', -- PUBLIC, PRIVATE
    pool_entered_at TIMESTAMP,
    collaborators JSONB,
    tags JSONB,
    custom_fields JSONB,
    attachments JSONB,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by BIGINT,
    remark TEXT,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_customers_owner ON customers(owner_id);
CREATE INDEX idx_customers_org_unit ON customers(org_unit_id);
CREATE INDEX idx_customers_pool_status ON customers(pool_status);
CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_customers_uscc ON customers(uscc);
CREATE UNIQUE INDEX idx_customers_unique ON customers(name, uscc, region) WHERE deleted = FALSE;

-- Contacts Table
CREATE TABLE contacts (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100),
    department VARCHAR(100),
    mobile VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100),
    wechat VARCHAR(50),
    qq VARCHAR(20),
    role_tag VARCHAR(50), -- DECISION_MAKER, INFLUENCER, USER
    birthday DATE,
    preference TEXT,
    consent BOOLEAN DEFAULT TRUE,
    is_primary BOOLEAN DEFAULT FALSE,
    custom_fields JSONB,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark TEXT,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_contacts_customer ON contacts(customer_id);
CREATE INDEX idx_contacts_mobile ON contacts(mobile);
CREATE INDEX idx_contacts_email ON contacts(email);
CREATE UNIQUE INDEX idx_contacts_unique ON contacts(customer_id, name, mobile) WHERE deleted = FALSE;

-- Activities Table (Customer Follow-ups)
CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    contact_id BIGINT REFERENCES contacts(id),
    opportunity_id BIGINT,
    project_id BIGINT,
    type VARCHAR(50) NOT NULL, -- VISIT, CALL, EMAIL, MEETING, DEMO
    subject VARCHAR(200) NOT NULL,
    content TEXT,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP,
    location VARCHAR(200),
    participants JSONB,
    attachments JSONB,
    next_action VARCHAR(200),
    next_follow_at TIMESTAMP,
    owner_id BIGINT REFERENCES users(id),
    org_unit_id BIGINT REFERENCES org_units(id),
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_activities_customer ON activities(customer_id);
CREATE INDEX idx_activities_contact ON activities(contact_id);
CREATE INDEX idx_activities_owner ON activities(owner_id);
CREATE INDEX idx_activities_start_at ON activities(start_at);

-- =============================================
-- 3. Lead Management Tables
-- =============================================

-- Leads Table
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    source VARCHAR(50) NOT NULL, -- WEBSITE, PHONE, REFERRAL, CAMPAIGN
    channel VARCHAR(50),
    customer_name VARCHAR(200),
    contact_name VARCHAR(100),
    mobile VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100),
    wechat VARCHAR(50),
    region VARCHAR(100),
    industry VARCHAR(50),
    product_intent VARCHAR(200),
    budget_range VARCHAR(50),
    purchase_timeline VARCHAR(50),
    summary TEXT,
    score INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'NEW', -- NEW, ASSIGNED, FOLLOWING, CONVERTED, INVALID
    assigned_to BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP,
    converted_at TIMESTAMP,
    converted_to_customer_id BIGINT REFERENCES customers(id),
    invalid_reason VARCHAR(200),
    tags JSONB,
    custom_fields JSONB,
    attachments JSONB,
    owner_id BIGINT REFERENCES users(id),
    org_unit_id BIGINT REFERENCES org_units(id),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark TEXT,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_assigned_to ON leads(assigned_to);
CREATE INDEX idx_leads_mobile ON leads(mobile);
CREATE INDEX idx_leads_email ON leads(email);
CREATE INDEX idx_leads_created_at ON leads(created_at);

-- Lead Assignment Rules
CREATE TABLE lead_assignment_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- ROUND_ROBIN, WEIGHTED, REGIONAL, SOURCE_BASED
    conditions JSONB,
    assignments JSONB,
    priority INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. Opportunity Management Tables
-- =============================================

-- Opportunity Stage Templates
CREATE TABLE opportunity_stage_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    org_unit_id BIGINT REFERENCES org_units(id),
    stages JSONB NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Opportunities Table
CREATE TABLE opportunities (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    contact_id BIGINT REFERENCES contacts(id),
    stage_template_id BIGINT REFERENCES opportunity_stage_templates(id),
    current_stage VARCHAR(50),
    stage_entered_at TIMESTAMP,
    expected_amount_ex_tax DECIMAL(18,2),
    expected_amount_inc_tax DECIMAL(18,2),
    probability INTEGER DEFAULT 0,
    expected_sign_at DATE,
    actual_sign_at DATE,
    competitor VARCHAR(200),
    purchase_method VARCHAR(50),
    decision_chain JSONB,
    win_loss_reason VARCHAR(200),
    tags JSONB,
    custom_fields JSONB,
    attachments JSONB,
    owner_id BIGINT REFERENCES users(id),
    org_unit_id BIGINT REFERENCES org_units(id),
    collaborators JSONB,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark TEXT,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_opportunities_customer ON opportunities(customer_id);
CREATE INDEX idx_opportunities_owner ON opportunities(owner_id);
CREATE INDEX idx_opportunities_stage ON opportunities(current_stage);
CREATE INDEX idx_opportunities_expected_sign ON opportunities(expected_sign_at);

-- Quotes Table
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    opportunity_id BIGINT REFERENCES opportunities(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    contact_id BIGINT REFERENCES contacts(id),
    current_version_id BIGINT,
    print_template_id BIGINT,
    approval_status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, PENDING, APPROVED, REJECTED
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    valid_until DATE,
    terms_conditions TEXT,
    custom_fields JSONB,
    attachments JSONB,
    owner_id BIGINT REFERENCES users(id),
    org_unit_id BIGINT REFERENCES org_units(id),
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark TEXT,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_quotes_opportunity ON quotes(opportunity_id);
CREATE INDEX idx_quotes_customer ON quotes(customer_id);
CREATE INDEX idx_quotes_approval_status ON quotes(approval_status);

-- Quote Versions Table
CREATE TABLE quote_versions (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id),
    version_no INTEGER NOT NULL,
    currency VARCHAR(10) DEFAULT 'CNY',
    exchange_rate DECIMAL(10,4) DEFAULT 1.0,
    tax_rate DECIMAL(5,2),
    total_ex_tax DECIMAL(18,2),
    total_inc_tax DECIMAL(18,2),
    total_tax DECIMAL(18,2),
    discount_rate DECIMAL(5,2),
    discount_amount DECIMAL(18,2),
    note TEXT,
    is_current BOOLEAN DEFAULT FALSE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(quote_id, version_no)
);

CREATE INDEX idx_quote_versions_quote ON quote_versions(quote_id);
CREATE INDEX idx_quote_versions_current ON quote_versions(is_current);

-- Quote Items Table
CREATE TABLE quote_items (
    id BIGSERIAL PRIMARY KEY,
    quote_version_id BIGINT NOT NULL REFERENCES quote_versions(id),
    product_id BIGINT,
    product_code VARCHAR(50),
    product_name VARCHAR(200) NOT NULL,
    specification TEXT,
    uom VARCHAR(20),
    quantity DECIMAL(18,4) NOT NULL,
    unit_price_ex_tax DECIMAL(18,4) NOT NULL,
    tax_rate DECIMAL(5,2),
    discount_rate DECIMAL(5,2),
    discount_amount DECIMAL(18,2),
    subtotal_ex_tax DECIMAL(18,2),
    tax_amount DECIMAL(18,2),
    subtotal_inc_tax DECIMAL(18,2),
    delivery_date DATE,
    note TEXT,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quote_items_version ON quote_items(quote_version_id);
CREATE INDEX idx_quote_items_product ON quote_items(product_id);

-- =============================================
-- 5. Product Management Tables
-- =============================================

-- Product Categories
CREATE TABLE product_categories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT REFERENCES product_categories(id),
    level INTEGER DEFAULT 1,
    path VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_categories_parent ON product_categories(parent_id);

-- Products Table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    category_id BIGINT REFERENCES product_categories(id),
    brand VARCHAR(100),
    model VARCHAR(100),
    specification TEXT,
    uom VARCHAR(20) NOT NULL, -- Unit of Measure
    tax_rate DECIMAL(5,2),
    standard_price DECIMAL(18,4),
    cost_price DECIMAL(18,4),
    min_price DECIMAL(18,4),
    is_spare BOOLEAN DEFAULT FALSE,
    is_bundle BOOLEAN DEFAULT FALSE,
    bundle_items JSONB,
    images JSONB,
    attachments JSONB,
    custom_fields JSONB,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_name ON products(name);

-- Price Policies Table
CREATE TABLE price_policies (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- STANDARD, AGREEMENT, REGIONAL, PROMOTION
    region_id BIGINT,
    customer_id BIGINT REFERENCES customers(id),
    product_id BIGINT REFERENCES products(id),
    product_category_id BIGINT REFERENCES product_categories(id),
    price_ex_tax DECIMAL(18,4),
    discount_rate DECIMAL(5,2),
    min_quantity DECIMAL(18,4),
    effective_from TIMESTAMP NOT NULL,
    effective_to TIMESTAMP,
    priority INTEGER DEFAULT 0,
    conditions JSONB,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_price_policies_type ON price_policies(type);
CREATE INDEX idx_price_policies_customer ON price_policies(customer_id);
CREATE INDEX idx_price_policies_product ON price_policies(product_id);
CREATE INDEX idx_price_policies_effective ON price_policies(effective_from, effective_to);

-- Inventory (Lightweight)
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    warehouse_code VARCHAR(50) DEFAULT 'MAIN',
    available_qty DECIMAL(18,4) DEFAULT 0,
    in_transit_qty DECIMAL(18,4) DEFAULT 0,
    reserved_qty DECIMAL(18,4) DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, warehouse_code)
);

CREATE INDEX idx_inventory_product ON inventory(product_id);

-- =============================================
-- 6. Audit Tables
-- =============================================

-- Audit Log Table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE, VIEW, EXPORT
    changes JSONB,
    user_id BIGINT,
    user_name VARCHAR(100),
    user_ip VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);

-- Create monthly partitions for audit logs
CREATE TABLE audit_logs_2025_01 PARTITION OF audit_logs
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');
CREATE TABLE audit_logs_2025_02 PARTITION OF audit_logs
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

-- System Logs Table
CREATE TABLE system_logs (
    id BIGSERIAL PRIMARY KEY,
    level VARCHAR(20) NOT NULL, -- DEBUG, INFO, WARN, ERROR
    module VARCHAR(50),
    message TEXT,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

CREATE INDEX idx_system_logs_level ON system_logs(level);
CREATE INDEX idx_system_logs_module ON system_logs(module);
CREATE INDEX idx_system_logs_created ON system_logs(created_at);

-- Create monthly partitions for system logs
CREATE TABLE system_logs_2025_01 PARTITION OF system_logs
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');
CREATE TABLE system_logs_2025_02 PARTITION OF system_logs
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');