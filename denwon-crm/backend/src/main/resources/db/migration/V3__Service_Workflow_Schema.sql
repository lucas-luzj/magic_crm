-- =============================================
-- Service, Workflow and Commission Tables
-- Version: 3.0.0
-- =============================================

-- =============================================
-- 11. Asset and Service Management Tables
-- =============================================

-- Assets Table (Equipment Registry)
CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT REFERENCES products(id),
    serial_id BIGINT REFERENCES serials(id),
    customer_id BIGINT REFERENCES customers(id),
    project_id BIGINT REFERENCES projects(id),
    sales_contract_id BIGINT REFERENCES sales_contracts(id),
    asset_name VARCHAR(200) NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    serial_no VARCHAR(100),
    mac_address VARCHAR(50),
    imei VARCHAR(50),
    installed_date DATE,
    installed_by BIGINT REFERENCES users(id),
    installation_address TEXT,
    warranty_start_date DATE,
    warranty_end_date DATE,
    asset_status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, MAINTENANCE, RETIRED
    qr_code VARCHAR(200),
    location_details JSONB,
    maintenance_schedule JSONB,
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

CREATE INDEX idx_assets_customer ON assets(customer_id);
CREATE INDEX idx_assets_project ON assets(project_id);
CREATE INDEX idx_assets_serial ON assets(serial_no);
CREATE INDEX idx_assets_status ON assets(asset_status);
CREATE INDEX idx_assets_warranty ON assets(warranty_end_date);

