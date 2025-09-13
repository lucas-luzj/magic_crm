-- =============================================
-- Contract, Project and Financial Tables
-- Version: 2.0.0
-- =============================================

-- =============================================
-- 7. Contract Management Tables
-- =============================================

-- Sales Contracts Table
CREATE TABLE sales_contracts (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL, -- DIRECT, FRAMEWORK, AGENCY, SERVICE
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    opportunity_id BIGINT REFERENCES opportunities(id),
    quote_id BIGINT REFERENCES quotes(id),
    currency VARCHAR(10) DEFAULT 'CNY',
    exchange_rate DECIMAL(10,4) DEFAULT 1.0,
    tax_rate DECIMAL(5,2),
    amount_ex_tax DECIMAL(18,2) NOT NULL,
    amount_inc_tax DECIMAL(18,2) NOT NULL,
    tax_amount DECIMAL(18,2),
    discount_amount DECIMAL(18,2),
    signer_entity VARCHAR(200),
    signer_name VARCHAR(100),
    signer_contact VARCHAR(100),
    signed_date DATE,
    effective_date DATE,
    expiry_date DATE,
    delivery_address TEXT,
    delivery_contact VARCHAR(100),
    delivery_phone VARCHAR(20),
    payment_term_id BIGINT,
    payment_method VARCHAR(50),
    contract_terms TEXT,
    special_terms TEXT,
    approval_status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, PENDING, APPROVED, REJECTED, SIGNED
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    contract_status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, ACTIVE, COMPLETED, TERMINATED
    attachments JSONB,
    custom_fields JSONB,
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

CREATE INDEX idx_sales_contracts_customer ON sales_contracts(customer_id);
CREATE INDEX idx_sales_contracts_opportunity ON sales_contracts(opportunity_id);
CREATE INDEX idx_sales_contracts_status ON sales_contracts(contract_status);
CREATE INDEX idx_sales_contracts_signed_date ON sales_contracts(signed_date);

-- Contract Items Table
CREATE TABLE contract_items (
    id BIGSERIAL PRIMARY KEY,
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    product_id BIGINT REFERENCES products(id),
    product_code VARCHAR(50),
    product_name VARCHAR(200) NOT NULL,
    specification TEXT,
    uom VARCHAR(20),
    quantity DECIMAL(18,4) NOT NULL,
    delivered_quantity DECIMAL(18,4) DEFAULT 0,
    unit_price_ex_tax DECIMAL(18,4) NOT NULL,
    tax_rate DECIMAL(5,2),
    discount_rate DECIMAL(5,2),
    discount_amount DECIMAL(18,2),
    subtotal_ex_tax DECIMAL(18,2),
    tax_amount DECIMAL(18,2),
    subtotal_inc_tax DECIMAL(18,2),
    delivery_due_date DATE,
    warranty_months INTEGER,
    note TEXT,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_items_contract ON contract_items(sales_contract_id);
CREATE INDEX idx_contract_items_product ON contract_items(product_id);

-- Contract Revisions Table
CREATE TABLE contract_revisions (
    id BIGSERIAL PRIMARY KEY,
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    revision_no INTEGER NOT NULL,
    revision_type VARCHAR(20) NOT NULL, -- SUPPLEMENT, CHANGE, TERMINATION
    change_summary TEXT,
    change_details JSONB,
    original_amount DECIMAL(18,2),
    revised_amount DECIMAL(18,2),
    delta_amount DECIMAL(18,2),
    reason TEXT,
    effective_date DATE,
    approval_status VARCHAR(20) DEFAULT 'DRAFT',
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sales_contract_id, revision_no)
);

CREATE INDEX idx_contract_revisions_contract ON contract_revisions(sales_contract_id);
CREATE INDEX idx_contract_revisions_status ON contract_revisions(approval_status);

-- Orders Table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    sales_contract_id BIGINT REFERENCES sales_contracts(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    order_date DATE NOT NULL,
    delivery_address TEXT,
    delivery_contact VARCHAR(100),
    delivery_phone VARCHAR(20),
    planned_ship_date DATE,
    actual_ship_date DATE,
    order_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    total_amount DECIMAL(18,2),
    attachments JSONB,
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

CREATE INDEX idx_orders_contract ON orders(sales_contract_id);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_orders_date ON orders(order_date);

-- Order Items Table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    contract_item_id BIGINT REFERENCES contract_items(id),
    product_id BIGINT REFERENCES products(id),
    product_code VARCHAR(50),
    product_name VARCHAR(200) NOT NULL,
    specification TEXT,
    uom VARCHAR(20),
    quantity DECIMAL(18,4) NOT NULL,
    shipped_quantity DECIMAL(18,4) DEFAULT 0,
    unit_price DECIMAL(18,4),
    subtotal DECIMAL(18,2),
    item_status VARCHAR(20) DEFAULT 'PENDING',
    sort_order INTEGER DEFAULT 0
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- Deliveries Table
CREATE TABLE deliveries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    order_id BIGINT REFERENCES orders(id),
    delivery_date DATE NOT NULL,
    shipped_date DATE,
    carrier VARCHAR(100),
    tracking_no VARCHAR(100),
    delivery_status VARCHAR(20) DEFAULT 'PREPARING', -- PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, RETURNED
    delivery_address TEXT,
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(20),
    receiver_signature VARCHAR(200),
    received_date DATE,
    doc_url VARCHAR(500),
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deliveries_order ON deliveries(order_id);
CREATE INDEX idx_deliveries_status ON deliveries(delivery_status);
CREATE INDEX idx_deliveries_date ON deliveries(delivery_date);

-- Delivery Items Table
CREATE TABLE delivery_items (
    id BIGSERIAL PRIMARY KEY,
    delivery_id BIGINT NOT NULL REFERENCES deliveries(id),
    order_item_id BIGINT REFERENCES order_items(id),
    product_id BIGINT REFERENCES products(id),
    serial_id BIGINT,
    quantity DECIMAL(18,4) NOT NULL,
    batch_no VARCHAR(50),
    note TEXT
);

CREATE INDEX idx_delivery_items_delivery ON delivery_items(delivery_id);
CREATE INDEX idx_delivery_items_product ON delivery_items(product_id);

-- =============================================
-- 8. Project Management Tables
-- =============================================

-- Projects Table
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL, -- IMPLEMENTATION, DELIVERY, INTERNAL
    customer_id BIGINT REFERENCES customers(id),
    sales_contract_id BIGINT REFERENCES sales_contracts(id),
    opportunity_id BIGINT REFERENCES opportunities(id),
    source_type VARCHAR(20), -- OPPORTUNITY, CONTRACT, INTERNAL
    manager_id BIGINT REFERENCES users(id),
    team_members JSONB,
    start_date DATE,
    end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    budget DECIMAL(18,2),
    actual_cost DECIMAL(18,2),
    project_status VARCHAR(20) DEFAULT 'PLANNING', -- PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED
    progress_percentage INTEGER DEFAULT 0,
    risk_level VARCHAR(20), -- LOW, MEDIUM, HIGH, CRITICAL
    attachments JSONB,
    custom_fields JSONB,
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

CREATE INDEX idx_projects_customer ON projects(customer_id);
CREATE INDEX idx_projects_contract ON projects(sales_contract_id);
CREATE INDEX idx_projects_manager ON projects(manager_id);
CREATE INDEX idx_projects_status ON projects(project_status);

-- Project Contract Mapping (N:N)
CREATE TABLE project_contracts (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    allocation_ratio DECIMAL(5,2),
    allocated_amount DECIMAL(18,2),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, sales_contract_id)
);

CREATE INDEX idx_project_contracts_project ON project_contracts(project_id);
CREATE INDEX idx_project_contracts_contract ON project_contracts(sales_contract_id);

-- Milestones Table
CREATE TABLE milestones (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    planned_date DATE,
    actual_date DATE,
    milestone_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, DELAYED
    deliverables TEXT,
    triggers_receivable BOOLEAN DEFAULT FALSE,
    receivable_amount DECIMAL(18,2),
    responsible_id BIGINT REFERENCES users(id),
    attachments JSONB,
    sort_order INTEGER DEFAULT 0,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_milestones_project ON milestones(project_id);
CREATE INDEX idx_milestones_status ON milestones(milestone_status);
CREATE INDEX idx_milestones_date ON milestones(planned_date);

-- Tasks Table
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id),
    milestone_id BIGINT REFERENCES milestones(id),
    parent_task_id BIGINT REFERENCES tasks(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    assignee_id BIGINT REFERENCES users(id),
    planned_start_date DATE,
    planned_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    estimated_hours DECIMAL(10,2),
    actual_hours DECIMAL(10,2),
    progress_percentage INTEGER DEFAULT 0,
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    task_status VARCHAR(20) DEFAULT 'TODO', -- TODO, IN_PROGRESS, DONE, CANCELLED
    dependencies JSONB,
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_milestone ON tasks(milestone_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_tasks_status ON tasks(task_status);

-- Timesheets Table
CREATE TABLE timesheets (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id),
    project_id BIGINT REFERENCES projects(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    work_date DATE NOT NULL,
    hours DECIMAL(10,2) NOT NULL,
    cost_rate DECIMAL(18,2),
    total_cost DECIMAL(18,2),
    work_type VARCHAR(50),
    description TEXT,
    approval_status VARCHAR(20) DEFAULT 'PENDING',
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_timesheets_task ON timesheets(task_id);
CREATE INDEX idx_timesheets_project ON timesheets(project_id);
CREATE INDEX idx_timesheets_user ON timesheets(user_id);
CREATE INDEX idx_timesheets_date ON timesheets(work_date);

-- =============================================
-- 9. Financial Management Tables
-- =============================================

-- Receivable Plans Table
CREATE TABLE receivable_plans (
    id BIGSERIAL PRIMARY KEY,
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    milestone_id BIGINT REFERENCES milestones(id),
    term_name VARCHAR(100) NOT NULL,
    term_type VARCHAR(20), -- ADVANCE, PROGRESS, FINAL, WARRANTY
    due_date DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'CNY',
    received_amount DECIMAL(18,2) DEFAULT 0,
    plan_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PARTIAL, RECEIVED, OVERDUE
    overdue_days INTEGER DEFAULT 0,
    penalty_amount DECIMAL(18,2) DEFAULT 0,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_receivable_plans_contract ON receivable_plans(sales_contract_id);
CREATE INDEX idx_receivable_plans_status ON receivable_plans(plan_status);
CREATE INDEX idx_receivable_plans_due_date ON receivable_plans(due_date);

-- Receipts Table
CREATE TABLE receipts (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    received_date DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'CNY',
    exchange_rate DECIMAL(10,4) DEFAULT 1.0,
    payment_method VARCHAR(50), -- BANK_TRANSFER, CHECK, CASH, ONLINE
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    bank_reference VARCHAR(100),
    receipt_status VARCHAR(20) DEFAULT 'UNALLOCATED', -- UNALLOCATED, PARTIAL, ALLOCATED
    allocated_amount DECIMAL(18,2) DEFAULT 0,
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark TEXT
);

CREATE INDEX idx_receipts_customer ON receipts(customer_id);
CREATE INDEX idx_receipts_date ON receipts(received_date);
CREATE INDEX idx_receipts_status ON receipts(receipt_status);

-- Receipt Applications Table (N:N mapping)
CREATE TABLE receipt_applications (
    id BIGSERIAL PRIMARY KEY,
    receipt_id BIGINT NOT NULL REFERENCES receipts(id),
    receivable_plan_id BIGINT NOT NULL REFERENCES receivable_plans(id),
    applied_amount DECIMAL(18,2) NOT NULL,
    applied_date DATE NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_receipt_applications_receipt ON receipt_applications(receipt_id);
CREATE INDEX idx_receipt_applications_plan ON receipt_applications(receivable_plan_id);

-- Invoices Table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    invoice_no VARCHAR(50),
    type VARCHAR(20) NOT NULL, -- NORMAL, SPECIAL, ELECTRONIC
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    sales_contract_id BIGINT REFERENCES sales_contracts(id),
    order_id BIGINT REFERENCES orders(id),
    receipt_id BIGINT REFERENCES receipts(id),
    title VARCHAR(200) NOT NULL,
    tax_no VARCHAR(50),
    address_phone TEXT,
    bank_account VARCHAR(100),
    amount DECIMAL(18,2) NOT NULL,
    tax_rate DECIMAL(5,2),
    tax_amount DECIMAL(18,2),
    total_amount DECIMAL(18,2),
    invoice_date DATE,
    invoice_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ISSUED, CANCELLED, RED_FLUSH
    red_flush_reason TEXT,
    original_invoice_id BIGINT REFERENCES invoices(id),
    pdf_url VARCHAR(500),
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark TEXT
);

CREATE INDEX idx_invoices_customer ON invoices(customer_id);
CREATE INDEX idx_invoices_contract ON invoices(sales_contract_id);
CREATE INDEX idx_invoices_status ON invoices(invoice_status);
CREATE INDEX idx_invoices_date ON invoices(invoice_date);

-- =============================================
-- 10. Purchase Management Tables
-- =============================================

-- Suppliers Table
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    uscc VARCHAR(50),
    region VARCHAR(100),
    address TEXT,
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    rating VARCHAR(20), -- A, B, C, D
    is_blacklist BOOLEAN DEFAULT FALSE,
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

CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_rating ON suppliers(rating);

-- Purchase Contracts Table
CREATE TABLE purchase_contracts (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    supplier_id BIGINT NOT NULL REFERENCES suppliers(id),
    contract_date DATE,
    currency VARCHAR(10) DEFAULT 'CNY',
    amount DECIMAL(18,2) NOT NULL,
    payment_terms TEXT,
    delivery_terms TEXT,
    contract_status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, ACTIVE, COMPLETED, CANCELLED
    approval_status VARCHAR(20) DEFAULT 'DRAFT',
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    attachments JSONB,
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

CREATE INDEX idx_purchase_contracts_supplier ON purchase_contracts(supplier_id);
CREATE INDEX idx_purchase_contracts_status ON purchase_contracts(contract_status);

-- Sales Purchase Mapping Table (N:N)
CREATE TABLE sales_purchase_mappings (
    id BIGSERIAL PRIMARY KEY,
    sales_contract_id BIGINT NOT NULL REFERENCES sales_contracts(id),
    purchase_contract_id BIGINT NOT NULL REFERENCES purchase_contracts(id),
    allocation_ratio DECIMAL(5,2),
    allocated_amount DECIMAL(18,2),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sales_contract_id, purchase_contract_id)
);

CREATE INDEX idx_sales_purchase_map_sales ON sales_purchase_mappings(sales_contract_id);
CREATE INDEX idx_sales_purchase_map_purchase ON sales_purchase_mappings(purchase_contract_id);

-- Inbounds Table
CREATE TABLE inbounds (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    purchase_contract_id BIGINT REFERENCES purchase_contracts(id),
    supplier_id BIGINT REFERENCES suppliers(id),
    received_date DATE NOT NULL,
    inspector_id BIGINT REFERENCES users(id),
    inspection_result VARCHAR(20), -- PASS, FAIL, PARTIAL
    inbound_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, RECEIVED, INSPECTED, STORED
    attachments JSONB,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark TEXT
);

CREATE INDEX idx_inbounds_purchase ON inbounds(purchase_contract_id);
CREATE INDEX idx_inbounds_supplier ON inbounds(supplier_id);
CREATE INDEX idx_inbounds_status ON inbounds(inbound_status);

-- Inbound Items Table
CREATE TABLE inbound_items (
    id BIGSERIAL PRIMARY KEY,
    inbound_id BIGINT NOT NULL REFERENCES inbounds(id),
    product_id BIGINT REFERENCES products(id),
    serial_id BIGINT,
    quantity DECIMAL(18,4) NOT NULL,
    inspection_qty DECIMAL(18,4),
    passed_qty DECIMAL(18,4),
    batch_no VARCHAR(50),
    production_date DATE,
    expiry_date DATE,
    result VARCHAR(20), -- PASS, FAIL
    note TEXT
);

CREATE INDEX idx_inbound_items_inbound ON inbound_items(inbound_id);
CREATE INDEX idx_inbound_items_product ON inbound_items(product_id);

-- Serial Numbers Table
CREATE TABLE serials (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    product_id BIGINT REFERENCES products(id),
    batch_no VARCHAR(50),
    manufacture_date DATE,
    warranty_months INTEGER,
    serial_status VARCHAR(20) DEFAULT 'IN_STOCK', -- IN_STOCK, SOLD, INSTALLED, RETURNED, SCRAPPED
    current_location VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_serials_product ON serials(product_id);
CREATE INDEX idx_serials_status ON serials(serial_status);