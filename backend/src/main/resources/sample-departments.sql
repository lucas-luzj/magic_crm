-- 示例部门数据
-- 注意：这个文件仅用于参考，实际部署时可以根据需要执行

-- 插入根部门
INSERT INTO departments (id, name, code, parent_id, manager_id, description, sort_order, is_active, created_at, updated_at) 
VALUES 
(1, '总公司', 'ROOT', NULL, NULL, '公司根部门', 0, TRUE, NOW(), NOW()),
(2, '技术部', 'TECH', 1, NULL, '负责技术研发工作', 1, TRUE, NOW(), NOW()),
(3, '销售部', 'SALES', 1, NULL, '负责销售业务', 2, TRUE, NOW(), NOW()),
(4, '人事部', 'HR', 1, NULL, '负责人力资源管理', 3, TRUE, NOW(), NOW()),
(5, '财务部', 'FINANCE', 1, NULL, '负责财务管理', 4, TRUE, NOW(), NOW()),
(6, '前端开发组', 'FRONTEND', 2, NULL, '前端开发团队', 1, TRUE, NOW(), NOW()),
(7, '后端开发组', 'BACKEND', 2, NULL, '后端开发团队', 2, TRUE, NOW(), NOW()),
(8, '测试组', 'QA', 2, NULL, '质量保证团队', 3, TRUE, NOW(), NOW()),
(9, '华北销售区', 'SALES_NORTH', 3, NULL, '华北地区销售', 1, TRUE, NOW(), NOW()),
(10, '华南销售区', 'SALES_SOUTH', 3, NULL, '华南地区销售', 2, TRUE, NOW(), NOW());

-- 更新用户表，为现有用户分配部门（如果有用户的话）
-- UPDATE users SET department_id = 2 WHERE role = 'ADMIN';
-- UPDATE users SET department_id = 6 WHERE username LIKE '%frontend%';
-- UPDATE users SET department_id = 7 WHERE username LIKE '%backend%';