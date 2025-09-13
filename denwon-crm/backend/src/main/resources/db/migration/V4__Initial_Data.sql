-- =============================================
-- Initial Data Setup
-- Version: 4.0.0
-- =============================================

-- Insert default organization units
INSERT INTO org_units (code, name, parent_id, level, path, type, status) VALUES
('HQ', '总部', NULL, 1, '/HQ', 'COMPANY', 1),
('SALES', '销售部', 1, 2, '/HQ/SALES', 'DEPARTMENT', 1),
('TECH', '技术部', 1, 2, '/HQ/TECH', 'DEPARTMENT', 1),
('SERVICE', '服务部', 1, 2, '/HQ/SERVICE', 'DEPARTMENT', 1),
('FINANCE', '财务部', 1, 2, '/HQ/FINANCE', 'DEPARTMENT', 1),
('ADMIN', '行政部', 1, 2, '/HQ/ADMIN', 'DEPARTMENT', 1);

-- Insert default roles
INSERT INTO roles (code, name, description, type, status) VALUES
('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 'SYSTEM', 1),
('ADMIN', '系统管理员', '系统管理员，可管理系统配置', 'SYSTEM', 1),
('GENERAL_MANAGER', '总经理', '公司总经理角色', 'SYSTEM', 1),
('SALES_MANAGER', '销售经理', '销售部门经理', 'SYSTEM', 1),
('SALES', '销售员', '销售人员', 'SYSTEM', 1),
('TECH_SUPPORT', '技术支持', '技术支持人员', 'SYSTEM', 1),
('SERVICE_ENGINEER', '服务工程师', '售后服务工程师', 'SYSTEM', 1),
('FINANCE', '财务', '财务人员', 'SYSTEM', 1),
('ADMIN_STAFF', '行政', '行政人员', 'SYSTEM', 1),
('VIEWER', '访客', '只读访问权限', 'SYSTEM', 1);

-- Insert permissions
INSERT INTO permissions (code, name, module, action, resource) VALUES
-- System permissions
('system.view', '查看系统设置', 'system', 'view', 'settings'),
('system.manage', '管理系统设置', 'system', 'manage', 'settings'),
('user.view', '查看用户', 'system', 'view', 'user'),
('user.create', '创建用户', 'system', 'create', 'user'),
('user.update', '更新用户', 'system', 'update', 'user'),
('user.delete', '删除用户', 'system', 'delete', 'user'),
('role.view', '查看角色', 'system', 'view', 'role'),
('role.manage', '管理角色', 'system', 'manage', 'role'),
('org.view', '查看组织', 'system', 'view', 'organization'),
('org.manage', '管理组织', 'system', 'manage', 'organization'),

-- Customer permissions
('customer.view', '查看客户', 'customer', 'view', 'customer'),
('customer.create', '创建客户', 'customer', 'create', 'customer'),
('customer.update', '更新客户', 'customer', 'update', 'customer'),
('customer.delete', '删除客户', 'customer', 'delete', 'customer'),
('customer.export', '导出客户', 'customer', 'export', 'customer'),
('customer.import', '导入客户', 'customer', 'import', 'customer'),
('customer.transfer', '转移客户', 'customer', 'transfer', 'customer'),
('customer.share', '共享客户', 'customer', 'share', 'customer'),
('customer.pool.view', '查看公海', 'customer', 'view', 'pool'),
('customer.pool.claim', '认领公海客户', 'customer', 'claim', 'pool'),
('customer.pool.release', '释放至公海', 'customer', 'release', 'pool'),

-- Lead permissions
('lead.view', '查看线索', 'lead', 'view', 'lead'),
('lead.create', '创建线索', 'lead', 'create', 'lead'),
('lead.update', '更新线索', 'lead', 'update', 'lead'),
('lead.delete', '删除线索', 'lead', 'delete', 'lead'),
('lead.assign', '分配线索', 'lead', 'assign', 'lead'),
('lead.convert', '转化线索', 'lead', 'convert', 'lead'),

-- Opportunity permissions
('opportunity.view', '查看商机', 'opportunity', 'view', 'opportunity'),
('opportunity.create', '创建商机', 'opportunity', 'create', 'opportunity'),
('opportunity.update', '更新商机', 'opportunity', 'update', 'opportunity'),
('opportunity.delete', '删除商机', 'opportunity', 'delete', 'opportunity'),
('quote.view', '查看报价', 'opportunity', 'view', 'quote'),
('quote.create', '创建报价', 'opportunity', 'create', 'quote'),
('quote.update', '更新报价', 'opportunity', 'update', 'quote'),
('quote.approve', '审批报价', 'opportunity', 'approve', 'quote'),

-- Contract permissions
('contract.view', '查看合同', 'contract', 'view', 'contract'),
('contract.create', '创建合同', 'contract', 'create', 'contract'),
('contract.update', '更新合同', 'contract', 'update', 'contract'),
('contract.delete', '删除合同', 'contract', 'delete', 'contract'),
('contract.approve', '审批合同', 'contract', 'approve', 'contract'),
('contract.revise', '修订合同', 'contract', 'revise', 'contract'),

-- Project permissions
('project.view', '查看项目', 'project', 'view', 'project'),
('project.create', '创建项目', 'project', 'create', 'project'),
('project.update', '更新项目', 'project', 'update', 'project'),
('project.delete', '删除项目', 'project', 'delete', 'project'),
('project.manage', '管理项目', 'project', 'manage', 'project'),

-- Finance permissions
('receivable.view', '查看应收', 'finance', 'view', 'receivable'),
('receivable.manage', '管理应收', 'finance', 'manage', 'receivable'),
('receipt.view', '查看回款', 'finance', 'view', 'receipt'),
('receipt.create', '登记回款', 'finance', 'create', 'receipt'),
('invoice.view', '查看发票', 'finance', 'view', 'invoice'),
('invoice.create', '开具发票', 'finance', 'create', 'invoice'),
('commission.view', '查看提成', 'finance', 'view', 'commission'),
('commission.manage', '管理提成', 'finance', 'manage', 'commission'),

-- Service permissions
('asset.view', '查看设备', 'service', 'view', 'asset'),
('asset.manage', '管理设备', 'service', 'manage', 'asset'),
('service.view', '查看工单', 'service', 'view', 'order'),
('service.create', '创建工单', 'service', 'create', 'order'),
('service.update', '更新工单', 'service', 'update', 'order'),
('service.assign', '分配工单', 'service', 'assign', 'order'),

-- Report permissions
('report.view', '查看报表', 'report', 'view', 'report'),
('report.export', '导出报表', 'report', 'export', 'report'),
('dashboard.view', '查看仪表盘', 'report', 'view', 'dashboard');

-- Assign permissions to roles
-- Super Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- Admin gets most permissions except financial management
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions 
WHERE code NOT IN ('commission.manage', 'receivable.manage');

-- General Manager gets all business permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions 
WHERE module IN ('customer', 'lead', 'opportunity', 'contract', 'project', 'finance', 'service', 'report');

-- Sales Manager permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, id FROM permissions 
WHERE module IN ('customer', 'lead', 'opportunity', 'contract', 'report')
AND action IN ('view', 'create', 'update', 'assign', 'approve', 'export');

-- Sales permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 5, id FROM permissions 
WHERE module IN ('customer', 'lead', 'opportunity', 'contract')
AND action IN ('view', 'create', 'update')
AND code NOT IN ('customer.delete', 'lead.delete', 'opportunity.delete', 'contract.delete', 'contract.approve');

-- Insert default admin user (password: Admin@123)
INSERT INTO users (username, password, email, mobile, name, employee_no, org_unit_id, title, status) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'admin@denwon.com', '13800000000', '系统管理员', 'EMP001', 1, '系统管理员', 1);