-- Service Orders Table (Work Orders)
CREATE TABLE service_orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    asset_id BIGINT REFERENCES assets(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    contact_id BIGINT REFERENCES contacts(id),
    type VARCHAR(20) NOT NULL, -- INSTALLATION, REPAIR, MAINTENANCE, INSPECTION
    source VARCHAR(20), -- PHONE, EMAIL, SYSTEM, CONTRACT
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    sla_id BIGINT,
    problem_description TEXT,
    reported_at TIMESTAMP NOT NULL,
    reporter_name VARCHAR(100),
    reporter_phone VARCHAR(20),
    assigned_to BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP,
    scheduled_date DATE,
    response_deadline TIMESTAMP,
    resolution_deadline TIMESTAMP,
    actual_response_at TIMESTAMP,
    actual_arrival_at TIMESTAMP,
    actual_resolution_at TIMESTAMP,
    service_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED
    resolution_description TEXT,
    closed_at TIMESTAMP,
    closed_by BIGINT REFERENCES users(id),
    is_billable BOOLEAN DEFAULT TRUE,
    total_cost DECIMAL(18,2),
    customer_signature VARCHAR(500),
    satisfaction_score INTEGER,
    attachments JSONB,
    custom_fields JSONB,
    owner_id BIGINT REFERENCES users(id),
    org_unit_id BIGINT REFERENCES org_units(id),
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark TEXT,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_service_orders_asset ON service_orders(asset_id);
CREATE INDEX idx_service_orders_customer ON service_orders(customer_id);
CREATE INDEX idx_service_orders_assigned ON service_orders(assigned_to);
CREATE INDEX idx_service_orders_status ON service_orders(service_status);
CREATE INDEX idx_service_orders_reported ON service_orders(reported_at);

-- Service Records Table (Service Actions)
CREATE TABLE service_records (
    id BIGSERIAL PRIMARY KEY,
    service_order_id BIGINT NOT NULL REFERENCES service_orders(id),
    engineer_id BIGINT NOT NULL REFERENCES users(id),
    action_type VARCHAR(20), -- DIAGNOSE, REPAIR, REPLACE, TEST
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP,
    is_resolved BOOLEAN DEFAULT FALSE,
    action_description TEXT,
    solution_description TEXT,
    parts_used JSONB,
    parts_cost DECIMAL(18,2) DEFAULT 0,
    labor_hours DECIMAL(10,2),
    labor_cost DECIMAL(18,2) DEFAULT 0,
    travel_distance DECIMAL(10,2),
    travel_cost DECIMAL(18,2) DEFAULT 0,
    other_cost DECIMAL(18,2) DEFAULT 0,
    total_cost DECIMAL(18,2),
    attachments JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_service_records_order ON service_records(service_order_id);
CREATE INDEX idx_service_records_engineer ON service_records(engineer_id);

-- Service Surveys Table
CREATE TABLE service_surveys (
    id BIGSERIAL PRIMARY KEY,
    service_order_id BIGINT NOT NULL REFERENCES service_orders(id),
    customer_id BIGINT REFERENCES customers(id),
    contact_id BIGINT REFERENCES contacts(id),
    overall_score INTEGER NOT NULL CHECK (overall_score >= 1 AND overall_score <= 5),
    response_time_score INTEGER,
    service_quality_score INTEGER,
    engineer_attitude_score INTEGER,
    problem_resolution_score INTEGER,
    comments TEXT,
    improvement_suggestions TEXT,
    survey_date DATE NOT NULL,
    survey_method VARCHAR(20), -- PHONE, EMAIL, APP, WECHAT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_service_surveys_order ON service_surveys(service_order_id);
CREATE INDEX idx_service_surveys_score ON service_surveys(overall_score);

-- Warranty Contracts Table
CREATE TABLE warranty_contracts (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    sales_contract_id BIGINT REFERENCES sales_contracts(id),
    contract_type VARCHAR(20), -- STANDARD, EXTENDED, PREMIUM
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    coverage_scope TEXT,
    response_sla_hours INTEGER,
    resolution_sla_hours INTEGER,
    inspection_plan JSONB,
    included_services JSONB,
    excluded_services JSONB,
    consumables JSONB,
    contract_amount DECIMAL(18,2),
    renewal_reminder_days INTEGER DEFAULT 30,
    auto_renewal BOOLEAN DEFAULT FALSE,
    contract_status VARCHAR(20) DEFAULT 'ACTIVE', -- DRAFT, ACTIVE, EXPIRED, RENEWED, CANCELLED
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark TEXT
);

CREATE INDEX idx_warranty_contracts_customer ON warranty_contracts(customer_id);
CREATE INDEX idx_warranty_contracts_status ON warranty_contracts(contract_status);
CREATE INDEX idx_warranty_contracts_dates ON warranty_contracts(start_date, end_date);

-- Service Knowledge Base
CREATE TABLE service_knowledge (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL,
    product_id BIGINT REFERENCES products(id),
    problem_type VARCHAR(100),
    problem_description TEXT NOT NULL,
    root_cause TEXT,
    solution TEXT NOT NULL,
    prevention_measures TEXT,
    applicable_models JSONB,
    tags JSONB,
    view_count INTEGER DEFAULT 0,
    usage_count INTEGER DEFAULT 0,
    effectiveness_score DECIMAL(3,2),
    attachments JSONB,
    status INTEGER DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_service_knowledge_category ON service_knowledge(category);
CREATE INDEX idx_service_knowledge_product ON service_knowledge(product_id);
CREATE INDEX idx_service_knowledge_usage ON service_knowledge(usage_count);

-- =============================================
-- 12. Commission and Bonus Tables
-- =============================================

-- Commission Plans Table
CREATE TABLE commission_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- SALES, SERVICE, MANAGEMENT
    calculation_method VARCHAR(20), -- PERCENTAGE, FIXED, TIERED
    base_type VARCHAR(20), -- CONTRACT_AMOUNT, RECEIPT_AMOUNT, PROFIT
    rules JSONB NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_commission_plans_type ON commission_plans(type);
CREATE INDEX idx_commission_plans_active ON commission_plans(is_active);

-- Commission Allocations Table
CREATE TABLE commission_allocations (
    id BIGSERIAL PRIMARY KEY,
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    commission_plan_id BIGINT REFERENCES commission_plans(id),
    participants JSONB NOT NULL, -- Array of {user_id, role, percentage/amount}
    total_commission_amount DECIMAL(18,2),
    calculation_details JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_commission_allocations_contract ON commission_allocations(sales_contract_id);
CREATE INDEX idx_commission_allocations_plan ON commission_allocations(commission_plan_id);

-- Commission Accruals Table
CREATE TABLE commission_accruals (
    id BIGSERIAL PRIMARY KEY,
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    period VARCHAR(7) NOT NULL, -- YYYY-MM
    accrual_type VARCHAR(20), -- SALES, SERVICE, MANAGEMENT, CONTRIBUTION
    stage VARCHAR(50), -- SIGNING, RECEIPT, COMPLETION
    base_amount DECIMAL(18,2),
    commission_rate DECIMAL(5,2),
    commission_amount DECIMAL(18,2) NOT NULL,
    is_negative BOOLEAN DEFAULT FALSE,
    source_revision_id BIGINT REFERENCES contract_revisions(id),
    accrual_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, PAID, CANCELLED
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_commission_accruals_contract ON commission_accruals(sales_contract_id);
CREATE INDEX idx_commission_accruals_user ON commission_accruals(user_id);
CREATE INDEX idx_commission_accruals_period ON commission_accruals(period);
CREATE INDEX idx_commission_accruals_status ON commission_accruals(accrual_status);

-- Commission Payouts Table
CREATE TABLE commission_payouts (
    id BIGSERIAL PRIMARY KEY,
    period VARCHAR(7) NOT NULL, -- YYYY-MM
    user_id BIGINT NOT NULL REFERENCES users(id),
    total_amount DECIMAL(18,2) NOT NULL,
    deductions DECIMAL(18,2) DEFAULT 0,
    net_amount DECIMAL(18,2),
    payout_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, PAID
    payout_date DATE,
    bank_reference VARCHAR(100),
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark TEXT
);

CREATE INDEX idx_commission_payouts_period ON commission_payouts(period);
CREATE INDEX idx_commission_payouts_user ON commission_payouts(user_id);
CREATE INDEX idx_commission_payouts_status ON commission_payouts(payout_status);

-- Commission Adjustments Table
CREATE TABLE commission_adjustments (
    id BIGSERIAL PRIMARY KEY,
    accrual_id BIGINT REFERENCES commission_accruals(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    adjustment_type VARCHAR(20), -- CORRECTION, PENALTY, BONUS
    original_amount DECIMAL(18,2),
    adjustment_amount DECIMAL(18,2) NOT NULL,
    final_amount DECIMAL(18,2),
    reason TEXT NOT NULL,
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_commission_adjustments_accrual ON commission_adjustments(accrual_id);
CREATE INDEX idx_commission_adjustments_user ON commission_adjustments(user_id);

-- One-time Bonuses Table
CREATE TABLE bonuses_once (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL, -- TRANSFER, REFERRAL, SPECIAL
    project_id BIGINT REFERENCES projects(id),
    sales_contract_id BIGINT REFERENCES sales_contracts(id),
    from_user_id BIGINT REFERENCES users(id),
    to_user_id BIGINT NOT NULL REFERENCES users(id),
    amount DECIMAL(18,2) NOT NULL,
    reason TEXT,
    bonus_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, PAID, CANCELLED
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    paid_at TIMESTAMP,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bonuses_once_to_user ON bonuses_once(to_user_id);
CREATE INDEX idx_bonuses_once_status ON bonuses_once(bonus_status);

-- =============================================
-- 13. Workflow and Notification Tables
-- =============================================

-- Workflow Processes Table
CREATE TABLE workflow_processes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    version INTEGER DEFAULT 1,
    bpmn_xml TEXT,
    form_config JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    deployment_id VARCHAR(100),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_processes_category ON workflow_processes(category);
CREATE INDEX idx_workflow_processes_active ON workflow_processes(is_active);

-- Workflow Instances Table
CREATE TABLE workflow_instances (
    id BIGSERIAL PRIMARY KEY,
    process_id BIGINT NOT NULL REFERENCES workflow_processes(id),
    process_instance_id VARCHAR(100),
    business_type VARCHAR(50) NOT NULL,
    business_id BIGINT NOT NULL,
    business_key VARCHAR(100),
    title VARCHAR(200),
    initiator_id BIGINT REFERENCES users(id),
    current_task VARCHAR(100),
    current_assignee BIGINT REFERENCES users(id),
    instance_status VARCHAR(20) DEFAULT 'RUNNING', -- RUNNING, COMPLETED, CANCELLED, SUSPENDED
    variables JSONB,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_instances_process ON workflow_instances(process_id);
CREATE INDEX idx_workflow_instances_business ON workflow_instances(business_type, business_id);
CREATE INDEX idx_workflow_instances_initiator ON workflow_instances(initiator_id);
CREATE INDEX idx_workflow_instances_status ON workflow_instances(instance_status);

-- Workflow Tasks Table
CREATE TABLE workflow_tasks (
    id BIGSERIAL PRIMARY KEY,
    instance_id BIGINT NOT NULL REFERENCES workflow_instances(id),
    task_id VARCHAR(100),
    task_name VARCHAR(100),
    task_type VARCHAR(20), -- USER_TASK, SERVICE_TASK, GATEWAY
    assignee_id BIGINT REFERENCES users(id),
    candidate_users JSONB,
    candidate_groups JSONB,
    due_date TIMESTAMP,
    priority INTEGER DEFAULT 50,
    task_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, CLAIMED, COMPLETED, DELEGATED, CANCELLED
    form_data JSONB,
    comments TEXT,
    completed_by BIGINT REFERENCES users(id),
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_tasks_instance ON workflow_tasks(instance_id);
CREATE INDEX idx_workflow_tasks_assignee ON workflow_tasks(assignee_id);
CREATE INDEX idx_workflow_tasks_status ON workflow_tasks(task_status);

-- Notifications Table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    channel VARCHAR(20) NOT NULL, -- EMAIL, SMS, WECHAT, SYSTEM
    template_id BIGINT,
    to_user_id BIGINT REFERENCES users(id),
    to_address VARCHAR(200),
    subject VARCHAR(200),
    content TEXT,
    variables JSONB,
    priority VARCHAR(20) DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, URGENT
    send_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SENT, FAILED, CANCELLED
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(to_user_id);
CREATE INDEX idx_notifications_status ON notifications(send_status);
CREATE INDEX idx_notifications_created ON notifications(created_at);

-- Notification Templates Table
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject_template VARCHAR(500),
    content_template TEXT NOT NULL,
    variables_schema JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_templates_code ON notification_templates(code);
CREATE INDEX idx_notification_templates_channel ON notification_templates(channel);

-- =============================================
-- 14. AI and Analytics Tables
-- =============================================

-- AI Tasks Table
CREATE TABLE ai_tasks (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- LEAD_SCORE, WIN_RATE, NEXT_ACTION, ANOMALY
    subject VARCHAR(200),
    business_type VARCHAR(50),
    business_id BIGINT,
    due_at TIMESTAMP,
    priority VARCHAR(20) DEFAULT 'NORMAL',
    input_data JSONB,
    output_data JSONB,
    suggestions JSONB,
    confidence_score DECIMAL(3,2),
    task_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED
    error_message TEXT,
    processing_time_ms INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_ai_tasks_type ON ai_tasks(type);
CREATE INDEX idx_ai_tasks_business ON ai_tasks(business_type, business_id);
CREATE INDEX idx_ai_tasks_status ON ai_tasks(task_status);

-- Voice Notes Table
CREATE TABLE voice_notes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    audio_url VARCHAR(500),
    audio_duration_seconds INTEGER,
    asr_text TEXT,
    asr_confidence DECIMAL(3,2),
    llm_extracted_json JSONB,
    llm_confidence DECIMAL(3,2),
    linked_entity_type VARCHAR(50),
    linked_entity_id BIGINT,
    processing_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);

CREATE INDEX idx_voice_notes_user ON voice_notes(user_id);
CREATE INDEX idx_voice_notes_linked ON voice_notes(linked_entity_type, linked_entity_id);
CREATE INDEX idx_voice_notes_status ON voice_notes(processing_status);

-- Insights Table
CREATE TABLE insights (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- TREND, ANOMALY, PREDICTION, RECOMMENDATION
    scope VARCHAR(50), -- USER, TEAM, DEPARTMENT, COMPANY
    scope_id BIGINT,
    period VARCHAR(20),
    title VARCHAR(200),
    content TEXT,
    data_points JSONB,
    score DECIMAL(5,2),
    priority VARCHAR(20),
    is_actionable BOOLEAN DEFAULT FALSE,
    action_taken BOOLEAN DEFAULT FALSE,
    generated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_insights_type ON insights(type);
CREATE INDEX idx_insights_scope ON insights(scope, scope_id);
CREATE INDEX idx_insights_generated ON insights(generated_at);

-- Forecasts Table
CREATE TABLE forecasts (
    id BIGSERIAL PRIMARY KEY,
    scope VARCHAR(50) NOT NULL, -- USER, TEAM, DEPARTMENT, COMPANY
    scope_id BIGINT,
    period VARCHAR(20) NOT NULL, -- MONTH, QUARTER, YEAR
    period_value VARCHAR(20) NOT NULL, -- 2025-01, 2025-Q1, 2025
    forecast_type VARCHAR(50), -- REVENUE, DEALS, RECEIPTS
    forecast_amount DECIMAL(18,2),
    confidence_level DECIMAL(3,2),
    method VARCHAR(50), -- ML, STATISTICAL, HYBRID
    input_metrics JSONB,
    assumptions JSONB,
    generated_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_forecasts_scope ON forecasts(scope, scope_id);
CREATE INDEX idx_forecasts_period ON forecasts(period, period_value);
CREATE INDEX idx_forecasts_type ON forecasts(forecast_type);

-- Anomaly Events Table
CREATE TABLE anomaly_events (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- PRICE, DISCOUNT, REVISION, ACTIVITY
    severity VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    business_type VARCHAR(50),
    business_id BIGINT,
    description TEXT,
    detected_value JSONB,
    expected_range JSONB,
    deviation_percentage DECIMAL(10,2),
    detected_at TIMESTAMP NOT NULL,
    acknowledged BOOLEAN DEFAULT FALSE,
    acknowledged_by BIGINT REFERENCES users(id),
    acknowledged_at TIMESTAMP,
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_anomaly_events_type ON anomaly_events(type);
CREATE INDEX idx_anomaly_events_severity ON anomaly_events(severity);
CREATE INDEX idx_anomaly_events_business ON anomaly_events(business_type, business_id);
CREATE INDEX idx_anomaly_events_detected ON anomaly_events(detected_at);

-- =============================================
-- 15. File Management Table
-- =============================================

-- File Objects Table
CREATE TABLE file_objects (
    id BIGSERIAL PRIMARY KEY,
    business_type VARCHAR(50) NOT NULL,
    business_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    file_type VARCHAR(50),
    file_size BIGINT,
    mime_type VARCHAR(100),
    storage_path VARCHAR(500),
    url VARCHAR(500),
    thumbnail_url VARCHAR(500),
    hash_md5 VARCHAR(32),
    watermark_info JSONB,
    upload_user_id BIGINT REFERENCES users(id),
    upload_ip VARCHAR(50),
    download_count INTEGER DEFAULT 0,
    last_download_at TIMESTAMP,
    is_public BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_file_objects_business ON file_objects(business_type, business_id);
CREATE INDEX idx_file_objects_user ON file_objects(upload_user_id);
CREATE INDEX idx_file_objects_created ON file_objects(created_at);