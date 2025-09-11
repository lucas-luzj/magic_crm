-- 角色与权限管理系统数据库迁移脚本
-- 版本: V8
-- 描述: 创建角色、菜单权限、用户角色关联、角色权限关联表

-- 创建角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE sys_role IS '系统角色表';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色代码';
COMMENT ON COLUMN sys_role.description IS '角色描述';
COMMENT ON COLUMN sys_role.status IS '状态：true-启用，false-禁用';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.create_by IS '创建人';
COMMENT ON COLUMN sys_role.update_by IS '更新人';
COMMENT ON COLUMN sys_role.deleted IS '删除标记';

-- 创建菜单权限表（支持树形结构）
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type VARCHAR(20) NOT NULL,
    path VARCHAR(255),
    component VARCHAR(255),
    permission_code VARCHAR(100),
    icon VARCHAR(50),
    sort_order INTEGER DEFAULT 0,
    status BOOLEAN DEFAULT TRUE,
    is_public BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE sys_menu IS '系统菜单权限表';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID，0表示顶级菜单';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型：MENU-菜单，BUTTON-按钮';
COMMENT ON COLUMN sys_menu.path IS '路由路径';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.permission_code IS '权限标识符';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.sort_order IS '排序顺序';
COMMENT ON COLUMN sys_menu.status IS '状态：true-启用，false-禁用';
COMMENT ON COLUMN sys_menu.is_public IS '是否公共权限';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_menu.create_by IS '创建人';
COMMENT ON COLUMN sys_menu.update_by IS '更新人';
COMMENT ON COLUMN sys_menu.deleted IS '删除标记';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_user_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_user_role.create_by IS '创建人';
COMMENT ON COLUMN sys_user_role.update_by IS '更新人';
COMMENT ON COLUMN sys_user_role.deleted IS '删除标记';

-- 创建角色菜单权限关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色菜单权限关联表';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单权限ID';
COMMENT ON COLUMN sys_role_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_role_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_role_menu.create_by IS '创建人';
COMMENT ON COLUMN sys_role_menu.update_by IS '更新人';
COMMENT ON COLUMN sys_role_menu.deleted IS '删除标记';

-- 创建索引以提高查询性能
CREATE INDEX idx_sys_role_role_code ON sys_role(role_code);
CREATE INDEX idx_sys_role_status ON sys_role(status);
CREATE INDEX idx_sys_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_sys_menu_menu_type ON sys_menu(menu_type);
CREATE INDEX idx_sys_menu_permission_code ON sys_menu(permission_code);
CREATE INDEX idx_sys_menu_status ON sys_menu(status);
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);
CREATE INDEX idx_sys_role_menu_role_id ON sys_role_menu(role_id);
CREATE INDEX idx_sys_role_menu_menu_id ON sys_role_menu(menu_id);

-- 插入初始数据：系统管理员角色（如果不存在）
INSERT INTO sys_role (role_name, role_code, description, status, create_by, update_by, deleted)
SELECT * FROM (VALUES 
('系统管理员', 'ROLE_ADMIN', '拥有系统所有权限', true, 1, 1, false),
('普通用户', 'ROLE_USER', '普通用户权限', true, 1, 1, false),
('部门经理', 'ROLE_MANAGER', '部门经理权限', true, 1, 1, false)
) AS t(role_name, role_code, description, status, create_by, update_by, deleted)
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = t.role_code);

