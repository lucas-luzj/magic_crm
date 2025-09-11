-- 插入系统字典数据
INSERT INTO dictionary (dict_key, dict_name, dict_value, dict_type, description, sort_order, is_system, is_active, created_by) VALUES
-- 表单分类
('form_category_leave', '请假申请', 'leave', 'form_category', '请假相关表单分类', 1, TRUE, TRUE, 'system'),
('form_category_expense', '费用报销', 'expense', 'form_category', '费用报销相关表单分类', 2, TRUE, TRUE, 'system'),
('form_category_purchase', '采购申请', 'purchase', 'form_category', '采购相关表单分类', 3, TRUE, TRUE, 'system'),
('form_category_approval', '审批流程', 'approval', 'form_category', '审批相关表单分类', 4, TRUE, TRUE, 'system'),
('form_category_other', '其他', 'other', 'form_category', '其他类型表单分类', 5, TRUE, TRUE, 'system'),

-- 表单状态
('form_status_active', '激活', 'ACTIVE', 'form_status', '表单模板激活状态', 1, TRUE, TRUE, 'system'),
('form_status_inactive', '停用', 'INACTIVE', 'form_status', '表单模板停用状态', 2, TRUE, TRUE, 'system'),
('form_status_draft', '草稿', 'DRAFT', 'form_status', '表单模板草稿状态', 3, TRUE, TRUE, 'system'),

-- 表单数据状态
('form_data_status_draft', '草稿', 'DRAFT', 'form_data_status', '表单数据草稿状态', 1, TRUE, TRUE, 'system'),
('form_data_status_submitted', '已提交', 'SUBMITTED', 'form_data_status', '表单数据已提交状态', 2, TRUE, TRUE, 'system'),
('form_data_status_approved', '已审批', 'APPROVED', 'form_data_status', '表单数据已审批状态', 3, TRUE, TRUE, 'system'),
('form_data_status_rejected', '已拒绝', 'REJECTED', 'form_data_status', '表单数据已拒绝状态', 4, TRUE, TRUE, 'system'),

-- 流程表单映射类型
('mapping_type_start', '开始表单', 'START', 'mapping_type', '流程开始时的表单', 1, TRUE, TRUE, 'system'),
('mapping_type_task', '任务表单', 'TASK', 'mapping_type', '任务节点的表单', 2, TRUE, TRUE, 'system'),
('mapping_type_end', '结束表单', 'END', 'mapping_type', '流程结束时的表单', 3, TRUE, TRUE, 'system'),

-- 表单字段类型
('field_type_text', '文本', 'text', 'field_type', '文本输入框', 1, TRUE, TRUE, 'system'),
('field_type_textarea', '多行文本', 'textarea', 'field_type', '多行文本输入框', 2, TRUE, TRUE, 'system'),
('field_type_number', '数字', 'number', 'field_type', '数字输入框', 3, TRUE, TRUE, 'system'),
('field_type_date', '日期', 'date', 'field_type', '日期选择器', 4, TRUE, TRUE, 'system'),
('field_type_datetime', '日期时间', 'datetime', 'field_type', '日期时间选择器', 5, TRUE, TRUE, 'system'),
('field_type_select', '下拉选择', 'select', 'field_type', '下拉选择框', 6, TRUE, TRUE, 'system'),
('field_type_radio', '单选', 'radio', 'field_type', '单选按钮组', 7, TRUE, TRUE, 'system'),
('field_type_checkbox', '多选', 'checkbox', 'field_type', '多选按钮组', 8, TRUE, TRUE, 'system'),
('field_type_file', '文件上传', 'file', 'field_type', '文件上传组件', 9, TRUE, TRUE, 'system');