-- Assign super admin role to admin user
INSERT INTO user_roles (user_id, role_id, assigned_by) VALUES
(1, 1, 1);

-- Insert data dictionaries
-- Customer levels
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('CUSTOMER_LEVEL', 'VIP', 'VIP客户', 'VIP', 1, 1),
('CUSTOMER_LEVEL', 'KEY', '重点客户', 'KEY', 2, 1),
('CUSTOMER_LEVEL', 'NORMAL', '普通客户', 'NORMAL', 3, 1),
('CUSTOMER_LEVEL', 'POTENTIAL', '潜在客户', 'POTENTIAL', 4, 1);

-- Customer sources
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('CUSTOMER_SOURCE', 'WEBSITE', '官网', 'WEBSITE', 1, 1),
('CUSTOMER_SOURCE', 'PHONE', '电话', 'PHONE', 2, 1),
('CUSTOMER_SOURCE', 'REFERRAL', '转介绍', 'REFERRAL', 3, 1),
('CUSTOMER_SOURCE', 'EXHIBITION', '展会', 'EXHIBITION', 4, 1),
('CUSTOMER_SOURCE', 'PARTNER', '合作伙伴', 'PARTNER', 5, 1),
('CUSTOMER_SOURCE', 'OTHER', '其他', 'OTHER', 99, 1);

