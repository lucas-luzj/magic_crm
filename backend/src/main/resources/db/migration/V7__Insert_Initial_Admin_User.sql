-- =====================================================
-- 初始化管理员用户数据
-- 创建时间: 2025-09-06
-- 描述: 创建系统初始管理员用户，用户名admin，密码admin123
-- =====================================================

-- 首先检查users表是否存在，如果不存在则创建
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar VARCHAR(500),
    photo VARCHAR(500),
    birthday DATE,
    last_login_ip VARCHAR(45),
    last_login_user_agent VARCHAR(1000),
    position VARCHAR(100),
    wechat_union_id VARCHAR(100),
    wechat_open_id VARCHAR(100),
    dingtalk_id VARCHAR(100),
    qq_number VARCHAR(20),
    hire_date DATE,
    resignation_date DATE,
    gender INTEGER,
    hometown VARCHAR(200),
    graduate_school VARCHAR(200),
    address VARCHAR(500),
    emergency_contact VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    remarks VARCHAR(1000),
    last_login_at TIMESTAMP,
    department_id BIGINT,
    name_pinyin VARCHAR(100)
);

-- 添加约束（如果不存在）
DO $$
BEGIN
    -- 检查并添加role枚举约束
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.check_constraints 
        WHERE constraint_name = 'users_role_check'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT users_role_check 
        CHECK (role IN ('ADMIN', 'MANAGER', 'USER'));
    END IF;
END $$;

-- 创建索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_department_id ON users(department_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- 插入初始管理员用户
-- 密码: admin123 (BCrypt加密)
INSERT INTO users (
    username,
    email,
    password,
    full_name,
    role,
    is_active,
    created_at,
    updated_at,
    position,
    name_pinyin
) VALUES (
    'admin',
    'admin@magic-crm.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',  -- admin123的BCrypt哈希
    '系统管理员',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    '系统管理员',
    'XTGLY'
) ON CONFLICT (username) DO NOTHING;

-- 插入一个普通用户示例
INSERT INTO users (
    username,
    email,
    password,
    full_name,
    role,
    is_active,
    created_at,
    updated_at,
    position,
    name_pinyin
) VALUES (
    'user001',
    'user001@magic-crm.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',  -- admin123的BCrypt哈希
    '测试用户',
    'USER',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    '普通员工',
    'CSYH'
) ON CONFLICT (username) DO NOTHING;

-- 创建更新时间触发器函数（如果不存在）
CREATE OR REPLACE FUNCTION update_users_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器（如果不存在）
DROP TRIGGER IF EXISTS trigger_update_users_updated_at ON users;
CREATE TRIGGER trigger_update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_users_updated_at();

-- 添加表注释
COMMENT ON TABLE users IS '用户表 - 存储系统用户信息';
COMMENT ON COLUMN users.id IS '用户ID，主键';
COMMENT ON COLUMN users.username IS '用户名，唯一';
COMMENT ON COLUMN users.email IS '邮箱地址，唯一';
COMMENT ON COLUMN users.password IS 'BCrypt加密的密码';
COMMENT ON COLUMN users.full_name IS '用户全名';
COMMENT ON COLUMN users.role IS '用户角色：ADMIN-管理员，MANAGER-经理，USER-普通用户';
COMMENT ON COLUMN users.is_active IS '是否激活';
COMMENT ON COLUMN users.department_id IS '所属部门ID';
COMMENT ON COLUMN users.name_pinyin IS '姓名拼音首字母';

-- 输出初始化结果
DO $$
DECLARE
    admin_count INTEGER;
    user_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO admin_count FROM users WHERE username = 'admin';
    SELECT COUNT(*) INTO user_count FROM users WHERE role = 'USER';
    
    RAISE NOTICE '=== 用户初始化完成 ===';
    RAISE NOTICE '管理员用户数量: %', admin_count;
    RAISE NOTICE '普通用户数量: %', user_count;
    RAISE NOTICE '默认管理员账号: admin';
    RAISE NOTICE '默认密码: admin123';
    RAISE NOTICE '请登录后及时修改默认密码！';
    RAISE NOTICE '========================';
END $$;