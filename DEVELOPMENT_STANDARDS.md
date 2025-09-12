# Magic CRM 项目开发规范

## 1. 技术栈

### 1.1 前端技术栈
- **框架**: Vue 3.5.21 + Composition API
- **构建工具**: Vite 7.1.4
- **UI组件库**: Element Plus 2.11.1
- **状态管理**: Pinia 3.0.3
- **路由管理**: Vue Router 4.5.1
- **HTTP客户端**: Axios 1.11.0
- **图表库**: ECharts 6.0.0
- **样式预处理**: Sass 1.91.0

### 1.2 后端技术栈
- **框架**: Spring Boot 3.5.5
- **Java版本**: JDK 17
- **数据库**: PostgreSQL
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security + JWT
- **数据库迁移**: Flyway 11.7.2
- **构建工具**: Maven
- **开发工具**: Lombok, Spring Boot DevTools

### 1.3 数据库与中间件
- **主数据库**: PostgreSQL
- **JWT库**: JJWT 0.12.3
- **中文拼音**: Pinyin4j 2.5.1

## 2. 技术规范

### 2.1 目录结构约定

#### 前端目录结构
```
frontend/
├── src/
│   ├── api/                    # API接口定义
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   │   └── workflow/          # 工作流相关组件
│   ├── layout/                # 布局组件
│   │   └── components/        # 布局子组件
│   ├── router/                # 路由配置
│   ├── stores/                # Pinia状态管理
│   ├── styles/                # 全局样式
│   ├── utils/                 # 工具函数
│   └── views/                 # 页面组件
│       ├── dashboard/         # 仪表盘
│       ├── workflow/          # 工作流管理
│       ├── form/              # 表单管理
│       └── system/            # 系统管理
├── dist/                      # 构建输出
└── node_modules/              # 依赖包
```

#### 后端目录结构
```
backend/src/main/java/com/magic/crm/
├── config/                    # 配置类
├── controller/                # 控制器层
├── dto/                       # 数据传输对象
├── entity/                    # 实体类
├── exception/                 # 异常处理
├── interceptor/               # 拦截器
├── repository/                # 数据访问层
├── security/                  # 安全相关
├── service/                   # 业务逻辑层
└── util/                      # 工具类
```

### 2.2 环境配置要求
- **Node.js**: >= 16.0.0
- **Java**: JDK 17
- **PostgreSQL**: >= 17.0
- **Maven**: >= 3.6.0

### 2.3 依赖管理方式
- **前端**: npm/yarn + package.json
- **后端**: Maven + pom.xml
- **数据库**: Flyway迁移脚本

## 3. API接口规范

### 3.1 统一请求/响应格式

#### 请求格式
```javascript
// 整个项目使用POST方式， 不使用REST Api， 

// POST
{
  "field1": "value1",
  "field2": "value2"
}

// 请求命名方式
// 分页请求
/api/moduleName/list
{
  pageIndex,
  pageSize,
  filters:{...}
  sort:"排序要求",
}

// 查询单挑数据
/api/moduleName/get 
{id}

// 新增或修改单条数据
/api/moduleName/update
{
  id:0,//新增
  id:xxx,//修改
  ...//其它字段数据
}

// 删除数据
/api/moduleName/delete
{
  id:xxx
}

// 其它功能操作
/api/moduleName/functionName
{
  ...
}
```

#### 响应格式
```javascript
// 成功响应
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}

// 错误响应
{
  "code": 400,
  "success": false,
  "message": "错误信息",
  "error": "详细错误描述"
}

// 分页响应
{
  "code": 200,
  "success": true,
  "message": "查询成功",
  "data": {
    "totalCount": 100,
    "pageIndex": 0
    "pageCount": 10,
    "records": [...],
  }
}
```

### 3.2 错误码体系
- **200**: 成功
- **400**: 请求参数错误
- **401**: 未授权/登录失效
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

### 3.3 鉴权机制
- **认证方式**: JWT Bearer Token
- **Token位置**: HTTP Header `Authorization: Bearer <token>`
- **Token过期**: 24小时 (可刷新)
- **刷新Token**: 7天有效期

#### 前端请求拦截器
```javascript
// 自动添加Authorization头
config.headers['Authorization'] = `Bearer ${token}`
```

#### 后端安全配置
```java
@PreAuthorize("isAuthenticated()")  // 需要登录
@PreAuthorize("hasRole('ADMIN')")   // 需要管理员权限
```

## 4. 代码风格

### 4.1 前端代码规范

#### Vue组件规范
```vue
<template>
  <!-- 使用kebab-case命名 -->
  <div class="component-name">
    <el-button @click="handleClick">按钮</el-button>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'


// 使用Composition API
}
</script>

<style lang="scss" scoped>
.component-name {
  // 样式规则
}
</style>
```

#### JavaScript规范
- 使用ES6+语法
- 优先使用const/let，避免var
- 函数命名使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 文件名使用kebab-case

### 4.2 后端代码规范

最重要的： 按业务功能模块组织代码

#### Java类规范
```java
@Entity
@Table(name = "users")
@Data                    // Lombok注解
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    // 其他字段...
}
```