-- 插入初始菜单权限数据（如果不存在）
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission_code, icon, sort_order, status, is_public, create_by, update_by, deleted)
SELECT * FROM (VALUES 
-- 系统管理菜单
(0, '系统管理', 'MENU', '/system', 'Layout', 'system:manage', 'el-icon-s-tools', 100, true, false, 1, 1, false),
-- 用户管理
(1, '用户管理', 'MENU', '/system/user', 'system/user/index', 'system:user:view', 'el-icon-user', 101, true, false, 1, 1, false),
(2, '新增用户', 'BUTTON', NULL, NULL, 'system:user:add', NULL, 1, true, false, 1, 1, false),
(2, '编辑用户', 'BUTTON', NULL, NULL, 'system:user:edit', NULL, 2, true, false, 1, 1, false),
(2, '删除用户', 'BUTTON', NULL, NULL, 'system:user:delete', NULL, 3, true, false, 1, 1, false),
-- 角色管理
(1, '角色管理', 'MENU', '/system/role', 'system/role/index', 'system:role:view', 'el-icon-s-custom', 102, true, false, 1, 1, false),
(6, '新增角色', 'BUTTON', NULL, NULL, 'system:role:add', NULL, 1, true, false, 1, 1, false),
(6, '编辑角色', 'BUTTON', NULL, NULL, 'system:role:edit', NULL, 2, true, false, 1, 1, false),
(6, '删除角色', 'BUTTON', NULL, NULL, 'system:role:delete', NULL, 3, true, false, 1, 1, false),
(6, '分配权限', 'BUTTON', NULL, NULL, 'system:role:assign', NULL, 4, true, false, 1, 1, false),
-- 权限管理
(1, '权限管理', 'MENU', '/system/menu', 'system/menu/index', 'system:menu:view', 'el-icon-menu', 103, true, false, 1, 1, false),
(11, '新增权限', 'BUTTON', NULL, NULL, 'system:menu:add', NULL, 1, true, false, 1, 1, false),
(11, '编辑权限', 'BUTTON', NULL, NULL, 'system:menu:edit', NULL, 2, true, false, 1, 1, false),
(11, '删除权限', 'BUTTON', NULL, NULL, 'system:menu:delete', NULL, 3, true, false, 1, 1, false),

-- 工作流菜单
(0, '工作流程', 'MENU', '/process', 'Layout', 'process:manage', 'el-icon-s-operation', 200, true, false, 1, 1, false),
(15, '流程设计', 'MENU', '/process/design', 'process/design/index', 'process:design:view', 'el-icon-edit', 201, true, false, 1, 1, false),
(15, '我的待办', 'MENU', '/process/todo', 'process/todo/index', 'process:todo:view', 'el-icon-document', 202, true, false, 1, 1, false),
(15, '已办任务', 'MENU', '/process/done', 'process/done/index', 'process:done:view', 'el-icon-finished', 203, true, false, 1, 1, false),
(15, '发起流程', 'MENU', '/process/start', 'process/start/index', 'process:start:view', 'el-icon-circle-plus', 204, true, false, 1, 1, false),

-- CRM菜单
(0, '客户管理', 'MENU', '/crm', 'Layout', 'crm:manage', 'el-icon-office-building', 300, true, false, 1, 1, false),
(20, '客户列表', 'MENU', '/crm/customer', 'crm/customer/index', 'crm:customer:view', 'el-icon-notebook-2', 301, true, false, 1, 1, false),
(20, '新增客户', 'BUTTON', NULL, NULL, 'crm:customer:add', NULL, 1, true, false, 1, 1, false),
(20, '编辑客户', 'BUTTON', NULL, NULL, 'crm:customer:edit', NULL, 2, true, false, 1, 1, false),
(20, '删除客户', 'BUTTON', NULL, NULL, 'crm:customer:delete', NULL, 3, true, false, 1, 1, false),

-- 公共页面（登录页等）
(0, '登录', 'MENU', '/login', 'auth/login/index', 'auth:login:view', NULL, 999, true, true, 1, 1, false),
(0, '首页', 'MENU', '/dashboard', 'dashboard/index', 'dashboard:view', 'el-icon-house', 1, true, false, 1, 1, false)
) AS t(parent_id, menu_name, menu_type, path, component, permission_code, icon, sort_order, status, is_public, create_by, update_by, deleted)
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE permission_code = t.permission_code);

-- 为管理员角色分配所有权限（如果不存在）
INSERT INTO sys_role_menu (role_id, menu_id, create_by, update_by, deleted)
SELECT 
    r.id as role_id,
    m.id as menu_id,
    1 as create_by,
    1 as update_by,
    false as deleted
FROM sys_role r
CROSS JOIN sys_menu m
WHERE r.role_code = 'ROLE_ADMIN'
AND m.deleted = false
AND NOT EXISTS (SELECT 1 FROM sys_role_menu srm WHERE srm.role_id = r.id AND srm.menu_id = m.id);

-- 为初始管理员用户（ID=1）分配管理员角色（如果不存在）
INSERT INTO sys_user_role (user_id, role_id, create_by, update_by, deleted)
SELECT 1, 1, 1, 1, false
WHERE NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = 1 AND role_id = 1);

-- 更新表注释
COMMENT ON TABLE sys_role IS '系统角色表，存储系统角色信息';
COMMENT ON TABLE sys_menu IS '系统菜单权限表，支持树形结构的权限管理';
COMMENT ON TABLE sys_user_role IS '用户角色关联表，管理用户与角色的多对多关系';
COMMENT ON TABLE sys_role_menu IS '角色菜单权限关联表，管理角色与权限的多对多关系';