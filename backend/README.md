# Magic CRM Backend

基于Spring Boot 3.2.12的CRM系统后端服务，使用JWT认证和PostgreSQL数据库。

## 技术栈

- **框架**: Spring Boot 3.2.12
- **数据库**: PostgreSQL
- **ORM**: Spring Data JPA
- **认证**: JWT (JSON Web Token)
- **安全**: Spring Security
- **构建工具**: Maven
- **Java版本**: 17

## 功能特性

- ✅ JWT认证和授权
- ✅ 用户注册和登录
- ✅ 角色权限管理
- ✅ RESTful API设计
- ✅ 全局异常处理
- ✅ 参数验证
- ✅ CORS跨域支持
- ✅ 数据库自动建表

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### 2. 数据库配置

创建PostgreSQL数据库：

```sql
CREATE DATABASE magic_crm;
CREATE USER magic_user WITH PASSWORD 'magic_password';
GRANT ALL PRIVILEGES ON DATABASE magic_crm TO magic_user;
```

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/magic_crm
    username: magic_user
    password: magic_password
```

### 4. 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API文档

### 认证接口

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "新用户",
  "phoneNumber": "13800138000"
}
```

#### 刷新令牌
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### 退出登录
```http
POST /api/auth/logout
Authorization: Bearer your-jwt-token
```

### 用户接口

#### 获取当前用户信息
```http
GET /api/users/profile
Authorization: Bearer your-jwt-token
```

#### 检查用户名是否可用
```http
GET /api/users/check-username/{username}
```

#### 检查邮箱是否可用
```http
GET /api/users/check-email/{email}
```

## 默认用户

系统初始化时会创建以下默认用户：

| 用户名 | 密码 | 角色 | 邮箱 |
|--------|------|------|------|
| admin | admin123 | ADMIN | admin@magic-crm.com |
| testuser | user123 | USER | test@magic-crm.com |

## 项目结构

```
src/main/java/com/magic/crm/
├── MagicCrmApplication.java          # 启动类
├── config/                           # 配置类
│   ├── JwtProperties.java           # JWT配置属性
│   └── SecurityConfig.java          # 安全配置
├── controller/                       # 控制器
│   ├── AuthController.java          # 认证控制器
│   └── UserController.java          # 用户控制器
├── dto/                             # 数据传输对象
│   ├── ApiResponse.java             # 统一响应格式
│   ├── AuthResponse.java            # 认证响应
│   ├── LoginRequest.java            # 登录请求
│   └── RegisterRequest.java         # 注册请求
├── entity/                          # 实体类
│   └── User.java                    # 用户实体
├── exception/                       # 异常处理
│   └── GlobalExceptionHandler.java  # 全局异常处理器
├── repository/                      # 数据访问层
│   └── UserRepository.java         # 用户仓库
├── security/                        # 安全相关
│   ├── JwtAuthenticationEntryPoint.java  # JWT认证入口点
│   └── JwtAuthenticationFilter.java      # JWT认证过滤器
├── service/                         # 服务层
│   ├── AuthService.java             # 认证服务
│   └── UserService.java             # 用户服务
└── util/                           # 工具类
    └── JwtUtil.java                # JWT工具类
```

## 开发指南

### 添加新的实体

1. 在 `entity` 包下创建实体类
2. 在 `repository` 包下创建对应的Repository接口
3. 在 `service` 包下创建服务类
4. 在 `controller` 包下创建控制器
5. 在 `dto` 包下创建相关的DTO类

### 权限控制

使用 `@PreAuthorize` 注解进行方法级权限控制：

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyMethod() {
    // 只有ADMIN角色可以访问
}

@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
public ResponseEntity<?> ownerOrAdminMethod(@PathVariable Long id) {
    // ADMIN角色或资源所有者可以访问
}
```

## 部署

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/crm-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 环境变量

生产环境建议使用环境变量配置敏感信息：

```bash
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret_key
```

## 许可证

MIT License