-- Industries
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('INDUSTRY', 'IT', '信息技术', 'IT', 1, 1),
('INDUSTRY', 'MANUFACTURING', '制造业', 'MANUFACTURING', 2, 1),
('INDUSTRY', 'FINANCE', '金融业', 'FINANCE', 3, 1),
('INDUSTRY', 'RETAIL', '零售业', 'RETAIL', 4, 1),
('INDUSTRY', 'HEALTHCARE', '医疗健康', 'HEALTHCARE', 5, 1),
('INDUSTRY', 'EDUCATION', '教育', 'EDUCATION', 6, 1),
('INDUSTRY', 'REAL_ESTATE', '房地产', 'REAL_ESTATE', 7, 1),
('INDUSTRY', 'LOGISTICS', '物流运输', 'LOGISTICS', 8, 1),
('INDUSTRY', 'ENERGY', '能源', 'ENERGY', 9, 1),
('INDUSTRY', 'OTHER', '其他', 'OTHER', 99, 1);

-- Activity types
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('ACTIVITY_TYPE', 'VISIT', '拜访', 'VISIT', 1, 1),
('ACTIVITY_TYPE', 'CALL', '电话', 'CALL', 2, 1),
('ACTIVITY_TYPE', 'EMAIL', '邮件', 'EMAIL', 3, 1),
('ACTIVITY_TYPE', 'MEETING', '会议', 'MEETING', 4, 1),
('ACTIVITY_TYPE', 'DEMO', '演示', 'DEMO', 5, 1),
('ACTIVITY_TYPE', 'TRAINING', '培训', 'TRAINING', 6, 1);

-- Opportunity stages
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('OPPORTUNITY_STAGE', 'INITIAL', '初步接触', '10', 1, 1),
('OPPORTUNITY_STAGE', 'REQUIREMENT', '需求确认', '20', 2, 1),
('OPPORTUNITY_STAGE', 'PROPOSAL', '方案报价', '40', 3, 1),
('OPPORTUNITY_STAGE', 'NEGOTIATION', '商务谈判', '60', 4, 1),
('OPPORTUNITY_STAGE', 'CONTRACT', '合同签订', '80', 5, 1),
('OPPORTUNITY_STAGE', 'WON', '赢单', '100', 6, 1),
('OPPORTUNITY_STAGE', 'LOST', '输单', '0', 7, 1);

-- Contract types
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('CONTRACT_TYPE', 'DIRECT', '直销合同', 'DIRECT', 1, 1),
('CONTRACT_TYPE', 'FRAMEWORK', '框架合同', 'FRAMEWORK', 2, 1),
('CONTRACT_TYPE', 'AGENCY', '代理合同', 'AGENCY', 3, 1),
('CONTRACT_TYPE', 'SERVICE', '服务合同', 'SERVICE', 4, 1);

-- Payment methods
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('PAYMENT_METHOD', 'BANK_TRANSFER', '银行转账', 'BANK_TRANSFER', 1, 1),
('PAYMENT_METHOD', 'CHECK', '支票', 'CHECK', 2, 1),
('PAYMENT_METHOD', 'CASH', '现金', 'CASH', 3, 1),
('PAYMENT_METHOD', 'ONLINE', '在线支付', 'ONLINE', 4, 1),
('PAYMENT_METHOD', 'LC', '信用证', 'LC', 5, 1);

-- Service order types
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('SERVICE_TYPE', 'INSTALLATION', '安装', 'INSTALLATION', 1, 1),
('SERVICE_TYPE', 'REPAIR', '维修', 'REPAIR', 2, 1),
('SERVICE_TYPE', 'MAINTENANCE', '保养', 'MAINTENANCE', 3, 1),
('SERVICE_TYPE', 'INSPECTION', '巡检', 'INSPECTION', 4, 1),
('SERVICE_TYPE', 'UPGRADE', '升级', 'UPGRADE', 5, 1);