#### Controller规范
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/list")
    public PageResponse<User> getUsers(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize) {
        return userService.getUsers(pageIndex, pageSize);
    }
    
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> updateUser(@Valid @RequestBody User user) {
        return ApiResponse.success(userService.createUser(user));
    }
}
```

### 4.3 命名约定

#### 前端命名
- **组件**: PascalCase (UserProfile.vue)
- **文件**: kebab-case (user-profile.js)
- **变量/函数**: camelCase (getUserInfo)
- **常量**: UPPER_SNAKE_CASE (API_BASE_URL)
- **CSS类**: kebab-case (.user-profile)

#### 后端命名
- **类**: PascalCase (UserService)
- **方法**: camelCase (getUserById)
- **变量**: camelCase (userName)
- **常量**: UPPER_SNAKE_CASE (MAX_RETRY_COUNT)
- **数据库表**: snake_case (user_profiles)
- **数据库字段**: snake_case (created_at)

### 4.4 注释标准

#### 前端注释
```javascript
/**
 * 获取用户信息
 * @param {number} userId - 用户ID
 * @returns {Promise<Object>} 用户信息
 */
async function getUserInfo(userId) {
  // 实现逻辑
}
```

#### 后端注释
```java
/**
 * 用户服务类
 * 提供用户相关的业务逻辑处理
 * 
 * @author Magic Team
 * @since 1.0.0
 */
@Service
public class UserService {
    
    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     * @throws UserNotFoundException 用户不存在时抛出
     */
    public User getUserById(Long id) {
        // 实现逻辑
    }
}
```

## 5. 核心原则

### 5.1 设计模式

#### 前端设计模式
- **组合式API**: 优先使用Composition API
- **状态管理**: 使用Pinia进行全局状态管理
- **组件通信**: Props down, Events up
- **路由守卫**: 统一权限控制
- **请求拦截**: 统一错误处理和Loading状态

#### 后端设计模式
- **分层架构**: Controller -> Service -> Repository
- **依赖注入**: 使用Spring的@Autowired
- **AOP切面**: 统一异常处理、日志记录
- **Builder模式**: 实体类构建
- **策略模式**: 工作流处理逻辑

### 5.2 性能优化点

#### 前端性能优化
- **路由懒加载**: 使用动态import
- **组件缓存**: keep-alive缓存页面组件
- **图片优化**: 使用WebP格式，懒加载
- **打包优化**: Vite自动代码分割
- **请求优化**: 防抖节流，请求缓存

#### 后端性能优化
- **数据库优化**: 合理使用索引，避免N+1查询
- **缓存策略**: 使用Spring Cache
- **连接池**: HikariCP数据库连接池
- **异步处理**: @Async异步任务
- **分页查询**: 统一分页参数

### 5.3 安全注意事项

#### 前端安全
- **XSS防护**: 使用v-html时注意内容过滤
- **CSRF防护**: 使用CSRF Token
- **敏感信息**: 不在前端存储敏感数据
- **路由权限**: 前后端双重权限验证

#### 后端安全
- **SQL注入**: 使用参数化查询
- **权限控制**: 方法级权限注解
- **密码加密**: BCrypt加密存储
- **JWT安全**: 合理设置过期时间
- **输入验证**: 使用@Valid注解验证

### 5.4 工作流特有规范

#### BPMN流程设计
- **命名规范**: 流程名称使用中文，流程Key使用英文
- **节点命名**: 任务节点使用动词+名词
- **网关使用**: 合理使用排他网关和并行网关
- **表单集成**: 流程节点与表单模板关联

#### Flowable集成
- **流程部署**: 统一通过API部署BPMN文件
- **任务处理**: 使用统一的任务处理接口
- **历史记录**: 开启完整历史记录
- **权限控制**: 集成Spring Security权限体系

## 6. 开发工作流

### 6.1 Git工作流
- **分支策略**: GitFlow (main/develop/feature/hotfix)
- **提交规范**: 使用Conventional Commits
- **代码审查**: Pull Request必须经过审查

### 6.2 部署流程
- **环境隔离**: dev/test/prod环境配置
- **数据库迁移**: 使用Flyway自动迁移
- **配置管理**: 环境变量配置敏感信息

### 6.3 测试规范
- **单元测试**: 业务逻辑必须有单元测试
- **集成测试**: API接口集成测试
- **E2E测试**: 关键业务流程端到端测试

## 7. 项目特有配置

### 7.1 代理配置
```javascript
// 前端开发代理
proxy: {
  '/api': 'http://localhost:8080',
  '/uploads': 'http://localhost:8080'
}
```

### 7.2 文件上传配置
```yaml
# 后端文件配置
file:
  dm-path: E:/luzj/magic_crm/dm/
  upload:
    path: E:/luzj/magic_crm/uploads/
```

### 7.3 工作流配置
```yaml
# Flowable配置
flowable:
  database-schema-update: true
  history-level: full
  rest:
    enabled: false  # 禁用内置REST API
```

## 8. 常用工具类和组件

### 8.1 前端工具
- **request.js**: 统一HTTP请求封装
- **auth.js**: 认证相关工具函数
- **SearchContainer.vue**: 通用搜索容器组件

### 8.2 后端工具
- **ApiResponse**: 统一响应格式封装
- **JwtUtil**: JWT工具类
- **PinyinUtil**: 中文拼音转换工具
- **BeanCopyUtils**: 对象拷贝工具

---

**注意**: 此规范文档应作为项目开发的基础参考，所有开发人员和AI助手都应严格遵循这些规范，确保代码质量和项目一致性。