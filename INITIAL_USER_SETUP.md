# 初始用户设置说明

## 概述
系统已自动创建初始管理员用户，用于首次登录和系统配置。

## 默认管理员账号
- **用户名**: `admin`
- **密码**: `admin123`
- **邮箱**: `admin@magic-crm.com`
- **角色**: `ADMIN` (系统管理员)

## 默认测试用户
- **用户名**: `user001`
- **密码**: `admin123`
- **邮箱**: `user001@magic-crm.com`
- **角色**: `USER` (普通用户)

## 数据库迁移文件
初始用户数据通过Flyway迁移文件自动创建：
```
backend/src/main/resources/db/migration/V7__Insert_Initial_Admin_User.sql
```

## 首次登录步骤

### 1. 启动系统
```bash
# 启动后端服务
cd backend
mvn spring-boot:run

# 启动前端服务
cd frontend
npm run dev
```

### 2. 访问系统
- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080/api

### 3. 登录系统
1. 打开浏览器访问 http://localhost:3000
2. 在登录页面输入：
   - 用户名: `admin`
   - 密码: `admin123`
3. 点击登录

### 4. 修改默认密码 (重要!)
登录成功后，请立即修改默认密码：
1. 点击右上角用户头像
2. 选择"个人中心"或"修改密码"
3. 输入新密码并确认
4. 保存更改

## 安全注意事项

### ⚠️ 重要提醒
1. **立即修改默认密码** - 默认密码仅用于首次登录
2. **删除测试用户** - 生产环境中应删除或禁用测试用户
3. **设置强密码策略** - 建议密码包含大小写字母、数字和特殊字符
4. **定期更新密码** - 建议定期更换管理员密码

### 密码要求
- 最小长度: 6位
- 建议包含: 大写字母、小写字母、数字、特殊字符
- 避免使用: 常见密码、个人信息相关密码

## 用户管理

### 创建新用户
1. 使用管理员账号登录
2. 进入"用户管理"页面
3. 点击"添加用户"
4. 填写用户信息并设置角色
5. 保存用户

### 用户角色说明
- **ADMIN**: 系统管理员，拥有所有权限
- **MANAGER**: 部门经理，拥有部门管理权限
- **USER**: 普通用户，拥有基本操作权限

## 故障排除

### 登录失败
1. 检查用户名和密码是否正确
2. 确认用户账号是否激活 (`is_active = true`)
3. 检查数据库连接是否正常
4. 查看后端日志是否有错误信息

### 密码重置
如果忘记管理员密码，可以通过以下方式重置：

#### 方法1: 数据库直接更新
```sql
-- 将admin密码重置为admin123
UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE username = 'admin';
```

#### 方法2: 重新运行迁移
```bash
cd backend
mvn flyway:clean
mvn flyway:migrate
```

## 数据库结构
用户表主要字段：
- `id`: 用户ID (主键)
- `username`: 用户名 (唯一)
- `email`: 邮箱地址 (唯一)
- `password`: BCrypt加密密码
- `role`: 用户角色 (ADMIN/MANAGER/USER)
- `is_active`: 是否激活
- `department_id`: 所属部门ID
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 相关文件
- 迁移文件: `backend/src/main/resources/db/migration/V7__Insert_Initial_Admin_User.sql`
- 用户实体: `backend/src/main/java/com/magic/crm/entity/User.java`
- 认证服务: `backend/src/main/java/com/magic/crm/service/AuthService.java`
- 安全配置: `backend/src/main/java/com/magic/crm/config/SecurityConfig.java`

---

**注意**: 这是开发环境的初始设置。在生产环境部署时，请确保：
1. 使用强密码
2. 启用HTTPS
3. 配置适当的安全策略
4. 定期备份用户数据