-- Priority levels
INSERT INTO dictionaries (type, code, name, value, sort_order, status) VALUES
('PRIORITY', 'LOW', '低', '1', 1, 1),
('PRIORITY', 'MEDIUM', '中', '2', 2, 1),
('PRIORITY', 'HIGH', '高', '3', 3, 1),
('PRIORITY', 'URGENT', '紧急', '4', 4, 1);

-- Insert default opportunity stage template
INSERT INTO opportunity_stage_templates (name, org_unit_id, stages, is_default, status) VALUES
('标准销售流程', NULL, 
'[
  {"code": "INITIAL", "name": "初步接触", "probability": 10, "required_fields": ["customer_id", "contact_id"], "exit_criteria": "完成初次沟通"},
  {"code": "REQUIREMENT", "name": "需求确认", "probability": 20, "required_fields": ["expected_amount"], "exit_criteria": "需求调研报告"},
  {"code": "PROPOSAL", "name": "方案报价", "probability": 40, "required_fields": ["quote_id"], "exit_criteria": "提交报价单"},
  {"code": "NEGOTIATION", "name": "商务谈判", "probability": 60, "required_fields": [], "exit_criteria": "达成初步意向"},
  {"code": "CONTRACT", "name": "合同签订", "probability": 80, "required_fields": [], "exit_criteria": "合同审批通过"},
  {"code": "WON", "name": "赢单", "probability": 100, "required_fields": ["actual_sign_at"], "exit_criteria": "合同签署完成"},
  {"code": "LOST", "name": "输单", "probability": 0, "required_fields": ["win_loss_reason"], "exit_criteria": "记录失败原因"}
]'::jsonb, 
true, 1);

-- Insert sample product categories
INSERT INTO product_categories (code, name, parent_id, level, sort_order, status) VALUES
('SOFTWARE', '软件产品', NULL, 1, 1, 1),
('HARDWARE', '硬件产品', NULL, 1, 2, 1),
('SERVICE', '服务产品', NULL, 1, 3, 1),
('CRM', 'CRM系统', 1, 2, 1, 1),
('ERP', 'ERP系统', 1, 2, 2, 1),
('SERVER', '服务器', 2, 2, 1, 1),
('NETWORK', '网络设备', 2, 2, 2, 1),
('IMPL', '实施服务', 3, 2, 1, 1),
('SUPPORT', '技术支持', 3, 2, 2, 1);

-- Insert sample products
INSERT INTO products (code, name, category_id, brand, model, uom, tax_rate, standard_price, status) VALUES
('CRM-STD', 'Denwon CRM 标准版', 4, 'Denwon', 'V1.0', '套', 13.00, 98000.00, 1),
('CRM-ENT', 'Denwon CRM 企业版', 4, 'Denwon', 'V1.0', '套', 13.00, 298000.00, 1),
('CRM-IMPL', 'CRM实施服务', 8, 'Denwon', 'Standard', '人天', 6.00, 3000.00, 1),
('CRM-SUPPORT', 'CRM技术支持服务', 9, 'Denwon', 'Annual', '年', 6.00, 50000.00, 1);

-- Create notification templates
INSERT INTO notification_templates (code, name, channel, subject_template, content_template, is_active) VALUES
('CUSTOMER_ASSIGNED', '客户分配通知', 'SYSTEM', '您有新的客户分配', '您好，{{assignee_name}}！\n\n客户 {{customer_name}} 已分配给您，请及时跟进。\n\n分配人：{{assigner_name}}\n分配时间：{{assign_time}}', true),
('LEAD_ASSIGNED', '线索分配通知', 'SYSTEM', '您有新的线索分配', '您好，{{assignee_name}}！\n\n线索 {{lead_name}} 已分配给您，请及时跟进。\n\n线索来源：{{lead_source}}\n分配时间：{{assign_time}}', true),
('CONTRACT_APPROVAL', '合同审批通知', 'SYSTEM', '合同审批请求', '您有新的合同审批请求：\n\n合同编号：{{contract_code}}\n客户名称：{{customer_name}}\n合同金额：{{contract_amount}}\n提交人：{{submitter_name}}\n\n请登录系统进行审批。', true),
('TASK_REMINDER', '任务提醒', 'SYSTEM', '任务即将到期', '您的任务 {{task_name}} 即将在 {{due_date}} 到期，请及时处理。', true),
('RECEIPT_CONFIRMED', '回款确认通知', 'SYSTEM', '回款已确认', '回款已确认：\n\n客户：{{customer_name}}\n金额：{{amount}}\n到账日期：{{receipt_date}}', true);