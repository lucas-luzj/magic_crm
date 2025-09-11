-- 创建部门表（如果不存在）
CREATE TABLE IF NOT EXISTS departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE,
    parent_id BIGINT,
    manager_id BIGINT,
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- 添加字段注释
COMMENT ON TABLE departments IS '部门表';
COMMENT ON COLUMN departments.name IS '部门名称';
COMMENT ON COLUMN departments.code IS '部门编码';
COMMENT ON COLUMN departments.parent_id IS '父部门ID';
COMMENT ON COLUMN departments.manager_id IS '部门负责人ID';
COMMENT ON COLUMN departments.description IS '部门描述';
COMMENT ON COLUMN departments.sort_order IS '排序号';
COMMENT ON COLUMN departments.is_active IS '部门状态：true-启用，false-禁用';
COMMENT ON COLUMN departments.created_at IS '创建时间';
COMMENT ON COLUMN departments.updated_at IS '更新时间';
COMMENT ON COLUMN departments.created_by IS '创建人ID';
COMMENT ON COLUMN departments.updated_by IS '更新人ID';

-- 创建索引
CREATE INDEX idx_departments_parent_id ON departments(parent_id);
CREATE INDEX idx_departments_manager_id ON departments(manager_id);
CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_departments_sort_order ON departments(sort_order);

-- 为用户表添加部门关联字段和拼音字段（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'department_id') THEN
        ALTER TABLE users ADD COLUMN department_id BIGINT;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'name_pinyin') THEN
        ALTER TABLE users ADD COLUMN name_pinyin VARCHAR(100);
    END IF;
END $$;

-- 添加字段注释
COMMENT ON COLUMN users.department_id IS '部门ID';
COMMENT ON COLUMN users.name_pinyin IS '用户姓名拼音首字母';

-- 创建索引（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_users_department_id') THEN
        CREATE INDEX idx_users_department_id ON users(department_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_users_name_pinyin') THEN
        CREATE INDEX idx_users_name_pinyin ON users(name_pinyin);
    END IF;
END $$;

-- 添加外键约束（可选，根据需要启用）
-- ALTER TABLE users ADD CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(id);
-- ALTER TABLE departments ADD CONSTRAINT fk_departments_parent FOREIGN KEY (parent_id) REFERENCES departments(id);
-- ALTER TABLE departments ADD CONSTRAINT fk_departments_manager FOREIGN KEY (manager_id) REFERENCES users(id);

-- 插入默认根部门
INSERT INTO departments (name, code, description, sort_order, is_active, created_at, updated_at) 
VALUES ('总公司', 'ROOT', '根部门', 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);