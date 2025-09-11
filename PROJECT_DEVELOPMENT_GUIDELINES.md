# Magic CRM 项目开发规范

## 1. 技术栈

### 前端技术栈
- **框架**: Vue 3 + Composition API
- **UI组件库**: Element Plus
- **构建工具**: Vite
- **流程设计器**: BPMN.js + 自定义属性面板
- **状态管理**: Vuex/Pinia（待引入）
- **路由**: Vue Router
- **HTTP客户端**: Axios

### 后端技术栈
- **语言**: Java 17
- **框架**: Spring Boot 3.x
- **工作流引擎**: Flowable 7
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **构建工具**: Maven
- **日志**: SLF4J + Logback

### 开发工具
- **IDE**: VS Code（前端）, IntelliJ IDEA（后端）
- **版本控制**: Git
- **包管理**: npm（前端）, Maven（后端）

## 2. 技术规范

### 目录结构约定
```
magic_crm/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/magic/crm/
│   │   │   │       ├── controller/    # 控制器层
│   │   │   │       ├── service/       # 服务层
│   │   │   │       ├── repository/    # 数据访问层
│   │   │   │       ├── entity/        # 实体类
│   │   │   │       ├── dto/           # 数据传输对象
│   │   │   │       └── exception/     # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml    # 配置文件
│   │   │       └── static/            # 静态资源
│   │   └── test/                      # 测试代码
│   └── pom.xml                        # Maven配置
└── frontend/                # 前端项目
    ├── src/
    │   ├── api/             # API接口
    │   ├── components/      # 组件
    │   ├── views/           # 页面视图
    │   ├── router/          # 路由配置
    │   ├── stores/          # 状态管理
    │   ├── utils/           # 工具函数
    │   └── assets/          # 静态资源
    ├── package.json         # 依赖配置
    └── vite.config.ts      # Vite配置
```

### 环境配置要求
- **Node.js**: >= 16.0.0
- **Java**: 17
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 依赖管理
- 前端依赖在package.json中管理，使用npm install安装
- 后端依赖在pom.xml中管理，使用Maven管理
- 禁止引入未经审查的第三方依赖

## 3. API接口规范

### 请求格式
```json
{
  "page": 1,
  "size": 10,
  "filters": {...}
}
```

### 响应格式
```json
{
  "success": true,
  "data": {...},
  "totalCount": 100,
  "pageIndex": 1,
  "pageSize": 10,
  "message": "操作成功"
}
```

### 错误码体系
- **200**: 成功
- **400**: 请求参数错误
- **401**: 未授权
- **403**: 禁止访问
- **404**: 资源不存在
- **500**: 服务器内部错误

### 统一异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        // 统一异常处理逻辑
    }
}
```

## 4. 代码风格

### 命名约定
- **Java**: 驼峰命名法（类名PascalCase，方法名camelCase）
- **Vue**: 组件名PascalCase，变量名camelCase
- **数据库**: 表名和字段名使用蛇形命名法（snake_case）

### 注释标准
- 类和方法使用Javadoc注释
- 复杂逻辑添加行内注释
- TODO注释标记待完成功能

### Lint规则
- 前端: ESLint + Prettier
- 后端: Checkstyle + SpotBugs
- 提交前必须通过代码检查

## 5. 核心原则

### 设计模式
- **分层架构**: Controller-Service-Repository
- **依赖注入**: Spring IOC容器管理
- **面向接口编程**: 服务层定义接口
- **响应式编程**: Vue 3 Composition API

### 性能优化
- 数据库查询使用分页
- 流程实例状态缓存
- 前端组件懒加载
- 图片和资源压缩

### 安全注意事项
- SQL注入防护：使用预编译语句
- XSS防护：输入输出过滤
- CSRF防护：Token验证
- 变量名合法性验证：正则表达式检查
- 敏感信息加密存储

### Flowable工作流规范
- **变量命名**: 支持中文变量名（已验证）
- **流程状态**: 运行中(RUNNING)、暂停(SUSPENDED)、终止(TERMINATED)
- **异常处理**: 统一日志记录和异常抛出
- **事务管理**: @Transactional注解确保数据一致性

### 前端工作流组件规范
- 变量提取支持复杂表达式
- 表单配置包含键和类型
- 变量修改同步更新所有引用
- 支持中文变量名（与后端Flowable 7兼容）

## 6. 开发流程

1. **需求分析**: 明确功能需求和业务逻辑
2. **技术设计**: 设计API接口和数据库表结构
3. **编码实现**: 遵循上述规范和原则
4. **代码审查**: 团队成员交叉审查
5. **测试验证**: 单元测试和集成测试
6. **部署上线**: 自动化部署流程

## 7. 版本控制

- 功能分支开发，主分支保护
- 提交信息规范: feat/fix/docs/style/refactor/test
- 定期合并和解决冲突

---

*最后更新: 2025-09-06*
*版本: 1.0.0*